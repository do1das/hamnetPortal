package radio.do1das.hamnetPortal.httpserver;


import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PortalHTTP {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortalHTTP.class);

    public static void create(Server server, String path, String filesystemRoot, String directoryIndex) {
        HandlerList handlers = new HandlerList();

        SessionHandler sh = fileSessionHandler();
        ServletContextHandler servletHandlers = new ServletContextHandler();
        servletHandlers.setSessionHandler(sh);
        servletHandlers.addServlet("radio.do1das.hamnetPortal.httpserver.site.PostData", "/postData");
        servletHandlers.addServlet("radio.do1das.hamnetPortal.httpserver.site.WebProxy", "/proxy/*");
        
        PortalHTTPHandler phh = new PortalHTTPHandler(path, filesystemRoot, directoryIndex);
        handlers.addHandler(phh);
        handlers.addHandler(servletHandlers);
        server.setHandler(handlers);
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
        try {
            httpServer.join();
        } catch (InterruptedException e) {
            LOGGER.error("Problem beim Stoppen des HTTP Servers", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("HTTP Server erfolgreich gestoppt");
    }

    private static SessionHandler fileSessionHandler() {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(fileSessionDataStore());
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        sessionHandler.getSessionCookieConfig().setHttpOnly(true);
        sessionHandler.getSessionCookieConfig().setSecure(true);
        sessionHandler.getSessionCookieConfig().setComment("__SAME_SITE_STRICT__");
        // make additional changes to your SessionHandler here
        return sessionHandler;
    }

    private static FileSessionDataStore fileSessionDataStore() {
        FileSessionDataStore fileSessionDataStore = new FileSessionDataStore();
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File storeDir = new File(baseDir, "hamnetPortal-session-store");
        storeDir.mkdir();
        fileSessionDataStore.setStoreDir(storeDir);
        return fileSessionDataStore;
    }

}
