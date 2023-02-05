package com.kycox.game.level;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class RepositoryLevel {

    public void saveNumLevel(int numLevel) {
        try {
            Path pathToFile = getLevelNumTmpFile();
            pathToFile.toFile().delete();
            try (BufferedWriter writer = Files.newBufferedWriter(pathToFile)) {
                writer.write(Integer.toString(numLevel));
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public int getNumLevel() {
        try {
            Path pathToFile = getLevelNumTmpFile();
            String read = Files.readAllLines(pathToFile).get(0);
            return Integer.parseInt(read);
        } catch (IOException | NumberFormatException e) {
            saveNumLevel(0);
            return 0;
        }
    }

    private Path getLevelNumTmpFile() throws IOException {
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path ladybugTmpDir = tmpDir.resolve("labybug");
        if (!ladybugTmpDir.toFile().exists()) {
            ladybugTmpDir.toFile().mkdirs();
        }
        return ladybugTmpDir.resolve("numLevel.txt");
    }
}
