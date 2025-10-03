# Configuración de Liquibase para Spring Boot

Agrega lo siguiente en tu archivo `application.properties` para que Spring Boot use Liquibase y el changelog creado:

```
spring.liquibase.change-log=classpath:db/changelog/changelog.xml
```

Asegúrate de tener el driver de tu base de datos y la dependencia de Liquibase en tu `pom.xml`.
