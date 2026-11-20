package com.kycox.game.launcher;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Où le jeu cherche ses fichiers externes (étapes 2.3 et 2.4) : convention
 * Spring Boot, {@code config/} puis le dossier courant, sinon le jar.
 */
class ConfigLocationsTest {
	@TempDir
	Path workingDirectory;

	@Test
	void withoutExternalFileTheJarIsUsed() {
		assertThat(ConfigLocations.springXmlLocation(workingDirectory)).isEqualTo("classpath:application-context.xml");
		assertThat(ConfigLocations.findExternal(workingDirectory, "ladybug.properties")).isEmpty();
	}

	@Test
	void fileInWorkingDirectoryWins() throws IOException {
		var xml = Files.writeString(workingDirectory.resolve("application-context.xml"), "<beans/>");

		assertThat(ConfigLocations.springXmlLocation(workingDirectory)).isEqualTo(xml.toUri().toString())
		        .startsWith("file:");
	}

	@Test
	void configSubdirectoryWinsOverWorkingDirectory() throws IOException {
		Files.writeString(workingDirectory.resolve("ladybug.properties"), "ghosts.hat=0");
		var inConfig = Files.writeString(Files.createDirectory(workingDirectory.resolve("config"))
		        .resolve("ladybug.properties"), "ghosts.hat=1");

		assertThat(ConfigLocations.findExternal(workingDirectory, "ladybug.properties")).contains(inConfig);
	}

	@Test
	void commandLineOptionIsReportedAsTheXmlInUse() {
		String[] args = { "--other=1", "--ladybug.config=file:/tmp/mine.xml" };

		assertThat(ConfigLocations.effectiveSpringXml(args, "classpath:application-context.xml"))
		        .isEqualTo("file:/tmp/mine.xml");
		assertThat(ConfigLocations.effectiveSpringXml(new String[0], "classpath:application-context.xml"))
		        .isEqualTo("classpath:application-context.xml");
	}

	@Test
	void aDirectoryWithTheSameNameIsIgnored() throws IOException {
		Files.createDirectory(workingDirectory.resolve("application-context.xml"));

		assertThat(ConfigLocations.springXmlLocation(workingDirectory)).isEqualTo("classpath:application-context.xml");
	}
}
