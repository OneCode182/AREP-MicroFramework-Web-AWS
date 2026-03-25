package edu.escuelaing.arep.ioc;

import edu.escuelaing.arep.annotations.GetMapping;
import edu.escuelaing.arep.annotations.RequestParam;
import edu.escuelaing.arep.http.HttpRequest;
import edu.escuelaing.arep.http.HttpResponse;
import edu.escuelaing.arep.server.RouteRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MicroApplicationContext {

    private final BeanContainer beanContainer = new BeanContainer();
    private final RouteRegistry routeRegistry = new RouteRegistry();
    private final ClasspathScanner classpathScanner = new ClasspathScanner();

    public void loadFromPackage(String basePackage) {
        List<Class<?>> controllers = classpathScanner.findRestControllers(basePackage);
        for (Class<?> controllerClass : controllers) {
            registerController(controllerClass);
        }
    }

    public void loadSpecificClass(String className) {
        try {
            Class<?> controllerClass = Class.forName(className);
            registerController(controllerClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Controller class not found: " + className, e);
        }
    }

    private void registerController(Class<?> controllerClass) {
        if (!controllerClass.isAnnotationPresent(edu.escuelaing.arep.annotations.RestController.class)) {
            throw new IllegalArgumentException("Class is not annotated with @RestController: " + controllerClass.getName());
        }
        try {
            Object instance = controllerClass.getDeclaredConstructor().newInstance();
            beanContainer.registerBean(controllerClass, instance);
            registerControllerMethods(instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate controller: " + controllerClass.getName(), e);
        }
    }

    private void registerControllerMethods(Object controller) {
        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method method : methods) {
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            if (mapping == null) {
                continue;
            }

            String path = mapping.value();
            routeRegistry.registerGet(path, request -> invokeControllerMethod(controller, method, request));
        }
    }

    private HttpResponse invokeControllerMethod(Object controller, Method method, HttpRequest request) {
        try {
            Object[] args = buildArguments(method, request);
            Object result = method.invoke(controller, args);
            if (!(result instanceof String)) {
                return HttpResponse.internalError("Controller methods must return String");
            }
            String content = (String) result;
            return HttpResponse.ok("text/plain; charset=UTF-8", content.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalAccessException | InvocationTargetException e) {
            return HttpResponse.internalError("Error invoking controller method: " + e.getMessage());
        }
    }

    private Object[] buildArguments(Method method, HttpRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);

            if (requestParam == null) {
                throw new IllegalStateException("All controller params must use @RequestParam: " + method.getName());
            }

            String value = request.getQueryParam(requestParam.value());
            if (value == null || value.isEmpty()) {
                value = requestParam.defaultValue();
            }
            args[i] = value;
        }

        return args;
    }

    public RouteRegistry routeRegistry() {
        return routeRegistry;
    }

    public BeanContainer beanContainer() {
        return beanContainer;
    }

    public int totalRegisteredGetRoutes() {
        return routeRegistry.countGetRoutes();
    }
}
