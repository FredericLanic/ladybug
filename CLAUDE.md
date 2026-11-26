# Ladybug : règles pour Claude

Jeu type Pacman en Java 21 / Swing / Spring Boot (`com.kycox.game`), en cours de modernisation.
Le skill `ladybug-modernisation` donne l'état du chantier et où trouver le détail.

## Règles de travail
- **Discuter avant d'agir** : aucune branche, build, modification ou commit sans un feu vert explicite (« vas-y »). La lecture reste permise.
- Une branche par étape (`etape-N-sujet`, depuis `develop` à jour), un commit par sous-étape, diff montré avant chaque commit, PR vers `develop` en merge commit.
- Répondre **en français**, vocabulaire accessible. Code en anglais, commentaires et Javadoc en français.
- Ne jamais toucher à `~/.m2/settings.xml` ni lire les clés privées.

## Économie de tokens (demandé par Frédéric)
- Ne lire que ce qui sert à la tâche : utiliser `grep`/`Glob` et lire des **extraits** (offset/limit), pas des fichiers entiers.
- Plan : lire seulement `.claude/skills/ladybug-modernisation/references/plan/<étape en cours>.md`.
- Journal : ne jamais le relire en entier ; chercher avec `grep -rn "mot" .claude/skills/ladybug-modernisation/references/journal/`.
- Ajouter au journal **sans le lire** : `cat >> .claude/skills/ladybug-modernisation/references/journal/AAAA-MM-JJ.md <<'EOF' … EOF`, quelques lignes seulement.
- Commandes bavardes : `mvn -q …` ; rediriger les longues sorties dans un fichier (`> /tmp/sortie.log 2>&1`) et n'en lire que la fin (`tail -40`).
- Dashboard, pages, PDF et sauvegarde : **seulement** quand Frédéric lance `/ladybug-cloture`.
- Graphify et Context7 : seulement sur demande, ou Context7 avant une montée de version.

## Build
- `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` avant `mvn` (Java 25 par défaut casse Lombok).
- Graphify : toujours `GRAPHIFY_BACKEND=none graphify …`.
