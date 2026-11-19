package com.kycox.game.testutil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Golden master (étape 3.5.2d) : le journal d'une partie est comparé à un
 * journal de référence, validé une fois par Frédéric (décision C32) et rangé
 * dans {@code src/test/resources/golden/}.
 *
 * <ul>
 * <li>référence absente : le journal est écrit dans {@code target/golden/} comme
 * candidat, et le test échoue en le disant ;</li>
 * <li>écart : le test échoue et montre la première ligne qui diffère ; le journal
 * obtenu est écrit dans {@code target/golden/<nom>.actual.txt} ;</li>
 * <li>changement voulu d'une règle : {@code mvn test -Dgolden.update=true}
 * réécrit les références (à relire avant de commiter).</li>
 * </ul>
 */
public final class GoldenMaster {
	static final Path REFERENCE_DIRECTORY = Path.of("src", "test", "resources", "golden");
	static final Path OUTPUT_DIRECTORY = Path.of("target", "golden");
	/** Lignes affichées autour du premier écart. */
	private static final int CONTEXT = 3;

	/** Vérifie que le journal {@code actual} est identique à la référence {@code name}. */
	public static void assertMatches(String name, String actual) {
		var reference = REFERENCE_DIRECTORY.resolve(name + ".txt");
		if (Boolean.getBoolean("golden.update")) {
			write(reference, actual);
			return;
		}
		if (!Files.exists(reference)) {
			var candidate = write(OUTPUT_DIRECTORY.resolve(name + ".txt"), actual);
			throw new AssertionError("No reference for golden master '" + name + "'. Candidate written to "
			        + candidate.toAbsolutePath() + " : check it, then run with -Dgolden.update=true.");
		}
		// fins de ligne ignorées (\r\n sous Windows, \n ailleurs) : seul le contenu compte
		var expected = withUnixLineEnds(read(reference));
		actual = withUnixLineEnds(actual);
		if (!expected.equals(actual)) {
			var actualFile = write(OUTPUT_DIRECTORY.resolve(name + ".actual.txt"), actual);
			throw new AssertionError("Golden master '" + name + "' differs from its reference.\n"
			        + firstDifference(expected.lines().toList(), actual.lines().toList())
			        + "Full game journal: " + actualFile.toAbsolutePath());
		}
	}

	/** Première ligne différente, avec quelques lignes de contexte. */
	static String firstDifference(List<String> expected, List<String> actual) {
		var index = 0;
		while (index < expected.size() && index < actual.size() && expected.get(index).equals(actual.get(index))) {
			index++;
		}
		var report = new StringBuilder("First difference at line " + (index + 1) + ":\n");
		for (var i = Math.max(0, index - CONTEXT); i < index; i++) {
			report.append("    ").append(expected.get(i)).append('\n');
		}
		report.append("  - expected: ").append(index < expected.size() ? expected.get(index) : "(end of journal)")
		        .append('\n');
		report.append("  + actual:   ").append(index < actual.size() ? actual.get(index) : "(end of journal)")
		        .append('\n');
		return report.toString();
	}

	static String withUnixLineEnds(String text) {
		return text.replace("\r\n", "\n");
	}

	private static String read(Path file) {
		try {
			return Files.readString(file);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Path write(Path file, String text) {
		try {
			Files.createDirectories(file.getParent());
			return Files.writeString(file, text);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private GoldenMaster() {
	}
}
