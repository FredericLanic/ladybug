package com.kycox.game.level.repo;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LevelRepository {

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
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path ladybugTmpDir = tmpDir.resolve("labybug");
        if (!ladybugTmpDir.toFile().exists()) {
            ladybugTmpDir.toFile().mkdirs();
        }
        return ladybugTmpDir.resolve("numLevel.txt");
    }
}
