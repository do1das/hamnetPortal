package radio.do1das.hamnetPortal.httpserver;


import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class PortalHTTP {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortalHTTP.class);

    public static void create(Server server, String path, String filesystemRoot, String directoryIndex) {
        PortalHTTPHandler phh = new PortalHTTPHandler(path, filesystemRoot, directoryIndex);
        server.setHandler(phh);
    }
    public static void main(String[] args) {

        LOGGER.info("HTTP Server startet...");

        CommandLine commandLine;
        Option optionPort = Option.builder("p")
                .argName("Portnummer")
                .hasArg()
                .required(false)
                .desc("Port des HTTPServers")
                .longOpt("port")
                .build();
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        options.addOption(optionPort);
        int port = 8000;

        try {
            commandLine = parser.parse(options, args);
            if (commandLine.hasOption("p") && commandLine.getOptionValue("p") != null) {
                int desiredPort = Integer.parseInt(commandLine.getOptionValue("p"));
                if (desiredPort > 0 && desiredPort < 49151) {
                    port = desiredPort;
                }
            }
        } catch (ParseException e) {
            LOGGER.error("Fehler beim Parsen der Parameter", e);
        }

        Server httpServer = new Server(port);
        create(httpServer, "/", "/html", "index.html");
        try {
            httpServer.start();
        } catch (Exception e) {
            LOGGER.error("Problem beim Starten des HTTP Servers", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("HTTP Server erfolgreich gestartet und lauscht auf Port " +port);
    }

}
