package com.kycox.game.engine.maze.repo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sauvegarde du dernier niveau atteint, pour proposer de reprendre la partie.
 *
 * <p>Par défaut dans {@code <dossier temporaire>/labybug/numLevel.txt} (nom
 * historique conservé pour ne pas perdre les sauvegardes existantes). La
 * propriété {@code ladybug.save.dir} choisit un autre dossier : les tests s'en
 * servent pour ne jamais écraser la sauvegarde du joueur (étape 3.5.2a).
 */
@Component
public class LevelRepository {
    private final String saveDirectory;

    public LevelRepository(@Value("${ladybug.save.dir:}") String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public void saveNumLevel(int numLevel) {
        try {
            Path numLevelPath = getLevelNumTmpFile();
            writeNumLevelInToPath(numLevel, numLevelPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeNumLevelInToPath(int numLevel, Path numLevelPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(numLevelPath)) {
            writer.write(Integer.toString(numLevel));
            writer.flush();
        }
    }

    public int getNumLevel() {
        try {
            Path numLevelPath = getLevelNumTmpFile();
            String read = Files.readAllLines(numLevelPath).get(0);
            return Integer.parseInt(read);
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    private Path getLevelNumTmpFile() {
        Path ladybugTmpDir = saveDirectory.isBlank()
                ? Paths.get(System.getProperty("java.io.tmpdir")).resolve("labybug")
                : Paths.get(saveDirectory);
        if (!ladybugTmpDir.toFile().exists()) {
            ladybugTmpDir.toFile().mkdirs();
        }
        return ladybugTmpDir.resolve("numLevel.txt");
    }
}
