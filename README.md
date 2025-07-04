### Pair of employees who have worked together

REST API and JavaScript frontend

#### To run the application
* Run the docker-compose.yml file for the PostgreSQL database
* Run EmployeesApplication.java
* For Swagger interface, browse: http://localhost:8080/api/v1/swagger-ui/index.html#/ 
* Frontend is in /resources/static. Browse: http://localhost:8080/api/v1/index.html

#### Pending improvements:

* Implement handling of NULL in the CSV file
* I've used an uproach of processing the file in chunks, but it finds the pairs only in the chunks. Needs a logic to keep current findings and analyze them with next chunks.
* Currently collects all common projects of two employees, not the longest period of time
* More tests are needed
* Mappers to convert from API contract to entities and backward (MapStruct)
* Authentication/Authorization
* Don't use native SQL query in the JPA repository
* Brand DB credentials from environment variables
* Introduce DB schema versioning (Flyway)
* Frontend is really basic and with hardcoded paths