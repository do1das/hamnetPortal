package radio.do1das.hamnetPortal.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class PortalHTTPHandler implements HttpHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortalHTTPHandler.class);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.info("Neue Verbindung von " +exchange.getRemoteAddress().toString());
        String response = "TEST";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
