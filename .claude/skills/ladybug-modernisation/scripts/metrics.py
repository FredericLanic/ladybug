#!/usr/bin/env python3
"""Mesure l'état du code de Ladybug pour le dashboard.

Usage : python3 metrics.py <racine du dépôt>
Affiche un JSON {packages: [...], health: [...], at: "AAAA-MM-JJ"} sur la sortie standard.
Lecture seule : ne modifie aucun fichier.
"""
import datetime
import json
import pathlib
import re
import subprocess
import sys

root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else ".").resolve()
base = root / "src/main/java/com/kycox/game"

# Classes et lignes par package de premier niveau sous com.kycox.game
packages = {}
for f in base.rglob("*.java"):
    rel = f.relative_to(base).parts
    pkg = rel[0] if len(rel) > 1 else "(racine)"
    lines = sum(1 for _ in f.open(encoding="utf-8", errors="replace"))
    p = packages.setdefault(pkg, {"package": pkg, "classes": 0, "lines": 0})
    p["classes"] += 1
    p["lines"] += lines
pkg_rows = sorted(packages.values(), key=lambda r: -r["lines"])

pom = (root / "pom.xml").read_text(encoding="utf-8")
def pom_value(pattern):
    m = re.search(pattern, pom, re.S)
    return m.group(1).strip() if m else "?"

java_version = pom_value(r"<java\.version>([^<]+)</java\.version>")
spring_boot = pom_value(r"spring-boot-starter-parent</artifactId>\s*<version>([^<]+)</version>")
project_version = pom_value(r"<artifactId>ladybug</artifactId>\s*<version>([^<]+)</version>")
tests = len(list((root / "src/test").rglob("*.java"))) if (root / "src/test").exists() else 0
ghost_subclasses = len([f for f in (base / "body/ghost").glob("*.java")
                        if re.search(r"extends\s+Ghost\b", f.read_text(encoding="utf-8"))])
observable_users = len([f for f in base.rglob("*.java")
                        if re.search(r"\bjava\.util\.(Observable|Observer)\b|\bObservable\b|\bObserver\b",
                                     f.read_text(encoding="utf-8", errors="replace"))])
try:
    commit = subprocess.run(["git", "-C", str(root), "log", "-1", "--format=%h %ad", "--date=short"],
                            capture_output=True, text=True, check=True).stdout.strip()
except Exception:
    commit = "?"

total_classes = sum(r["classes"] for r in pkg_rows)
total_lines = sum(r["lines"] for r in pkg_rows)

health = [
    {"metric": "Classes Java", "value": total_classes, "unit": "classes", "target": "", "state": "info"},
    {"metric": "Lignes de code", "value": total_lines, "unit": "lignes", "target": "", "state": "info"},
    {"metric": "Tests automatisés", "value": tests, "unit": "fichiers", "target": "> 0 à l'étape 3", "state": "bad" if tests == 0 else "ok"},
    {"metric": "Sous-classes de fantômes", "value": ghost_subclasses, "unit": "classes", "target": "0 à l'étape 6", "state": "warn" if ghost_subclasses else "ok"},
    {"metric": "Classes utilisant Observable/Observer", "value": observable_users, "unit": "classes", "target": "0 à l'étape 4", "state": "warn" if observable_users else "ok"},
    {"metric": "Version Java (pom)", "value": java_version, "unit": "", "target": "21 à l'étape 1", "state": "ok" if java_version == "21" else "warn"},
    {"metric": "Spring Boot", "value": spring_boot, "unit": "", "target": "3.5.x à l'étape 1", "state": "ok" if spring_boot.startswith("3.5") else "warn"},
    {"metric": "Version du projet", "value": project_version, "unit": "", "target": "", "state": "info"},
    {"metric": "Dernier commit", "value": commit, "unit": "", "target": "", "state": "info"},
]

print(json.dumps({"at": datetime.date.today().isoformat(), "packages": pkg_rows, "health": health},
                 ensure_ascii=False, indent=1))
