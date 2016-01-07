import com.vevo.nibbler.Resizer;
import com.vevo.nibbler.Writer;
import com.vevo.nibbler.model.FileType;
import com.vevo.nibbler.model.ResizeParams;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * The main HTTP endpoint. Just a bare servlet. Keep things simple, I always say!
 */
public class Resource extends HttpServlet {
    private static URI base;
    private static final Client httpClient = ClientBuilder.newClient();

    public static void setBase(String base) {
        Resource.base = URI.create(base);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get the resize params.
        ResizeParams params = new ResizeParams(req.getParameterMap());

        if (params.width < 1 || params.height < 1) {
            error(resp, 400, "width and height must both be specified and non-zero");
            return;
        }

        // The requested file type comes off the end of the request.
        FileType type = FileType.fromString(req.getRequestURI());

        // Strip the extension since we use that to determine the OUTPUT file, not the INPUT file.
        String path = req.getRequestURI();

        for (String ext : new String[]{"png", "jpeg", "jpg", "bpg"}) {
            if (path.toLowerCase().endsWith(ext)) {
                path = path.substring(0, path.length() - ext.length() - 1);
                break;
            }
        }

        // Get the original image. Assume it has a PNG extension. You may want to change this depending on your server.
        Response imgResp = httpClient.target(base).path(path + ".png").request().get();

        if (imgResp.getStatus() != 200) {
            error(resp, 404, "not found");
            return;
        }

        // Resize.
        BufferedImage image = Resizer.resize(ImageIO.read((InputStream) imgResp.getEntity()), params);

        // And write it back out, taking care to pass along caching headers.
        resp.setStatus(200);
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Content-Type", type.getMimeType());

        if (imgResp.getHeaderString("Cache-Control") != null) {
            resp.setHeader("Cache-Control", imgResp.getHeaderString("Cache-Control"));
        }

        if (imgResp.getHeaderString("Last-Modified") != null) {
            resp.setHeader("Last-Modified", imgResp.getHeaderString("Last-Modified"));
        }

        Writer.write(image, type, req.getParameter("quality"), req.getParameter("bg"), resp.getOutputStream());
    }

    private void error(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("text/plain");
        resp.getWriter().println(message);
    }
}
