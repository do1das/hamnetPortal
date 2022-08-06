package radio.do1das.hamnetPortal.httpserver.site;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class PostData extends Site {
    private final static Logger LOGGER = LoggerFactory.getLogger(PostData.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.isNew())
            LOGGER.info("Neue Session: " + session.getId());
        else
            LOGGER.info("Alte Session: " + session.getId());
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.getWriter().println("OK");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.isNew())
            LOGGER.info("Neue Session: " + session.getId());
        else
            LOGGER.info("Alte Session: " + session.getId());
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        String postType = request.getParameter("type");
        if(postType == null) {
            response.sendError(400);
            LOGGER.info("(400) Bad Request (null) auf " + request.getRequestURI() + " von " + request.getRemoteHost());
            return;
        }
        if ("checkUser".equals(postType)) {
            checkUser(response);
        } else {
            response.sendError(400);
            LOGGER.info("(400) Bad Request auf " + request.getRequestURI() + " von " + request.getRemoteHost());
            return;
        }

        LOGGER.info("(200) Zugriff auf " + request.getRequestURI() + " von " + request.getRemoteHost());
    }

    private void checkUser(HttpServletResponse response) throws IOException {

        response.getWriter().println("OK");
    }
}
