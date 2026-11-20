import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * Robot de vérification de Ladybug : lance le jeu, lui envoie des touches selon un
 * scénario écrit à l'avance, prend des captures de la fenêtre et note les changements
 * de statut lus dans les logs.
 *
 * <p>Ce fichier ne fait pas partie du jeu : il ne touche pas au code et peut être
 * relancé après chaque étape pour comparer avec l'état « avant ».
 *
 * <p>Lancement (Java 21, sans compilation grâce au « source launcher ») :
 * <pre>
 *   java doc/reference-avant/robot/LadybugRobot.java target/ladybug-0.6.0-SNAPSHOT.jar doc/reference-avant/2026-11-18
 * </pre>
 *
 * <p>Prérequis macOS : autoriser le terminal (ou Claude) dans Réglages système →
 * Confidentialité et sécurité → « Enregistrement de l'écran » (captures) et
 * « Accessibilité » (envoi des touches, position de la fenêtre).
 *
 * <p>Sécurité : avant chaque touche, le robot vérifie que la fenêtre du jeu est au
 * premier plan. Si ce n'est pas le cas (clic ailleurs, autre application), il
 * s'arrête au lieu de taper dans une autre fenêtre.
 */
public class LadybugRobot {

	/** Ligne de log émise par CurrentProgramStatus à chaque changement de statut. */
	private static final Pattern STATUS_LINE = Pattern.compile("Passage du status en (\\w+)");

	/** Nombre de parties lancées pour tenter de voir les 4 rendus (tirés au hasard). */
	private static final int RENDERING_ATTEMPTS = 12;

	private final Path jar;
	private final Path outputDir;
	private final Robot robot;
	private final List<String> statuses = new CopyOnWriteArrayList<>();
	private final List<String> report = new ArrayList<>();
	private Process game;
	private int captureCount = 0;

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage : java LadybugRobot.java <chemin du jar> <dossier de sortie>");
			System.exit(1);
		}
		var ladybugRobot = new LadybugRobot(Path.of(args[0]), Path.of(args[1]));
		var exitCode = 0;
		try {
			ladybugRobot.run();
		} catch (Exception e) {
			ladybugRobot.note("❌ Scénario interrompu : " + e.getMessage());
			exitCode = 2;
		} finally {
			ladybugRobot.stopGame();
			ladybugRobot.writeReport();
		}
		System.exit(exitCode);
	}

	public LadybugRobot(Path jar, Path outputDir) throws AWTException, IOException {
		this.jar = jar;
		this.outputDir = outputDir;
		this.robot = new Robot();
		this.robot.setAutoDelay(40);
		Files.createDirectories(outputDir);
	}

	// ------------------------------------------------------------------
	// Le scénario : une méthode par section de doc/checklist-jeu.md
	// ------------------------------------------------------------------

	private void run() throws Exception {
		startGame();
		checkStartup();          // section 1
		startSoloGame();
		checkMovement();         // 2.1
		checkFunctionKeys();     // section 5
		checkPause();            // 5.6
		checkEscape();           // 5.7
		collectRenderings();     // section 6
		checkTwoPlayers();       // 4.1
		quitFromPresentation();  // 5.7 (sortie du jeu)
	}

	/** Section 1 : présentation, aide F1, choix du nombre de joueurs. */
	private void checkStartup() throws Exception {
		section("1. Démarrage");
		capture("presentation");
		// L'aide ne reste affichée que tant que F1 est enfoncée (keyReleased la masque)
		ensureGameInFront();
		robot.keyPress(KeyEvent.VK_F1);
		pause(600);
		capture("aide-f1-enfoncee");
		robot.keyRelease(KeyEvent.VK_F1);
		pause(400);
		capture("aide-f1-relachee");
		press(KeyEvent.VK_2);
		capture("choix-2-joueurs");
		press(KeyEvent.VK_1);
		capture("choix-1-joueur");
	}

	/** Lance une partie solo depuis la présentation et attend IN_GAME. */
	private void startSoloGame() throws Exception {
		section("Lancement d'une partie solo");
		launchGameFromPresentation();
		capture("en-jeu-depart");
	}

	/** 2.1 : quelques flèches, une capture après chacune. */
	private void checkMovement() throws Exception {
		section("2.1 Déplacement de Ladybug");
		for (var key : new int[] { KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN }) {
			press(key);
			pause(1200);
			capture("deplacement-" + KeyEvent.getKeyText(key).toLowerCase());
		}
	}

	/** Section 5 : chaque touche est pressée, capturée, puis pressée de nouveau pour revenir à l'état initial. */
	private void checkFunctionKeys() throws Exception {
		section("5. Touches de fonction");
		toggleAndCapture(KeyEvent.VK_F2, "f2-son");
		toggleAndCapture(KeyEvent.VK_F3, "f3-logo");
		toggleAndCapture(KeyEvent.VK_F4, "f4-bandeau");
		toggleAndCapture(KeyEvent.VK_F5, "f5-chapeau");
		toggleAndCapture(KeyEvent.VK_F6, "f6-lampe");
		toggleAndCapture(KeyEvent.VK_F7, "f7-camouflage");
		toggleAndCapture(KeyEvent.VK_ENTER, "entree-debug");
	}

	/** 5.6 : en pause, deux captures à une seconde d'écart doivent être identiques. */
	private void checkPause() throws Exception {
		section("5.6 Pause");
		note("Attention : les claviers Mac n'ont pas de touche Pause. Le robot envoie VK_PAUSE, "
				+ "mais macOS ne la transmet peut-être pas : comparer avec le bruit des autres captures (≈ 0,9 %).");
		press(KeyEvent.VK_PAUSE);
		pause(500);
		var first = capture("pause-1");
		pause(1000);
		var second = capture("pause-2");
		note("Différence entre les deux captures en pause : %.2f %% des pixels (≈ 0 attendu)",
				differencePercent(first, second));
		press(KeyEvent.VK_PAUSE);
		pause(1000);
		var resumed = capture("pause-reprise");
		note("Différence après reprise : %.2f %% des pixels (> 0 attendu)", differencePercent(second, resumed));
	}

	/** 5.7 : Échap demande confirmation ; N reprend la partie, O l'arrête. */
	private void checkEscape() throws Exception {
		section("5.7 Échap");
		var from = statuses.size();
		press(KeyEvent.VK_ESCAPE);
		if (!waitForStatusSince(from, "ASK_FORCE_END_GAME", 5)) {
			throw new IllegalStateException("ASK_FORCE_END_GAME non atteint après Échap (statut : " + lastStatus() + ")");
		}
		capture("echap-confirmation");
		from = statuses.size();
		press(KeyEvent.VK_N);
		if (!waitForStatusSince(from, "IN_GAME", 5)) {
			throw new IllegalStateException("IN_GAME non atteint après N (statut : " + lastStatus() + ")");
		}
		note("N : retour en partie ✅");
		stopCurrentGame();
	}

	/** Section 6 : plusieurs parties pour croiser les 4 rendus tirés au hasard. */
	private void collectRenderings() throws Exception {
		section("6. Rendus des niveaux (" + RENDERING_ATTEMPTS + " parties)");
		note("Le rendu n'est pas écrit dans les logs : il est identifié en regardant les captures.");
		for (var attempt = 1; attempt <= RENDERING_ATTEMPTS; attempt++) {
			launchGameFromPresentation();
			capture("rendu-partie-" + attempt);
			stopCurrentGame();
		}
	}

	/** 4.1 : mode 2 joueurs, le fantôme se dirige avec Z, Q, S et D. */
	private void checkTwoPlayers() throws Exception {
		section("4.1 Mode 2 joueurs");
		press(KeyEvent.VK_2);
		launchGameFromPresentation();
		capture("2-joueurs-depart");
		// En jeu, S déplace le fantôme vers le bas (ce n'est plus « Start »)
		for (var key : new int[] { KeyEvent.VK_Q, KeyEvent.VK_Z, KeyEvent.VK_D, KeyEvent.VK_S }) {
			press(key);
			pause(1200);
			capture("2-joueurs-fantome-" + KeyEvent.getKeyText(key).toLowerCase());
		}
		stopCurrentGame();
		press(KeyEvent.VK_1);
	}

	/** Échap pendant la présentation : le programme doit se fermer. */
	private void quitFromPresentation() throws Exception {
		section("Sortie du jeu");
		press(KeyEvent.VK_ESCAPE);
		var exited = game.waitFor(10, TimeUnit.SECONDS);
		note(exited ? "Échap en présentation : le jeu se ferme ✅ (code " + game.exitValue() + ")"
				: "❌ Échap en présentation : le jeu ne s'est pas fermé en 10 s");
	}

	// ------------------------------------------------------------------
	// Enchaînements réutilisés
	// ------------------------------------------------------------------

	/** Depuis la présentation : S, puis attente de IN_GAME (réponse N si le jeu propose de reprendre le niveau). */
	private void launchGameFromPresentation() throws Exception {
		var from = statuses.size();
		press(KeyEvent.VK_S);
		var deadline = System.currentTimeMillis() + 20_000;
		while (System.currentTimeMillis() < deadline) {
			if (lastStatus().equals("PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL") && statusAgeMs() > 1000) {
				note("Le jeu propose de reprendre le niveau précédent : réponse N");
				press(KeyEvent.VK_N);
			}
			if (statuses.subList(from, statuses.size()).contains("IN_GAME")) {
				pause(300);
				return;
			}
			pause(100);
		}
		throw new IllegalStateException("IN_GAME non atteint 20 s après S (dernier statut : " + lastStatus() + ")");
	}

	/**
	 * Échap puis O : fin de la partie et retour à la présentation.
	 *
	 * <p>Le 18/11, le jeu est une fois revenu de lui-même en IN_GAME 0,24 s après Échap
	 * (cause inexpliquée) : on réessaie donc jusqu'à 3 fois, en vérifiant que le jeu
	 * attend bien la confirmation au moment d'envoyer O.
	 */
	private void stopCurrentGame() throws Exception {
		for (var attempt = 1; attempt <= 3; attempt++) {
			var from = statuses.size();
			press(KeyEvent.VK_ESCAPE);
			if (!waitForStatusSince(from, "ASK_FORCE_END_GAME", 5) || !lastStatus().equals("ASK_FORCE_END_GAME")) {
				note("⚠️ Échap n°%d : le jeu n'attend pas de confirmation (statut : %s), nouvel essai", attempt,
						lastStatus());
				pause(500);
				continue;
			}
			from = statuses.size();
			press(KeyEvent.VK_O);
			if (waitForStatusSince(from, "PROGRAM_PRESENTATION", 10)) {
				pause(500);
				return;
			}
			note("⚠️ O n°%d : pas de retour à la présentation (statut : %s), nouvel essai", attempt, lastStatus());
		}
		throw new IllegalStateException("impossible d'arrêter la partie après 3 essais (statut : " + lastStatus() + ")");
	}

	private void toggleAndCapture(int key, String name) throws Exception {
		var before = capture(name + "-avant");
		press(key);
		pause(700);
		var after = capture(name + "-apres");
		note("%s : %.2f %% des pixels ont changé", KeyEvent.getKeyText(key), differencePercent(before, after));
		press(key);
		pause(500);
	}

	// ------------------------------------------------------------------
	// Le jeu : lancement, lecture des logs, arrêt
	// ------------------------------------------------------------------

	private void startGame() throws Exception {
		section("Lancement");
		var javaBin = Path.of(System.getProperty("java.home"), "bin", "java").toString();
		game = new ProcessBuilder(javaBin, "-jar", jar.toString()).redirectErrorStream(true).start();
		var log = outputDir.resolve("ladybug.log");
		var logReader = new Thread(() -> readLog(log), "lecture-logs");
		logReader.setDaemon(true);
		logReader.start();
		waitForStatus("PROGRAM_PRESENTATION", 60);
		pause(1000);
		note("Jeu lancé (pid %d), fenêtre : %s", game.pid(), windowBounds());
	}

	/** Recopie les logs du jeu dans un fichier et mémorise chaque changement de statut. */
	private void readLog(Path log) {
		try (var in = new BufferedReader(new InputStreamReader(game.getInputStream(), StandardCharsets.UTF_8));
				var out = new PrintWriter(Files.newBufferedWriter(log))) {
			String line;
			while ((line = in.readLine()) != null) {
				out.println(line);
				var matcher = STATUS_LINE.matcher(line);
				if (matcher.find()) {
					statuses.add(matcher.group(1));
					lastStatusTime = System.currentTimeMillis();
				}
			}
		} catch (IOException e) {
			System.err.println("Lecture des logs interrompue : " + e.getMessage());
		}
	}

	private volatile long lastStatusTime = System.currentTimeMillis();

	private String lastStatus() {
		return statuses.isEmpty() ? "" : statuses.get(statuses.size() - 1);
	}

	private long statusAgeMs() {
		return System.currentTimeMillis() - lastStatusTime;
	}

	/** Attend l'apparition d'un statut dans les logs (à partir de maintenant ou juste avant). */
	private void waitForStatus(String expected, int timeoutSeconds) throws Exception {
		var from = Math.max(0, statuses.size() - 1);
		var deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
		while (System.currentTimeMillis() < deadline) {
			if (statuses.subList(from, statuses.size()).contains(expected)) {
				return;
			}
			if (!game.isAlive()) {
				throw new IllegalStateException("le jeu s'est arrêté (code " + game.exitValue() + ")");
			}
			pause(100);
		}
		throw new IllegalStateException(expected + " non atteint en " + timeoutSeconds + " s (dernier statut : "
				+ lastStatus() + ")");
	}

	/**
	 * Attend un statut apparu depuis l'indice {@code from} (relevé AVANT l'envoi de la touche,
	 * pour ne pas rater un statut qui changerait très vite).
	 */
	private boolean waitForStatusSince(int from, String expected, int timeoutSeconds) throws Exception {
		var deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
		while (System.currentTimeMillis() < deadline) {
			if (statuses.subList(from, statuses.size()).contains(expected)) {
				return true;
			}
			if (!game.isAlive()) {
				throw new IllegalStateException("le jeu s'est arrêté (code " + game.exitValue() + ")");
			}
			pause(50);
		}
		return false;
	}

	private void stopGame() {
		if (game != null && game.isAlive()) {
			game.destroy();
		}
	}

	// ------------------------------------------------------------------
	// Clavier, fenêtre et captures
	// ------------------------------------------------------------------

	private void press(int key) throws Exception {
		ensureGameInFront();
		robot.keyPress(key);
		robot.keyRelease(key);
		pause(250);
	}

	/**
	 * Met la fenêtre du jeu au premier plan, puis vérifie qu'elle y est vraiment.
	 * Sinon le robot s'arrête : il ne doit jamais taper dans une autre application.
	 */
	private void ensureGameInFront() throws Exception {
		if (!game.isAlive()) {
			throw new IllegalStateException("le jeu s'est arrêté (code " + game.exitValue() + ")");
		}
		osascript("tell application \"System Events\" to set frontmost of (first process whose unix id is "
				+ game.pid() + ") to true");
		pause(100);
		var frontPid = osascript(
				"tell application \"System Events\" to get unix id of (first process whose frontmost is true)");
		if (!frontPid.equals(String.valueOf(game.pid()))) {
			throw new IllegalStateException("la fenêtre du jeu n'est plus au premier plan (application active : pid "
					+ frontPid + ") : arrêt par sécurité");
		}
	}

	/** Position et taille de la fenêtre du jeu (via System Events), ou l'écran entier à défaut. */
	private Rectangle windowBounds() {
		try {
			var answer = osascript("tell application \"System Events\" to tell (first process whose unix id is "
					+ game.pid() + ") to get {position, size} of window 1");
			var parts = answer.split(",\\s*");
			return new Rectangle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
		} catch (Exception e) {
			return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		}
	}

	private BufferedImage capture(String name) throws IOException {
		var image = robot.createScreenCapture(windowBounds());
		var fileName = String.format("%02d-%s.png", ++captureCount, name);
		ImageIO.write(image, "png", outputDir.resolve(fileName).toFile());
		report.add("- 📷 `" + fileName + "` (statut : " + lastStatus() + ")");
		return image;
	}

	/** Pourcentage de pixels différents entre deux captures de même taille. */
	private static double differencePercent(BufferedImage a, BufferedImage b) {
		if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
			return 100.0;
		}
		long different = 0;
		for (var y = 0; y < a.getHeight(); y++) {
			for (var x = 0; x < a.getWidth(); x++) {
				if (a.getRGB(x, y) != b.getRGB(x, y)) {
					different++;
				}
			}
		}
		return 100.0 * different / ((long) a.getWidth() * a.getHeight());
	}

	private static String osascript(String script) throws IOException, InterruptedException {
		var process = new ProcessBuilder("osascript", "-e", script).redirectErrorStream(true).start();
		var answer = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
		if (process.waitFor() != 0) {
			throw new IOException("osascript : " + answer);
		}
		return answer;
	}

	private static void pause(long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

	// ------------------------------------------------------------------
	// Rapport
	// ------------------------------------------------------------------

	private void section(String title) {
		report.add("");
		report.add("## " + title);
		System.out.println("== " + title);
	}

	private void note(String format, Object... args) {
		var text = args.length == 0 ? format : String.format(format, args);
		report.add("- " + text);
		System.out.println("   " + text);
	}

	private void writeReport() throws IOException {
		var lines = new ArrayList<String>();
		lines.add("# Passage du robot du " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
		lines.add("");
		lines.add("Jar : `" + jar + "`. Logs complets : `ladybug.log`.");
		lines.addAll(report);
		lines.add("");
		lines.add("## Statuts vus dans les logs, dans l'ordre");
		lines.add("");
		lines.add(String.join(" → ", statuses));
		Files.write(outputDir.resolve("rapport.md"), lines);
	}
}
