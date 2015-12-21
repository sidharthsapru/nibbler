package com.vevo.nibbler;

import com.vevo.nibbler.model.FileType;
import com.vevo.nibbler.model.ResizeParams;
import com.vevo.nibbler.model.Resizer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.awt.*;
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
        ResizeParams params = new ResizeParams(req);

        if (params.width * params.height < 1) {
            error(resp, 400, "width and height must both be specified and non-zero");
            return;
        }

        // The requested file type comes off the end of the request.
        FileType type = FileType.fromString(req.getRequestURI());

        // Get the quality.
        float quality = 0.7f;

        if (req.getParameter("quality") != null) {
            try {
                int intQuality = Integer.parseInt(req.getParameter("quality"));
                quality = ((float) intQuality) / 100f;
            } catch (Exception e) { /* oh well */ }
        }

        // Strip the extension since we use that to determine the OUTPUT file, not the INPUT file.
        String path = req.getRequestURI();

        for (String ext : new String[]{"png", "jpeg", "jpg"}) {
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

        // If we're going to write out a JPEG (or a background was asked for), we need to replace transparency.
        if (type == FileType.JPEG || req.getParameter("bg") != null) {
            Color bg = Color.white;

            // Try out best to use the background color the user wants.
            if (req.getParameter("bg") != null) {
                String bgHex = req.getParameter("bg");

                if (!bgHex.startsWith("#")) {
                    bgHex = "#" + bgHex;
                }

                try {
                    bg = Color.decode(bgHex);
                } catch (NumberFormatException e) { /* oh well */ }
            }

            BufferedImage cleaned = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = cleaned.createGraphics();
            graphics2D.setPaint(bg);
            graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics2D.drawImage(image, 0, 0, null);
            image = cleaned;
        }

        // And write it back out, taking care to pass along caching headers.
        resp.setStatus(200);
        resp.setHeader("Content-Type", type.getMimeType());

        if (imgResp.getHeaderString("Cache-Control") != null) {
            resp.setHeader("Cache-Control", imgResp.getHeaderString("Cache-Control"));
        }

        if (imgResp.getHeaderString("Last-Modified") != null) {
            resp.setHeader("Last-Modified", imgResp.getHeaderString("Last-Modified"));
        }

        ImageWriter writer = ImageIO.getImageWritersByFormatName(type.getExtension()).next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        if (type == FileType.JPEG) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(quality);
        }

        ImageOutputStream os = new MemoryCacheImageOutputStream(resp.getOutputStream());
        writer.setOutput(os);
        IIOImage outputImage = new IIOImage(image, null, null);
        writer.write(null, outputImage, writeParam);
        writer.dispose();
        os.close();
    }

    private void error(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("text/plain");
        resp.getWriter().println(message);
    }
}
