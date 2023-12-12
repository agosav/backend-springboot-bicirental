# Proyecto de Backend para sistema de alquiler de bicicletas

## Descripción

Este proyecto fue desarrollado en el marco del Trabajo Práctico Integrador de la materia electiva Backend de Aplicaciones de la FRC UTN. El proyecto implementa un backend para un sistema de alquiler de bicicletas compuesto por 3 microservicios que expone una API REST con representación en JSON. 

### Tecnologías y herramientas utilizadas
- **Lenguaje de Programación:** Java
- **Frameworks:** Spring Boot e Hibernate (JPA)
- **Base de Datos:** SQLite
- **Documentación de API:** Swagger
- **Seguridad de Endpoints:** Keycloak
- **Servicio Externo para Conversión de Moneda:** API externa proporcionada por la facultad

### Autora

- Agostina Avalle

---

## Funcionalidades

1. **Consultar el listado de todas las estaciones disponibles en la ciudad.**
> GET /api/estaciones
2. **Consultar los datos de la estación más cercana a una ubicación provista por el cliente.**
> GET /api/estaciones/cercana?lat={latitud}&lon={longitud}
3. **Iniciar el alquiler de una bicicleta desde una estación dada.**
> POST /api/alquileres/iniciar/{estacionRetiroId}
4. **Finalizar un alquiler en curso, informando los datos del mismo y el costo expresado en la moneda que el cliente desee.**
   - La moneda puede ser elegida en el momento de finalizar el alquiler, y en caso de no hacerlo, el monto se expresa en pesos argentinos.
> POST /api/alquileres/finalizar/{estacionDevolucionId}
5. **Agregar una nueva estación al sistema.**
> POST /api/estaciones
6. **Obtener un listado de los alquileres realizados pudiendo aplicar filtros. (Estación de retiro, estación de devolución, id de cliente y/o moneda).**
> GET /api/alquileres/finalizados?clienteId={clienteId}&estacionRetiroId={estacionId}&estacionDevolucionId={estacionId}&moneda={moneda}

## Seguridad y Roles

- **API Gateway:** Implementa un único punto de entrada que expone todos los endpoints en el mismo puerto.
- **Autenticación:** Todas las llamadas a los distintos endpoints están permitidas solo para clientes autenticados.
- **Roles:**
  1. Administrador: Puede agregar nuevas estaciones y obtener listados sobre los alquileres realizados.
  2. Cliente: Puede realizar consultas sobre las estaciones, realizar alquileres y devoluciones.

## Documentación de la API

Todos los endpoints están documentados utilizando Swagger. Por ejemplo: http://localhost:8081/swagger-ui/index.html
- Microservicio de estaciones: puerto 8081
- Microservicio de alquileres: puerto 8082
- Microservicio API Gateway: puerto 8080

## Servicios Externos

- **Keycloak:** Utilizado para la seguridad de los endpoints según el rol del usuario.
- **Conversión de Moneda:** Se utiliza un servicio externo para la conversión de la moneda de los alquileres. Monedas aceptadas: USD, EUR, CLP, BRL, COP, PEN, GBP.

## Requerimientos del Dominio

- Cada cliente está representado con un único usuario.
- Un cliente no puede alquilar una bicicleta si ya tiene un alquiler activo.
- Se asume que siempre hay una bicicleta disponible en las estaciones, y que toda estación tiene lugar disponible para una devolución.
- El cliente decide en qué moneda se le mostrará el importe adeudado al momento de la devolución.
- El precio del alquiler se calcula en el momento de la devolución bajo las siguientes reglas:
  1. Hay un costo fijo por realizar el alquiler y un costo por hora completa. Existe una tabla en la base de datos que indica cuáles son estos costos por cada día de la semana.
  2. Se cobra un monto adicional por cada KM que separe las estaciones de retiro y de devolución.
  3. Hay días promocionales configurados en el sistema. 

## Aclaraciones Finales

- En la raíz del proyecto está el pdf provisto por la cátedra con la consigna completa del Trabajo Práctico.
