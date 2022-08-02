package radio.do1das.hamnetPortal.httpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PortalHTTPHandler implements HttpHandler {
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
    };

    private String filesystemRoot;
    private String urlPrefix;
    private String directoryIndex;

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
    public void handle(HttpExchange he) throws IOException {
        String method = he.getRequestMethod();
        if (! ("HEAD".equals(method) || "GET".equals(method) || "POST".equals(method))) {
            sendError(he, 501, "Unsupported HTTP method");
            return;
        }

        String wholeUrlPath = he.getRequestURI().getPath();
        if (wholeUrlPath.endsWith("/")) {
            wholeUrlPath += directoryIndex;
        }
        if (! wholeUrlPath.startsWith(urlPrefix)) {
            throw new RuntimeException("Path is not in prefix - incorrect routing?");
        }

        if (wholeUrlPath.endsWith("/postData")) {
            InputStream ios = he.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = ios.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            String parameters = result.toString("UTF-8");
            String[] input = parameters.split("&");
            //ToDo: input Array an Verarbeitungsmethode schicken

            String response = " <html>\n"
                    + "<body>\n"
                    + "\n"
                    + "<form action=\"http://localhost:8000/postData\" method=\"post\">\n"
                    + "input: <input type=\"text\" name=\"input\"><br>\n"
                    + "input2: <input type=\"text\" name=\"input2\"><br>\n"
                    + "<input type=\"submit\">\n"
                    + "</form>\n"
                    + "\n"
                    + "</body>\n"
                    + "</html> ";
            OutputStream os = he.getResponseBody();
            he.sendResponseHeaders(200, response.length());
            os.write(response.getBytes());
            os.close();
            return;
        }

        if (wholeUrlPath.equals("/proxy")) {
            //ToDo: Weiterleitung an Proxy
            return;
        }

        String urlPath = wholeUrlPath.substring(urlPrefix.length());
        String filePath = filesystemRoot + wholeUrlPath;

        if (! filePath.startsWith(filesystemRoot)) {
            reportPathTraversal(he);
            return;
        }

        InputStream fis;
        fis = getClass().getResourceAsStream(filePath);
        if(fis == null) {
            sendError(he, 404, "File not found");
            return;
        }

        String mimeType = lookupMime(urlPath);
        he.getResponseHeaders().set("Content-Type", mimeType);
        if ("GET".equals(method)) {
            he.sendResponseHeaders(200, fis.available());
            OutputStream os = he.getResponseBody();
            copyStream(fis, os);
            os.close();
        } else {
            assert("HEAD".equals(method));
            he.sendResponseHeaders(200, -1);
        }
        fis.close();
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) >= 0) {
            os.write(buf, 0, n);
        }
    }

    private void sendError(HttpExchange he, int rCode, String description) throws IOException {
        String message = "HTTP error " + rCode + ": " + description;
        byte[] messageBytes = message.getBytes("UTF-8");

        he.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        he.sendResponseHeaders(rCode, messageBytes.length);
        OutputStream os = he.getResponseBody();
        os.write(messageBytes);
        os.close();
    }

    private void reportPathTraversal(HttpExchange he) throws IOException {
        sendError(he, 400, "Path traversal attempt detected");
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
