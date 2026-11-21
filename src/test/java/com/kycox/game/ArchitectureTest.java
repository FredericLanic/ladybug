package com.kycox.game;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import java.util.Set;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;

import com.kycox.game.body.ghost.Ghost;
import com.kycox.game.body.ghost.GhostColor;
import com.kycox.game.engine.model.EventGameModel;
import com.kycox.game.engine.score.MessageType;
import com.kycox.game.input.PlayerInput;

/**
 * Les règles d'architecture de Ladybug, vérifiées à chaque build comme des tests
 * ordinaires (étape 5 bis, ArchUnit).
 *
 * <p>ArchUnit lit le bytecode compilé et regarde qui utilise qui. Chaque règle
 * écrit noir sur blanc une décision prise aux étapes 4 et 5 : si quelqu'un la
 * contourne plus tard, le build passe au rouge en citant la classe et la ligne
 * en cause, au lieu d'une dérive silencieuse découverte des mois après.
 *
 * <p>Seul le code de production est analysé : les tests ont le droit de tout
 * manipuler (par exemple écrire directement dans un {@link PlayerInput}).
 */
@AnalyzeClasses(packages = "com.kycox.game", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

	/** Le cœur du jeu : règles, personnages, labyrinthe, entrées, photo de la partie. */
	private static final String[] GAME_CORE = { "com.kycox.game.engine.model..", "com.kycox.game.engine.maze..",
	        "com.kycox.game.engine.bodyaction..", "com.kycox.game.engine.score..", "com.kycox.game.body..",
	        "com.kycox.game.input..", "com.kycox.game.snapshot..", "com.kycox.game.preferences.." };

	/** Les packages du modèle « vivant », celui que les vues ne doivent pas lire. */
	private static final String LIVE_MODEL = "com\\.kycox\\.game\\.(engine\\.(model|maze|bodyaction|score)|body)(\\..*)?";

	/**
	 * Exceptions assumées à la règle des vues : l'événement qui transporte la photo,
	 * et trois énumérations qui servent de vocabulaire commun à la photo et aux vues.
	 */
	private static final Set<Class<?>> SHARED_WITH_VIEWS = Set.of(EventGameModel.class, GhostColor.class,
	        Ghost.MoveStyle.class, MessageType.class);

	/**
	 * Le cœur du jeu ne connaît ni Swing ni AWT (étape 4.1) : il tourne sans écran,
	 * dans les tests comme demain sur un serveur (étape 9, réseau).
	 */
	@ArchTest
	static final ArchRule gameCoreDoesNotKnowTheScreen = noClasses().that()
	        .resideInAnyPackage(GAME_CORE)
	        .should().dependOnClassesThat().resideInAnyPackage("java.awt..", "javax.swing..")
	        .because("le modèle doit tourner sans écran (tests, futur serveur réseau)");

	/**
	 * Les vues ne lisent que la photo de la partie (étape 4.6) : jamais le modèle,
	 * les personnages ou le labyrinthe vivants.
	 */
	@ArchTest
	static final ArchRule viewsOnlyReadTheSnapshot = noClasses().that()
	        .resideInAPackage("com.kycox.game.engine.view..")
	        .should().dependOnClassesThat(describe("appartiennent au modèle vivant",
	                (JavaClass target) -> target.getPackageName().matches(LIVE_MODEL)
	                        && SHARED_WITH_VIEWS.stream().noneMatch(target::isEquivalentTo)))
	        .because("une vue dessine la photo de la partie (GameSnapshot), elle ne lit pas le modèle");

	/**
	 * Seuls les contrôleurs (clavier, manette) écrivent les ordres des joueurs
	 * (étape 5) ; demain, le réseau s'ajoutera à cette liste.
	 */
	@ArchTest
	static final ArchRule onlyControllersWritePlayerInputs = noClasses().that()
	        .resideOutsideOfPackages("com.kycox.game.engine.controller..", "com.kycox.game.input..")
	        .should().callMethodWhere(describe("PlayerInput.request(…)",
	                (JavaMethodCall call) -> call.getTargetOwner().isEquivalentTo(PlayerInput.class)
	                        && call.getName().equals("request")))
	        .because("un ordre de joueur vient d'un clavier, d'une manette ou (plus tard) du réseau");

	/**
	 * Pas de nouvelle dépendance en boucle entre les grands packages.
	 *
	 * <p>Il en existe déjà (dette D29) : surtout à cause de {@code constant}, qui
	 * mélange vraies constantes, contrats, images et données de niveaux. Plutôt
	 * que de tout réécrire maintenant, la règle est « gelée » : les boucles
	 * actuelles sont enregistrées dans {@code src/test/resources/archunit-store},
	 * seule une boucle nouvelle fait échouer le build, et chaque boucle supprimée
	 * disparaît du registre (elle ne peut plus revenir).
	 */
	@ArchTest
	static final ArchRule noNewCyclesBetweenTopLevelPackages = FreezingArchRule
	        .freeze(slices().matching("com.kycox.game.(*)..").should().beFreeOfCycles())
	        .associateViolationLinesVia(ArchitectureTest::sameCycle);

	/**
	 * Une boucle est reconnue par son chemin (body -> engine -> body), sans tenir
	 * compte du nombre de dépendances sur chaque lien : sinon, ajouter un simple
	 * appel sur un lien déjà en boucle ferait croire à une nouvelle boucle. Les
	 * dépendances ne sont pas détaillées (voir {@code archunit.properties}).
	 */
	private static boolean sameCycle(String storedLine, String currentLine) {
		return withoutCounts(storedLine).equals(withoutCounts(currentLine));
	}

	private static String withoutCounts(String line) {
		return line.replaceAll("\\(\\d+ further dependencies have been omitted\\.\\.\\.\\)", "(…)");
	}
}
