package radio.do1das.hamnetPortal.httpserver;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PortalHTTPHandler extends AbstractHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(PortalHTTPHandler.class);

    private static final Map<String,String> MIME_MAP = new HashMap<>();
    static {
        MIME_MAP.put("appcache", "text/cache-manifest");
        MIME_MAP.put("css", "text/css");
        MIME_MAP.put("gif", "image/gif");
        MIME_MAP.put("html", "text/html");
        MIME_MAP.put("js", "application/javascript");
        MIME_MAP.put("json", "application/json");
        MIME_MAP.put("jpg", "image/jpeg");
        MIME_MAP.put("jpeg", "image/jpeg");
        MIME_MAP.put("mp4", "video/mp4");
        MIME_MAP.put("pdf", "application/pdf");
        MIME_MAP.put("png", "image/png");
        MIME_MAP.put("svg", "image/svg+xml");
        MIME_MAP.put("xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_MAP.put("xml", "application/xml");
        MIME_MAP.put("zip", "application/zip");
        MIME_MAP.put("md", "text/plain");
        MIME_MAP.put("txt", "text/plain");
        //MIME_MAP.put("php", "text/plain");
    }

    private final String filesystemRoot;
    private final String urlPrefix;
    private final String directoryIndex;

    public PortalHTTPHandler(String urlPrefix, String filesystemRoot, String directoryIndex) {
        if (!urlPrefix.startsWith("/")) {
            throw new RuntimeException("pathPrefix does not start with a slash");
        }
        if (!urlPrefix.endsWith("/")) {
            throw new RuntimeException("pathPrefix does not end with a slash");
        }
        this.urlPrefix = urlPrefix;

        assert filesystemRoot.endsWith("/");
        this.filesystemRoot = filesystemRoot;

        this.directoryIndex = directoryIndex;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
        String method = request.getMethod();
        String wholeUrlPath = request.getPathInfo();
        if (! ("HEAD".equals(method) || "GET".equals(method) || ("POST".equals(method) && wholeUrlPath.endsWith("/postData")))) {
            sendError(response, 501, "Unsupported HTTP method");
            request.setHandled(true);
            LOGGER.warn("(501) Versuchter Zugriff durch nicht unterstützte Methode " + method + " von " + request.getRemoteHost());
            return;
        }

        if (! wholeUrlPath.startsWith(urlPrefix)) {
            throw new RuntimeException("Path is not in prefix - incorrect routing?");
        }

        if (wholeUrlPath.endsWith("/footer.html") || wholeUrlPath.endsWith("/header.html")) {
            sendError(response, 401, "Forbidden");
            request.setHandled(true);
            LOGGER.warn("(401) Versuchter Zugriff auf verbotene Datei " + request.getPathInfo() + " von " + request.getRemoteHost());
            return;
        }

        String filePath = filesystemRoot + wholeUrlPath;

        if (! filePath.startsWith(filesystemRoot)) {
            reportPathTraversal(response);
            request.setHandled(true);
            LOGGER.warn("Versuchter PathTraversal " + filePath + " von " + request.getRemoteHost());
            return;
        }

        if (wholeUrlPath.endsWith("/")) {
            filePath += directoryIndex;
            wholeUrlPath += directoryIndex;
        }
        InputStream fis;
        fis = getClass().getResourceAsStream(filePath);

        if(fis == null) {
            return;
        }

        String urlPath = wholeUrlPath.substring(urlPrefix.length());
        String mimeType = lookupMime(urlPath);
        response.setHeader("Content-Type", mimeType);

        if(mimeType.equals("text/html")){
            fis = mergeStream(fis);
        }

        if ("GET".equals(method)) {
            LOGGER.info("(200) Zugriff auf " + request.getPathInfo() + " von " + request.getRemoteHost());
            response.setStatus(200);
            OutputStream os = response.getOutputStream();
            copyStream(fis, os);
            os.close();
        } else {
            assert("HEAD".equals(method));
            response.setStatus(200);
        }
        fis.close();
        request.setHandled(true);

    }

    private InputStream mergeStream(InputStream body){
        if (body == null) return null;
        InputStream header, footer;
        header = getClass().getResourceAsStream(filesystemRoot + "/header.html");
        footer = getClass().getResourceAsStream(filesystemRoot + "/footer.html");
        Vector<InputStream> streams = new Vector<>();
        streams.add(header);
        streams.add(body);
        streams.add(footer);
        return new SequenceInputStream(streams.elements());
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) >= 0) {
            os.write(buf, 0, n);
        }
    }

    private void sendError(HttpServletResponse response, int rCode, String description) throws IOException {
        response.sendError(rCode, description);
    }

    private void reportPathTraversal(HttpServletResponse response) throws IOException {
        sendError(response, 400, "Path traversal attempt detected");
    }

    private static String getExt(String path) {
        int slashIndex = path.lastIndexOf('/');
        String basename = (slashIndex < 0) ? path : path.substring(slashIndex + 1);

        int dotIndex = basename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return basename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }

    private static String lookupMime(String path) {
        String ext = getExt(path).toLowerCase();
        return MIME_MAP.getOrDefault(ext, "application/octet-stream");
    }

}
