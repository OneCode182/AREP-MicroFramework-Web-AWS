package edu.escuelaing.arep;

import edu.escuelaing.arep.ioc.MicroApplicationContext;
import edu.escuelaing.arep.server.SimpleHttpServer;
import edu.escuelaing.arep.server.StaticFileService;

public class Main {

    public static void main(String[] args) {
        int port = 8080;
        String packageToScan = "edu.escuelaing.arep.app.controllers";

        MicroApplicationContext context = new MicroApplicationContext();

        if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
            String firstArg = args[0].trim();
            if (firstArg.contains(".")) {
                context.loadSpecificClass(firstArg);
            } else {
                packageToScan = firstArg;
                context.loadFromPackage(packageToScan);
            }
        } else {
            context.loadFromPackage(packageToScan);
        }

        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                port = 8080;
            }
        }

        StaticFileService staticFileService = new StaticFileService();
        SimpleHttpServer server = new SimpleHttpServer(context.routeRegistry(), staticFileService);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start(port);
    }
}

