package radio.do1das.hamnetPortal.httpserver.site;

import org.eclipse.jetty.proxy.AsyncProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public class WebProxy extends AsyncProxyServlet {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebProxy.class);

    @Override
    protected String rewriteTarget(HttpServletRequest request) {
        String uriString;
        try {
            URI uri = new URI("http://do1das.ddns.de.ampr.org");
            uriString = uri.toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("URI STRING: " + uriString);
        return uriString;
    }

}
