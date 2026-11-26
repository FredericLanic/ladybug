## Étape 8 : modernisation du langage et ménage
8.1 `record`, `switch` avec pattern matching, `var` lorsqu'il clarifie le code.
8.2 Logs : passage de `commons-logging` à SLF4J ; suppression de `log4j.properties` et de `src/main/java/log4j2-test.xml`.
8.3 Ménage :
- `view/tests/*` ;
- `TraceurAdvice` s'il n'est pas utilisé ;
- `images/_inWork` déplacé hors de `src`.

8.3 bis **Dette D29** : supprimer les boucles entre packages gelées par ArchUnit (5b.1), en commençant par vider `constant` (contrats vers leurs couches, images vers la vue, données de niveaux vers `maze`) ; le registre `archunit-store` doit finir vide.
8.4 **Documentation finale** : `ARCHITECTURE.md` complet, `package-info.java`, README technique (« pour les contributeurs »). Relecture du point de vue d'un développeur qui découvre le projet (le test « Jeff »). Le graphe Graphify final, comparé à celui de l'étape 0, illustre le chemin parcouru.
8.5 *(idée I18, 20/11/2026, à décider)* **Un cœur sans Spring, en modules Maven**, pour préparer une version Android : `ladybug-core` (Java pur : règles, modèle, `Position`, Dijkstra, `GameSnapshot`, tests et golden master), `ladybug-desktop` (Spring Boot, XML, Swing ou JavaFX), plus tard `ladybug-android` (libGDX). Le Spring du modèle est surtout cosmétique : 39 fichiers sur 42 n'ont qu'un `@Component` ; restent `ApplicationEventPublisher` (remplacé par le snapshot en 4.6), `MessageSource`, deux `@Value`, deux `@PostConstruct`.
