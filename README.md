# Modular monoliths

This repository explores different approaches to implement a modular monolith and sets them in contrast with a classical monolith that implements a layered architecture.

## Business Use Case

The use case is part of a manufacturing resources planning (MRP) project. It is a simplified subset of a real-world application and helps illustrating some of the important aspects.  

* The product manager defines a product.
* The product manager submits a production order to have a certain quantity of the product produced.
* The manufacturer accepts the production order and indicates the expected delivery date.
* The manufacturer marks a production order as completed.
* The product inventory is increased by the number of produced items.

## Implementation approaches

All implementations offer the functionality described above in the business use case. They are all based on the Spring framework, project Lombok, etc. See dependencies in the parent pom.

All projects contain more ore less the same classes and use the same package names as far as possible. This makes it easy to compare implementation of, for example, an aggregate in the different approaches.

Features present in all approaches:
* persistence of the domain model
* REST-API using hyperlinks to indicate possible actions on every resource (HATEOAS)
* HAL browser under http://localhost:8080/api
* OpenAPI / Swagger UI under http://localhost:8080/swagger-ui.html
* Spring Boot actuators to introspect application state at runtime

### Classical monolith

The classical monolith structures top-level packages by technical layer (model, repository, service, web). As all entities reside in the same package, so they easily mess up when the project evolves. Cohesion in these technical layers is low, as classes of the layer are concerned about all kinds of functionality. 

The implementation uses Spring Data JPA for persistence, with the related jakarta.persistence annotations in the model objects. The entities also require an all args or a no arg constructor, scattering them further with technical details that distract away from the business they implement. 

### modulith-base

The base implementation of the modulith uses plain Spring framework with Spring Data JDBC for OR mapping. As the latter was designed with domain driven design in mind, the aggregates feel more lightweight as compared to the ones in the classical monolith. Wrapping of identifiers is however not easily possible, as the fields of the entities must be of standard Java types.

As an important difference to the classical monolith, this implementation uses a top-level package structure oriented at business features, such as masterdata, production orders, or product inventory. Repositories are named by the plural form of the related aggregate, such as "Products" or "ProductionOrders" (instead of "ProductRepository" or "ProductionOrderRepository"), keeping the language business oriented in the domain layer and avoiding technical terms as much as possible. 

Since aggregate, repository and service reside in the same package, the code makes great use of package private classes and methods: everything that is not part of the public API of the module is package private, leading to a strong but natural encapsulation of the internals of the module.


### modulith-with-archunit

The base modulith cannot enforce dependency rules on the modules. We want to keep them cycle free and enforce for example that the common package does not access any other package. To impose such rules, the modulith-with-archunit uses the [ArchUnit](https://www.archunit.org/) library to implement simple but strong rules based on packages to impose the desired dependency checks.

### modulith-with-spring-modulith

The [Spring Modulith](https://github.com/spring-projects-experimental/spring-modulith) library employs some simple conventions to implement a modular monolith: all top-level packages are by default a module. The @SpringBootApplication annotation on the main class is complemented with the `@Modulith` annotation. Furthermore, `@ApplicationModuleTest` allows to bootstrap one module only when running integration tests, which can lead to a mayor performance boost when working on larger moduliths. When executing a module tests, any violations in terms of not allowed accesses are reported as test failures. Check out the reference documentation [here](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/).

### modulith-with-jmolecules

The last implementation uses the [jMolecules](https://github.com/xmolecules/jmolecules) library to implement the modulith. The package structure is the same as in the other approaches, but now the architectural concepts are made explicit using annotation of the Onion architecture layers on the related packages. DDD concepts like aggregates or domain events are expressed by implementing the respective interfaces. 

This 'architecture as code' approach allows the usage of tools from the [jMolecules-integrations](https://github.com/xmolecules/jmolecules-integrations) library. The ByteBuddy plugin, for example, instruments the aggregates with the required jakarta.persistence annotations to make them persistable using spring-data-jpa with Hibernate, while the source code remains pure and business-centric. ID-wrappers such as ´ProductIdentifier´ or ´ProductionOrderIdentifier´ can be used directly in the owning aggregate. 

The ´ArchitectureTests´ class sets then up rules to validate both the Onion architecture and the DDD model. Any breach would immediately be reported when running the unit tests. 

Alternatively, the jQAssistant plugin can be used to validate concepts such as DDD or onion architecture and to generate PLantUML documentation for the architecture and modules.

_Note: To execute the jQAssistant functionality, make sure that Graphwiz is installed on your computer and that dot.exe is on the path. You can then activate the jqa profile like this `mvn clean verify -Pjqa -DskipTests`. Generated documentation can then be found under modulith-with-jmolecules/target/jqassistant/report/asciidoc/. _ 

# Conclusion

Domain driven design and libraries like Moduliths or jMolecules help us to design software in a way that is consistent with the business model. Where as ambitious DDD projects often failed when it came to persistence of aggregates, we have now the libraries at hand to make it work end to end. If you want to go lightweight, spring-data-jdbc is currently possibly the best persistence option to implement a modular monolith in Java, as it was specifically designed to support DDD. 

If you still want to make use of JPA and features like the Hibernate cache, jMolecules with the ByteBuddy plugin is an excellent solution to keep your aggregates free of technical code. Using its interfaces and / or annotations to make your architecture visible directly in the code will not only help developers to more easily grasp the concepts, but also lays a basis to validate architectural constraints or to generate documentation directly out of the code, thus bridging to long feared architecture code gap.

# License

MIT