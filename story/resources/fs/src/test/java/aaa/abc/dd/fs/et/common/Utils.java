package aaa.abc.dd.fs.et.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {
    public static File tempDirectory(Path parent, String prefix) {
        File file;
        prefix = prefix == null ? "kafka-" : prefix;
        try {
            file = parent == null ?
                    Files.createTempDirectory(prefix).toFile() : Files.createTempDirectory(parent, prefix).toFile();
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to create a temp dir", ex);
        }
        file.deleteOnExit();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> Utils.delete(file)));

        return file;
    }

    public static void delete(File file) {
        file.deleteOnExit();
    }
}
