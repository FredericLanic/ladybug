## Vérification (à chaque étape)
0. Étape 0 :
   - `graphify-out/graph.html` s'ouvre et montre les packages de `com.kycox.game` ; `graphify-out/` est ignoré par git ;
   - Context7 répond à une question test, par exemple « notes de migration Spring Boot 3.0 → 3.5 » ;
   - la branche `modernisation` existe depuis `origin/develop`, avec un premier commit (skill et PDF) ;
   - `doc/checklist-jeu.md` et `doc/reference-avant/` existent, et l'état de compilation de `develop` est noté.
1. `mvn clean verify` : compilation et tests au vert (des tests existent à partir de 3.1 et 3.2, réalisés juste après l'étape 1).
2. `java -jar target/ladybug-*.jar` : le jeu démarre (même sans manette) et une partie se joue.
3. Étape 2 : un XML modifié à côté du jar est pris en compte, le log l'indique, et un XML cassé produit un message clair. Avec deux écrans, le jeu s'ouvre sur celui de la souris, et le labyrinthe est entier.
4. Étape 5 : deux fantômes joués en local, au clavier et à la manette.
5. Étape 6 : ajouter un 5ᵉ fantôme dans le XML externe, sans recompiler.
6. Étape 6 : en mode lampe, la visibilité respecte la `Perception` de chaque personnage.
7. Étape 7 : un `level9.txt` ajouté à côté du jar est jouable.
8. Étape 9 : deux postes sur le même réseau, l'un Ladybug, l'autre fantôme.
