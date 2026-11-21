# Les outils de qualité de Ladybug

Les [fiches de patterns](../patterns/README.md) expliquent des façons d'**écrire** le code. Ces fiches-ci expliquent les outils qui le **surveillent** : ce qu'ils vérifient, comment ils sont branchés dans Ladybug, et ce qu'ils ne voient pas.

Même format que les patterns, à trois niveaux de lecture :
- 🟢 junior : l'idée en une image ;
- 🔵 confirmé : l'outil dans Ladybug, avec la vraie configuration, la vraie commande et un vrai résultat ;
- 🟣 vétéran : les limites, les pièges rencontrés et les alternatives.

Chaque fiche se termine par un petit exercice facultatif.

| Fiche | En une phrase | Où | Étape |
|---|---|---|---|
| [ArchUnit](archunit.md) | Les règles d'architecture écrites comme des tests ; les 53 boucles de packages existantes sont gelées. | build | 5 bis |
| [JaCoCo](jacoco.md) | Mesurer les lignes et les branches que les tests exécutent vraiment (65,7 % des lignes). | build | 5 bis |
| [SonarQube Cloud](sonarqube-cloud.md) | Une relecture automatique de chaque PR : bugs probables, duplication, dette, porte qualité. | CI | 5 bis |
| [CI GitHub Actions](ci-github-actions.md) | Chaque PR compilée et testée sur Linux, Windows et macOS ; `develop` protégée. | CI | 1, 3 |
| [Robot de vérification visuelle](robot.md) | Le vrai jeu joué au clavier, captures et logs à l'appui, relancé à chaque étape. | poste local | 0 |
| [Dependabot](dependabot.md) | Les failles connues des bibliothèques utilisées, et comment lire une alerte (dont 87 fausses). | GitHub | en continu |
| [Graphify](graphify.md) | La carte du code, pour mesurer avant et après chaque étape. | poste local | 0 |

Deux techniques de test ont déjà leur fiche dans les patterns :
- [Golden master](../patterns/golden-master.md) : figer le journal d'une partie de référence et le comparer à chaque build ;
- [Test de mutation](../patterns/test-de-mutation.md) (PIT) : glisser de petites erreurs dans le code pour vérifier que les tests les détectent.

## Qui vérifie quoi

| Risque | Qui le voit |
|---|---|
| Une règle du jeu change sans le vouloir | golden master, tests unitaires |
| Un test ne vérifie rien | PIT, sabotage |
| Un morceau de code n'est jamais testé | JaCoCo |
| L'architecture se dégrade | ArchUnit, Graphify |
| Un bug probable, du copié-collé | SonarQube Cloud |
| Ça ne marche que sur un système | CI sur trois systèmes |
| L'affichage ou les touches sont cassés | robot, parties de Frédéric |
| Une bibliothèque a une faille connue | Dependabot |
| Le son, le plaisir de jeu | Frédéric |

## Le sabotage, règle commune

Tout nouveau test, et toute nouvelle règle ArchUnit, est vérifié en cassant le code exprès : le test doit échouer, puis on retire le sabotage. Un test qui ne peut pas échouer ne protège rien. Cette habitude a déjà révélé plusieurs tests trop faibles aux étapes 4 et 5.
