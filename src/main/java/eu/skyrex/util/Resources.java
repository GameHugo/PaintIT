package eu.skyrex.util;

import eu.skyrex.Main;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public static File getFolderFromZipResource(String resourcePath) {
        URL resource = Main.class.getResource(resourcePath);
        if (resource == null) {
            throw new RuntimeException("Could not find resource " + resourcePath);
        }

        try {
            // Create a temporary directory
            File tempDir = Files.createTempDirectory("tempResourceDir").toFile();
            tempDir.deleteOnExit(); // Ensure the temp folder is deleted on exit

            // Open the ZIP file from the resource stream
            try (InputStream inputStream = resource.openStream();
                 ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    File newFile = new File(tempDir, entry.getName());
                    if (entry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        // Ensure parent directories exist
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }

                        // Write file content
                        try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipInputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, len);
                            }
                        }
                    }
                    zipInputStream.closeEntry();
                }
            }

            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract and create temp folder from " + resourcePath, e);
        }
    }

}
