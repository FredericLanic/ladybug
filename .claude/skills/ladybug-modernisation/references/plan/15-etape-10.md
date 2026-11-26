## Étape 10 : optionnel
- Packaging natif avec `jpackage`.
- Interface JavaFX ou libGDX, facilitée par le découplage de l'étape 4 (**idée I17**, Frédéric, 20/11/2026). Depuis 4.3, le modèle n'importe plus ni AWT ni Swing ; resteraient à refaire les vues (24 fichiers, ~2 000 lignes), les images et la police, le clavier, la boucle, l'écran et la fenêtre. Avec le `GameSnapshot` (4.6), une vue se résume à « dessiner un snapshot » ; le profil Spring permettrait de faire cohabiter deux interfaces pendant la migration.
- **Version Android** (**idée I18**, Frédéric, 20/11/2026) : avec libGDX (même code d'affichage pour PC et Android), sur le cœur sans Spring de 8.5 ; écran tactile, sons Android, langage Java 17 côté Android (à vérifier le moment venu). Question pour l'étape 6 : lire la configuration des fantômes sur Android, le XML Spring restant le format PC (C01).

