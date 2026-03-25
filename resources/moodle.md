# AREM: Taller de Arquitecturas de Servidores de Aplicaciones, Meta protocolos de objetos, Patrón IoC, Reflexión

## Descripción

Para este taller los estudiantes deberán construir un servidor Web (tipo Apache) en Java. El servidor debe ser capaz de entregar páginas html e imágenes tipo PNG. Igualmente el servidor debe proveer un framework IoC para la construcción de aplicaciones web a partir de POJOS. Usando el servidor se debe construir una aplicación Web de ejemplo. El servidor debe atender múltiples solicitudes no concurrentes.

Para este taller desarrolle un prototipo mínimo que demuestre las capacidades reflexivas de JAVA y permita por lo menos cargar un bean (POJO) y derivar una aplicación Web a partir de él.

Debe entregar su trabajo al final del laboratorio. Luego puede complementar para entregarlo en 8 días. Se verificara y compararán el commit del día de inicio del laboratorio y el dela entrega final.

---

## Sugerencia

1. **Carga de POJO desde la línea de comandos**: Para su primera versión cargue el POJO de manera similar al framework de TEST. Es decir, pásela como parámetro cuando invoque el framework. Ejemplo de invocación:
   
   ```bash
   java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot co.edu.escuelaing.reflexionlab.FirstWebService
   ```

2. **Atención a la anotación `@GetMapping`**: Publique el servicio en la URI indicada, limitándolo a tipos de retorno `String`. Ejemplo:
   
   ```java
   @RestController
   public class HelloController {
       @GetMapping("/")
       public String index() {
           return "Greetings from Spring Boot!";
       }
   }
   ```

3. **Exploración automática del classpath**: En su versión final, el framework debe explorar el directorio raíz (o classpath) buscando clases con una anotación que indique que son componentes (por ejemplo `@RestController`) y cargar todos los que tengan dicha anotación. Así no tendrá que especificarlos siempre en la línea de comandos.

4. **Soporte de Anotaciones**: Debe soportar `@GetMapping` y `@RequestParam`.

5. **Componente de ejemplo**: Debe ser posible implementar el siguiente componente:
   
   ```java
   @RestController
   public class GreetingController {
       private static final String template = "Hello, %s!";
       private final AtomicLong counter = new AtomicLong();

       @GetMapping("/greeting")
       public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
           return "Hola " + name;
       }
   }
   ```

---

## Entregables

1. El código fuente del proyecto y el ciclo de vida debe ser estructurado y manejado usando Maven.
2. El proyecto se debe almacenar en la cuenta de GitHub del estudiante.
3. Evidencia de que el servidor de aplicaciones se desplegó en AWS y se ejecutó correctamente.

---

---

## Rúbrica de Evaluación

| Deliverables | Reference | Evaluation |
| :--- | :---: | :---: |
| Deployed on GitHub | 1 | 1 |
| Complete .gitignore file | 1 | 1 |
| Has README.md | 1 | 1 |
| Contains no unnecessary files or folders | 1 | 1 |
| Has a POM.xml | 1 | 1 |
| Respects Maven structure | 1 | 1 |
| Does not contain the target folder | 1 | 1 |
| **Subtotal Deliverables** | **7** | **7** |

| Design and Architecture | Reference | Evaluation |
| :--- | :---: | :---: |
| Implements the @RestController annotation to identify components that define web services. | 3 | 3 |
| Explores the root directory (or classpath) searching for classes with an annotation indicating they are components, such as @RestController, and load all those that have this annotation. | 3 | 3 |
| Implements the @GetMapping annotation to mark methods that will handle REST services. | 3 | 3 |
| Implements the @RequestParam annotation to extract query parameters from HTTP requests. | 3 | 3 |
| Meets all other functional requirements | 3 | 3 |
| Meets quality attributes | 5 | 5 |
| System design seems reasonable for the problem | 3 | 3 |
| Design is well documented in the README.md | 3 | 3 |
| README contains installation and usage instructions | 3 | 3 |
| README shows evidence of tests | 3 | 3 |
| Has automated tests | 3 | 3 |
| Repository can be cloned and executed | 3 | 3 |
| **Subtotal Design** | **38** | **38** |

| Resumen | Points | Evaluation |
| :--- | :---: | :---: |
| **Total** | **45** | **45** |
| **Nota Final (Grade)** | **5** | **5** |

---

*Fuente: [Moodle - Escuela Colombiana de Ingeniería](https://campusvirtual.escuelaing.edu.co/moodle/mod/assign/view.php?id=33479)*
