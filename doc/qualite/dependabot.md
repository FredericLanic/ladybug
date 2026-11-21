# Dependabot : les failles connues des bibliothèques utilisées

> Service intégré à GitHub, gratuit. Actif sur le dépôt Ladybug (alertes de sécurité et mises à jour de sécurité). Pages : onglet « Security » du dépôt, puis « Dependabot ».

**🟢 L'idée en une image (junior)**

Un constructeur automobile rappelle un modèle quand il découvre un défaut de freins. Encore faut-il que le garage sache quelle voiture vous avez.

Ladybug utilise des dizaines de bibliothèques (Spring Boot, Lombok, Jamepad, Log4j…). Quand une faille est découverte dans l'une d'elles, elle est publiée dans une base mondiale. Dependabot connaît la liste des bibliothèques de Ladybug, la compare à cette base et prévient : « cette version de Log4j a une faille, passez à telle version ».

**🔵 Dans Ladybug (confirmé)**

Dependabot lit la liste des dépendances que GitHub connaît pour le dépôt (le « graphe de dépendances »). Pour un projet Maven, cette liste vient du [pom](../../pom.xml), ou d'une liste envoyée par un workflow.

Un exemple réel (alerte n° 82) : Spring Boot 3.5.16 apporte `log4j-api` 2.24.3, qui a une faille connue. Le pom impose donc la version corrigée, en attendant que Spring Boot la fournisse :

```xml
<!-- Faille connue de log4j-api 2.24.3 (version de Spring Boot 3.5.16), corrigée en 2.25.5
     (alerte Dependabot n° 82). À retirer quand Spring Boot fournira une version corrigée. -->
<log4j2.version>2.25.5</log4j2.version>
```

*Une alerte se lit en trois questions* :
1. **La bibliothèque est-elle vraiment utilisée ?** Elle peut venir d'une ancienne liste ou d'un outil de test.
2. **Le code vulnérable est-il appelé ?** Une faille dans le serveur web de Tomcat ne touche pas un jeu qui n'ouvre aucun port.
3. **Une version corrigée existe-t-elle ?** Si oui, monter de version (en général via Spring Boot) ; sinon, surveiller.

**🟣 Le recul (senior / vétéran)**

- *Le cas des 87 alertes (21/11/2026)* : au push de l'étape 5 bis, GitHub annonçait 87 failles, dont 10 critiques, dans Tomcat, Spring Web et Jackson. Or le pom de Ladybug ne contient plus `spring-boot-starter-web` depuis longtemps. L'enquête :
  - GitHub connaît **deux** listes de dépendances : `pom.xml`, et un étrange `home/runner/work/ladybug/ladybug/pom.xml` ;
  - ce chemin est celui d'une machine de la CI : la liste a été **envoyée par un workflow**, pas lue dans le dépôt ;
  - la plus ancienne alerte date du 12 mars 2024, le jour où `maven-test-fla.yml` a été ajouté sur `master`, avec l'action `maven-dependency-submission-action` ;
  - ce workflow a envoyé une fois la liste des dépendances de l'époque (Spring Boot 3.0, avec le serveur web), puis il a disparu. GitHub a gardé cette liste, jamais remplacée.

  Conclusion : ces alertes décrivent le Ladybug de 2024, pas celui d'aujourd'hui. À traiter : les marquer comme non pertinentes (« inaccurate ») une fois la vérification faite, ou remplacer l'ancienne liste par une liste à jour.
- *Le bruit tue l'alerte* : 87 alertes dont la plupart sont fausses, c'est le meilleur moyen de rater la 88e, vraie. Une liste d'alertes propre vaut mieux qu'une longue.
- *Mises à jour automatiques* : Dependabot peut aussi ouvrir lui-même des PR de montée de version (fichier `.github/dependabot.yml`). Ladybug ne l'a pas encore : chaque montée de version passe par une décision et par la documentation à jour (Context7). Avec la CI sur trois systèmes et le golden master, ces PR seraient pourtant sûres à tester.
- *Alternatives* : OWASP Dependency-Check (en local, dans Maven), Snyk, ou l'analyse de sécurité de [SonarQube Cloud](sonarqube-cloud.md), qui regarde le code de Ladybug plutôt que ses bibliothèques.

**✏️ Exercice**

Ouvrez l'onglet « Security », puis « Dependabot ». Prenez une alerte Tomcat, lisez le fichier qu'elle cite et vérifiez avec `mvn dependency:tree | grep tomcat` que Tomcat n'est plus dans Ladybug. Puis ouvrez la page « Dependency graph » (Insights) et comparez les deux listes.
