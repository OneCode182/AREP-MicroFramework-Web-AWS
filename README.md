# AREP-IOC-Reflection-Server

![Java](https://img.shields.io/badge/Java-17+-007396?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven&logoColor=white)
![Git](https://img.shields.io/badge/Git-Project-F05032?logo=git&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?logo=amazon-aws&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?logo=amazon-ec2&logoColor=white)

> Servidor HTTP en Java con microframework IoC basado en reflexión y anotaciones para exponer servicios REST con `@RestController`, `@GetMapping` y `@RequestParam`.

---

## Tabla de contenido

- [AREP-IOC-Reflection-Server](#arep-ioc-reflection-server)
  - [Tabla de contenido](#tabla-de-contenido)
  - [Resumen](#resumen)
  - [Arquitectura y diseño](#arquitectura-y-diseño)
  - [Estructura del proyecto](#estructura-del-proyecto)
  - [Requisitos](#requisitos)
  - [Instalación y ejecución](#instalación-y-ejecución)
  - [Uso del microframework](#uso-del-microframework)
  - [Pruebas](#pruebas)
  - [Evidencia de funcionamiento](#evidencia-de-funcionamiento)
  - [Evidencia de despliegue en AWS](#evidencia-de-despliegue-en-aws)
  - [Cumplimiento de rúbrica](#cumplimiento-de-rúbrica)
  - [Author](#author)
  - [License](#license)

---

## Resumen

Este proyecto implementa un prototipo de servidor de aplicaciones con los requerimientos del laboratorio:

- Servidor HTTP no concurrente.
- Entrega de archivos estáticos (`HTML`, `PNG`).
- Framework IoC para construir aplicaciones web desde POJOs.
- Detección automática de componentes por reflexión.
- Publicación de métodos REST con anotaciones.

El resultado es un servidor ejecutable y clonable que cumple estructura Maven, incluye pruebas automatizadas y documentación completa.

---

## Arquitectura y diseño

```mermaid
graph TD
    A[Cliente HTTP / Navegador] --> B[SimpleHttpServer]
    B --> C{Ruta GET registrada}
    C -->|Si| D[RouteRegistry]
    D --> E[MicroApplicationContext]
    E --> F[Controlador @RestController]
    F --> G[Método @GetMapping]
    G --> H[Resolución @RequestParam]
    H --> I[Respuesta HTTP]
    C -->|No| J[StaticFileService]
    J --> K[src/main/resources/public]
    K --> I
```

Componentes principales:

- `SimpleHttpServer`: ciclo de vida del socket, parsing básico de requests y envío de respuestas HTTP.
- `MicroApplicationContext`: carga de controladores, reflexión de métodos y registro de rutas.
- `ClasspathScanner`: exploración del classpath por paquete para detectar clases anotadas con `@RestController`.
- `RouteRegistry`: mapa de rutas GET a handlers.
- `StaticFileService`: resolución de recursos estáticos locales y desde classpath.
- `BeanContainer`: registro de instancias de controladores.

Decisiones de diseño relevantes:

- Separación de responsabilidades por capa (HTTP, IoC, routing, static files).
- Tipos inmutables para request y respuesta HTTP.
- Manejo explícito de errores por códigos HTTP (`404`, `405`, `500`).

---

## Estructura del proyecto

```text
AREP-IOC-Reflection-Server/
├── pom.xml
├── .gitignore
├── README.md
├── resources/
│   └── moodle.md
└── src/
    ├── main/
    │   ├── java/edu/escuelaing/arep/
    │   │   ├── Main.java
    │   │   ├── annotations/
    │   │   │   ├── RestController.java
    │   │   │   ├── GetMapping.java
    │   │   │   └── RequestParam.java
    │   │   ├── http/
    │   │   │   ├── HttpRequest.java
    │   │   │   └── HttpResponse.java
    │   │   ├── ioc/
    │   │   │   ├── BeanContainer.java
    │   │   │   ├── ClasspathScanner.java
    │   │   │   └── MicroApplicationContext.java
    │   │   ├── server/
    │   │   │   ├── RouteHandler.java
    │   │   │   ├── RouteRegistry.java
    │   │   │   ├── SimpleHttpServer.java
    │   │   │   └── StaticFileService.java
    │   │   └── app/controllers/
    │   │       ├── GreetingController.java
    │   │       └── HealthController.java
    │   └── resources/public/
    │       ├── index.html
    │       ├── styles.css
    │       └── images/sample.png
    └── test/java/edu/escuelaing/arep/
        ├── HttpRequestTest.java
        ├── MicroApplicationContextTest.java
        ├── StaticFileServiceTest.java
        └── SimpleHttpServerIntegrationTest.java
```

---

## Requisitos

- Java 17 o superior.
- Maven 3.x.
- Git.

---

## Instalación y ejecución

1. Clonar el repositorio:

```bash
git clone https://github.com/USER/AREP-IOC-Reflection-Server.git
cd AREP-IOC-Reflection-Server
```

2. Compilar y ejecutar pruebas:

```bash
mvn clean test
```

3. Empaquetar:

```bash
mvn clean package
```

4. Ejecutar servidor (escaneo automático del paquete de controladores):

```bash
java -cp "target/classes:target/dependency/*" edu.escuelaing.arep.Main
```

5. Ejecutar indicando clase específica (modo de carga explícita):

```bash
java -cp "target/classes:target/dependency/*" edu.escuelaing.arep.Main edu.escuelaing.arep.app.controllers.GreetingController
```

---

## Uso del microframework

Anotaciones soportadas:

- `@RestController`: marca una clase como componente web.
- `@GetMapping("/ruta")`: publica un endpoint GET.
- `@RequestParam(value="name", defaultValue="World")`: extrae parámetros query.

Ejemplo implementado:

```java
@RestController
public class GreetingController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}
```

Endpoints de ejemplo:

- `http://localhost:8080/greeting?name=Pedro`
- `http://localhost:8080/greeting`
- `http://localhost:8080/health`
- `http://localhost:8080/index.html`
- `http://localhost:8080/images/sample.png`

---

## Pruebas

Este proyecto incluye pruebas unitarias e integración para cubrir:

- Parsing de query params.
- Carga de controladores por classpath (`@RestController`).
- Registro de rutas por `@GetMapping`.
- Resolución de `@RequestParam` con y sin valor por defecto.
- Entrega de recursos estáticos (`HTML`, `PNG`).
- Flujo real servidor HTTP + endpoints.

Ejecución:

```bash
mvn clean test
```

Resultado esperado:

- Build success.
- Todas las pruebas en verde.

---

## Evidencia de funcionamiento

Sección preparada para que insertes capturas clave de alta calidad.

1) Evidencia de pruebas automatizadas (`mvn clean test`):

![Evidencia de pruebas automatizadas 1](./resources/img/cmd-mvn-1.png)
![Evidencia de pruebas automatizadas 2](./resources/img/cmd-mvn-2.png)

2) Endpoint REST con parámetro (`/greeting?name=Pedro`):

![Evidencia endpoint greeting con query param](./resources/img/cmd-hola-pedro.png)

3) Endpoint REST con valor por defecto (`/greeting`):

![Evidencia endpoint greeting default value](./resources/img/cmd-hello-default.png)

4) Recurso estático HTML (`/index.html`):

![Evidencia static index](./resources/img/view-html.png)

5) Recurso estático PNG (`/images/sample.png`):

![Evidencia static png](./resources/img/view-sample-img.png)

---

## Evidencia de despliegue en AWS

La rúbrica exige evidencia de despliegue correcto en AWS. Dejo la sección para anexar evidencia al finalizar tu despliegue:

1) Instancia EC2 operativa:

![Evidencia EC2](./resources/img/aws-ec2-panel.png)

2) Servidor ejecutándose en instancia remota:

![Evidencia proceso servidor en EC2](./resources/img/aws-ec2-server.png)

3) Consumo del endpoint desde cliente externo:

![Evidencia request remota](./resources/img/aws-ec2-endpoint-client.png)

4) Evidencia de static file en AWS:

![Evidencia static file en AWS](./resources/img/aws-ec2-img-sample.png)

---

## Cumplimiento de rúbrica

| Criterio | Estado |
| :--- | :---: |
| `@RestController` implementado | [x] |
| Exploración de classpath y carga automática | [x] |
| `@GetMapping` implementado | [x] |
| `@RequestParam` implementado | [x] |
| Requisitos funcionales adicionales | [x] |
| Atributos de calidad y diseño razonable | [x] |
| README con arquitectura, ejecución y pruebas | [x] |
| Pruebas automatizadas | [x] |
| Repositorio clonable y ejecutable | [x] |

Notas de entregables:

- El repositorio incluye `.gitignore`, `README.md`, `pom.xml` y estructura Maven.
- `target/` está excluido en `.gitignore`.
- No se incluyen carpetas de build innecesarias en el árbol fuente.

---

## Author

**Sergio Andrey Silva Rodriguez**  
*Systems Engineering Student*  
Escuela Colombiana de Ingeniería Julio Garavito

## License

This project is for educational purposes as part of the AREP course at Escuela Colombiana de Ingeniería Julio Garavito.