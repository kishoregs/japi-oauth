# japi-oauth


mvn spring-boot:run -Dspring-boot.run.profiles=local

mvn clean package



pom.xml:
Configured for WAR packaging
Includes spring-boot-starter-tomcat with provided scope
Has a local profile defined
DemoApplication.java:
Extends SpringBootServletInitializer for WAR deployment
Overrides configure method for external server deployment
application.properties:
Contains shared configurations
Uses spring.main.web-application-type=servlet for embedded server
WebSphere-specific line is commented out
application-local.properties:
Contains local-specific settings
Explicitly sets spring.main.web-application-type=servlet for local development
The setup looks correct for both scenarios. However, to ensure smooth operation in both environments, consider the following adjustments:
1. Update application.properties:
Update application-local.properties:
Add a new application-websphere.properties:
4. Update the pom.xml to include a WebSphere profile:
With these changes:

```bash
local development: mvn spring-boot:run -Dspring-boot.run.profiles=local
WebSphere deployment: mvn clean package -Pwebsphere
```
This setup ensures that the application can run both locally with the embedded server and on WebSphere with the appropriate configurations.