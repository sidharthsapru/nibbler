import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * Entry point for the app.
 */
public class Service {
    public static void main(String[] args) throws Exception
    {
        // Set the base URL from the command line.
        if (args.length < 1) {
            System.out.println("Please pass the base URL of your image server as the first argument.");
            return;
        }

        Resource.setBase(args[0]);

        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(Resource.class, "/*");

        server.start();
        server.join();
    }
}
