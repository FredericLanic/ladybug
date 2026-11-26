## Étape 2 : nouveau démarrage (lanceur et XML externe)
> **Fait le 19/11/2026** (branche `etape-2-lanceur`), **avec Spring Boot** à la demande de Frédéric (« JE VEUX QUE CE SOIT SPRINGBOOT ») :
> - 2.1 `SpringApplicationBuilder` + `headless(false)` (Spring Boot passe sinon la JVM en mode sans écran, d'où l'échec de l'ancien essai commenté) + `@ImportResource` du XML ; `MainLadybug` est un `ApplicationRunner` qui ouvre la fenêtre par `invokeLater` ; ancien format de log conservé ;
> - 2.2 version lue dans le manifest (bannière Spring Boot et log) ; le contrôle « Java 19+ » était du code mort (une JVM plus ancienne refuse de charger les classes compilées pour Java 21) : retiré. **D03 corrigée** ;
> - 2.3 et 2.4 fichiers externes selon la convention Spring Boot (**C29** : dossier courant et `config/`, et non « à côté du jar ») : `application-context.xml` (`--ladybug.config=file:…` pour en imposer un) et `ladybug.properties` (`spring.config.import`) ; `StartupReport` écrit les fichiers utilisés ; XML invalide → message clair et arrêt (**C27**) ;
> - 2.5 `LaunchMode.LOCAL` seulement ; le choix 1 ou 2 joueurs reste dans le jeu (**C28**) ;
> - 2.7 et 3.3 profil « gui » par la méta-annotation `@GuiComponent` (21 classes d'interface) ; `SpringContextTest` et `ExternalSpringXmlTest` chargent le vrai contexte sans écran ;
> - 2.8 écran de la souris, place réellement disponible (**D18 corrigée**) ; à la demande de Frédéric : bloc centré, zone du bas collée au labyrinthe, entre 2 et 3 cases de haut, dessin de Ladybug sur toute sa bande, plafonné à une case ;
> - 2.6 README « Personnaliser le jeu », fiche `doc/patterns/configuration-externalisee.md`.
> 128 tests ; robot de fin d'étape dans `doc/reference-apres/etape-2/`.

2.1 `MainLadybug` devient un petit **lanceur** :
- lecture de la configuration ;
- choix du mode ;
- construction du contexte Spring ;
- affichage de la fenêtre via `SwingUtilities.invokeLater`.

2.2 Version lue depuis le manifest (fin de la chaîne en dur), contrôle de version de Java aligné sur le pom.
2.3 **XML externe prioritaire** :
- si `<dossier du jar>/application-context.xml` existe, il est chargé par `FileSystemXmlApplicationContext` ;
- sinon, celui du classpath est utilisé ;
- l'option `-Dladybug.config=...` permet de forcer un fichier ;
- les logs indiquent quel fichier est utilisé.

2.4 Même mécanisme pour `ladybug.properties` (`@PropertySource` classpath puis `file:`, avec `ignoreResourceNotFound`).
2.5 Modes prévus dans le lanceur dès maintenant : **solo** et **2 joueurs local**. La structure est prête pour accueillir plus tard « héberger » et « rejoindre ».
2.6 README : section « Personnaliser le jeu ».
2.7 Une fois le lanceur en place : test 3.3 (chargement du contexte Spring en mode headless), avec un cas pour le XML externe prioritaire.
2.8 **Écran du jeu selon la souris** (demandé par Frédéric le 18/11/2026) :
- la fenêtre s'ouvre sur l'écran où se trouve la souris au lancement (`MouseInfo.getPointerInfo().getDevice()`) ;
- les dimensions viennent de la place réellement disponible sur cet écran (bornes de l'écran moins barre de menus et Dock, via `Toolkit.getScreenInsets`), et non plus de `Toolkit.getScreenSize()`, qui donne l'écran principal barre de menus comprise ;
- corrige au passage le bas du labyrinthe coupé sur Mac : 118 px réservés en haut et en bas, il reste 687 px pour un labyrinthe de 720.
- la hauteur de la zone du haut est divisée par deux (`topDimension` = `getBorderHeight() / 2`), comme l'a demandé Frédéric. Ce correctif, d'abord proposé en commit séparé à l'étape 1, est reporté ici (choix de Frédéric, 18/11/2026).
