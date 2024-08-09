package eu.skyrex.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ServerProperties {
    private int port = 25565;
    private String ip = "0.0.0.0";

    Logger logger = LoggerFactory.getLogger(ServerProperties.class);

    public ServerProperties() {
        File file = new File("server.properties");
        if(file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.startsWith("server-port=")) {
                        port = Integer.parseInt(line.substring(12));
                    } else if(line.startsWith("server-ip=")) {
                        ip = line.substring(10);
                    }
                }
            } catch (FileNotFoundException e) {
                logger.error("Tried to read server.properties but it doesn't exist");
                throw new RuntimeException(e);
            }
        }
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
