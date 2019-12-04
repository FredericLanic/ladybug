package com.kycox.ladybug.tools.dijkstra;

import java.awt.Point;
import java.util.List;

import com.kycox.ladybug.level.ScreenBlock;
import com.kycox.ladybug.level.ScreenData;

/**
 * Algorithme Dijkstra pour trouver un plus court chemin dans un ScreenData
 *
 */
public class Dijkstra {
  /**
   * Retourne une liste de point en coordonnées (unité block) pour aller du point A au point B
   *
   * @param screenData      : jeu en cours
   * @param startCoordinate : coordonnées de départ
   * @param endCoordinate   : coordonnées d'arrivée
   * @return List<Point>
   */
  public static List<Point> getShorterWay(Point startCoordinate, Point endCoordinate,
      ScreenData screenData) {
    List<Point> shorterWay;
    Dijkstra    dijkstra = new Dijkstra(screenData);
    dijkstra.init(startCoordinate, screenData);

    // Lancement de la recherche
    dijkstra.search(startCoordinate, null, screenData);

    // Récupération de l'unité final
    int          idx            = dijkstra.getPos(endCoordinate, screenData);
    UnitDijkstra unitDijstraEnd = dijkstra.getListUnitDijkstra().get(idx);
    // Récupération du chemin le plus court
    shorterWay = unitDijstraEnd.getShorterWay();
    // FIXME : peut être des vérifications ? / capture d'exceptions au cas de Level
    // non valide ?
    return shorterWay;
  }

  private List<UnitDijkstra> listUnitDijkstra;
  int                        nbrAccessibleBlocks;

  /**
   * @param level
   */
  /**
   * Calcul du parcours le plus court en partant de startCoordonnate (coordonnées de Block) dans un
   * Level de ladybug IMPORTANT : On part du principe que le level a été vérifié : - tous les blocks
   * ont les bonnes bordures INDIVIDUELLEMENT
   *
   * @param screenData      : jeu en cours
   * @param startCoordinate : coordonnées de départ (unité : le block du screenData)
   */
  private Dijkstra(ScreenData screenData) {
    // Création de la list des unités Dijkstra
    listUnitDijkstra = screenData.convertToDjistraList();
  }

  /**
   * Getter et Setter
   *
   * @return
   */
  private List<UnitDijkstra> getListUnitDijkstra() {
    return listUnitDijkstra;
  }

  /**
   * Retourne l'index de listUnitDijkstra en fonction des coordonnées
   *
   * @param coordinate (unité x et y : block du genericData) /!\ intimmement lié au genericData
   * @return
   */
  private int getPos(Point coordinate, ScreenData screenData) {
    return coordinate.x + coordinate.y * screenData.getNbrBlocksPerLine();
  }

  /*
   * Initialisation de Dijkstra
   */
  private void init(Point startCoordinate, ScreenData screenData) {
    // Initialisation du block de début
    int posStart = getPos(startCoordinate, screenData);
    listUnitDijkstra.get(posStart).setDistance(0);
  }

  /**
   * Recherche (méthode récursice)
   *
   * @param currentCoordinate    : coordonnées à gérer (unité x et y : block du genericData)
   * @param previousUnitDijkstra
   */
  private void search(Point currentCoordinate, UnitDijkstra previousUnitDijkstra,
      ScreenData screenData) {
    // on rcupère la position
    int          currentPos          = getPos(currentCoordinate, screenData);
    // on initialise le flag weightHasChanged
    boolean      weightHasChanged    = false;

    UnitDijkstra currentUnitDijkstra = listUnitDijkstra.get(currentPos);

    // on pose le poids
    if (previousUnitDijkstra == null) { // toute première case
      currentUnitDijkstra.setDistance(0);
      currentUnitDijkstra.setCoordinate(currentCoordinate);
      currentUnitDijkstra.setPrevious(previousUnitDijkstra);
      weightHasChanged = true;
      // on doit faire les cases autour
    } else if (previousUnitDijkstra.getDistance() + 1 < currentUnitDijkstra.getDistance()) {
      // je pense qu'il y a une erreur dans la condition
      currentUnitDijkstra.setDistance(previousUnitDijkstra.getDistance() + 1);
      currentUnitDijkstra.setCoordinate(currentCoordinate);
      currentUnitDijkstra.setPrevious(previousUnitDijkstra);
      weightHasChanged = true;
    }

    // On récupère le Block courant
    ScreenBlock screenBlockCurrent = currentUnitDijkstra.getScreenBlock();

    // Définition des prochains points
    Point       coordinateUp       = new Point(currentCoordinate.x, currentCoordinate.y - 1);
    Point       coordinateLeft     = new Point(currentCoordinate.x - 1, currentCoordinate.y);
    Point       coordinateRight    = new Point(currentCoordinate.x + 1, currentCoordinate.y);
    Point       coordinateDown     = new Point(currentCoordinate.x, currentCoordinate.y + 1);

    // Lancement de la recherche en haut quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isUp()) {
      search(coordinateUp, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche à gauche quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isLeft()) {
      search(coordinateLeft, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche à droite quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isRight()) {
      search(coordinateRight, currentUnitDijkstra, screenData);
    }

    // Lancement de la recherche en bas quand c'est possible
    if (weightHasChanged && !screenBlockCurrent.isDown()) {
      search(coordinateDown, currentUnitDijkstra, screenData);
    }
  }
}
