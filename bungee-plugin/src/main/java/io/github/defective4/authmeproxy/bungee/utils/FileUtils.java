package io.github.defective4.authmeproxy.bungee.utils;

import java.io.File;
import java.io.IOException;

/**
 * File utilities.
 */
public final class FileUtils {

    // Utility class
    private FileUtils() {
    }

    /**
     * Creates the given file or throws an exception.
     *
     * @param file the file to create
     */
    public static void create(File file) {
        try {
            file.getParentFile().mkdirs();
            boolean result = file.createNewFile();
            if (!result) {
                throw new IllegalStateException("Could not create file '" + file + "'");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error while creating file '" + file + "'", e);
        }
    }
}
