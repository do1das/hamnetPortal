package radio.do1das.hamnetPortal.httpserver;


import com.sun.net.httpserver.HttpServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PortalHTTP {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortalHTTP.class);
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

        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            LOGGER.error("Problem beim Starten des HTTP Servers", e);
            throw new RuntimeException(e);
        }
        httpServer.createContext("/", new PortalHTTPHandler());
        httpServer.setExecutor(null);
        httpServer.start();
        LOGGER.info("HTTP Server erfolgreich gestartet und lauscht auf Port " +port);
    }

}
