package eu.skyrex.util;

import eu.skyrex.Main;

import java.io.*;
import java.net.URL;

public class Resources {

    public static File getFileFromResource(String resourcePath) {
        URL resource = Main.class.getResource(resourcePath);
        if (resource == null) {
            throw new RuntimeException("Could not find resource " + resourcePath);
        }

        try (InputStream inputStream = resource.openStream()) {
            // Create a temporary file
            File tempFile = File.createTempFile("tempResource", ".tmp");
            tempFile.deleteOnExit(); // Ensure the temp file is deleted on exit

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read and create temp file from " + resourcePath, e);
        }
    }

}
