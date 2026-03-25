package edu.escuelaing.arep.ioc;

import edu.escuelaing.arep.annotations.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClasspathScanner {

    public List<Class<?>> findRestControllers(String basePackage) {
        List<Class<?>> result = new ArrayList<>();
        String packagePath = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (!"file".equals(resource.getProtocol())) {
                    continue;
                }
                String decodedPath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
                File root = new File(decodedPath);
                scanDirectory(basePackage, root, result);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan classpath for package: " + basePackage, e);
        }

        return result;
    }

    private void scanDirectory(String packageName, File directory, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(packageName + "." + file.getName(), file, classes);
                continue;
            }
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            String simpleName = file.getName().substring(0, file.getName().length() - 6);
            String className = packageName + "." + simpleName;
            try {
                Class<?> candidate = Class.forName(className);
                if (candidate.isAnnotationPresent(RestController.class)) {
                    classes.add(candidate);
                }
            } catch (ClassNotFoundException ignored) {
                // Ignore classes not loadable in current runtime context.
            }
        }
    }
}

