# JaCoCo : ce que les tests exécutent vraiment

> Bibliothèque libre de mesure de couverture ([jacoco.org](https://www.jacoco.org)), version 0.8.16. Introduite dans Ladybug à l'étape 5 bis (21/11/2026).

**🟢 L'idée en une image (junior)**

Un inspecteur visite une maison et coche chaque pièce où il est entré. À la fin, le plan montre en vert les pièces visitées et en rouge celles où personne n'a mis les pieds. Il ne dit pas si les pièces vertes sont en bon état : seulement qu'on y est passé.

JaCoCo fait la même chose avec le code : pendant les tests, il note chaque ligne exécutée. Le rapport colore le code en vert (exécuté), jaune (un `if` dont une seule branche a servi) ou rouge (jamais exécuté).

**🔵 Dans Ladybug (confirmé)**

JaCoCo se branche comme un **agent Java** : un petit programme chargé dans la machine virtuelle, à côté des tests, qui observe le code pendant qu'il tourne. Dans le [pom](../../pom.xml) :

```xml
<plugin>
	<groupId>org.jacoco</groupId>
	<artifactId>jacoco-maven-plugin</artifactId>
	<version>0.8.16</version>
	<executions>
		<execution><id>prepare-agent</id><goals><goal>prepare-agent</goal></goals></execution>
		<execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
	</executions>
</plugin>
```

- `prepare-agent` prépare l'agent avant les tests ;
- `report` écrit le rapport après les tests, pendant `mvn verify`.

Résultat au 21/11/2026, 251 tests :

| | Couverture |
|---|---|
| **Lignes**, tout le jeu | **65,7 %** |
| **Branches** (les deux côtés de chaque `if`) | 49,9 % |
| Modèle, personnages, score, entrées | plus de 90 % |
| Vues (`engine.view…`) | de 0 à 35 % |

Les vues sont faibles, et c'est normal : elles dessinent dans une fenêtre, et les tests tournent sans écran. C'est le [robot](robot.md) qui les vérifie.

Où le trouver :
- en local : `mvn verify`, puis ouvrir `target/site/jacoco/index.html` ;
- dans la CI : sur la page d'un run de Build, onglet « Artifacts », fichier `jacoco-report` (Linux seulement) ;
- dans [SonarQube Cloud](sonarqube-cloud.md), qui lit le même rapport (`jacoco.xml`).

**🟣 Le recul (senior / vétéran)**

- *Couverture n'est pas qualité* : une ligne exécutée par un test qui ne vérifie rien est « couverte ». C'est le rôle du [test de mutation](../patterns/test-de-mutation.md) (PIT) : JaCoCo dit « ce code a tourné », PIT dit « si ce code était faux, un test l'aurait vu ». Ladybug utilise les deux.
- *Pourquoi Sonar affiche 60,1 % et JaCoCo 65,7 %* : Sonar mélange lignes et branches en un seul chiffre. Même rapport, autre calcul.
- *Le piège de l'`argLine`* : l'agent JaCoCo passe par la ligne de commande des tests (`argLine`), que Ladybug utilisait déjà pour l'agent Mockito. Écrire `<argLine>-javaagent:mockito…</argLine>` aurait **écrasé** celui de JaCoCo, et la couverture serait restée à zéro sans erreur. La solution est l'évaluation tardive `@{argLine}`, qui garde les deux :
  ```xml
  <argLine>@{argLine} -Xshare:off -javaagent:${org.mockito:mockito-core:jar}</argLine>
  ```
  Une propriété vide `<argLine></argLine>` évite une erreur si JaCoCo est désactivé.
- *PIT et JaCoCo ensemble* : PIT relit cette ligne de commande, donc l'agent JaCoCo ralentirait chacun de ses milliers de passages. Le workflow nocturne de PIT lance donc Maven avec `-Djacoco.skip=true`.
- *Pas de seuil (pour l'instant)* : JaCoCo sait faire échouer le build sous un pourcentage (`check`). Ladybug ne le fait pas : PIT a déjà un seuil de 60 %, et un seuil de couverture pousse à écrire des tests pour le chiffre. La porte qualité de Sonar surveille plutôt la couverture du **code nouveau**.

**✏️ Exercice**

Lancez `mvn verify`, ouvrez le rapport et descendez jusqu'à `com.kycox.game.tools.timer`. Trouvez une ligne rouge, écrivez le test qui l'exécute, relancez et regardez-la passer au vert. Puis demandez-vous si ce test vérifie vraiment quelque chose, ou s'il ne fait que passer par là.
