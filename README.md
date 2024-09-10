# Enable SSO in your Spring Boot application

**Disclaimer**: This example is provided solely to demonstrate the integration of OpenID Connect (OIDC) with a Spring Boot application. It is not intended to serve as a complete implementation of a custom tasklist. The focus is on illustrating how you can handle authorization in your custom application using OIDC. For a full-fledged custom tasklist implementation, additional considerations and configurations are required beyond the scope of this example.


## Add Spring Security

``` 
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
 </dependency>
```

When starting the application you will now see the following login screen:

![01-spring-security-enabled.png](docs%2F01-spring-security-enabled.png)

You will find the following statement in the logs:

``` 
Using generated security password: 522aa624-e7cd-4fe4-a6de-b0d2c21de362

This generated password is for development use only. Your security configuration must be updated before running your application in production.
``` 

If you now login with username: user and the auto-generated password, you will be redirected to Camunda.
However, you can see that you need to login again (default: demo/demo).


## Configure OAuth2
Next you have to add the oauth resource server dependency.

```
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		</dependency>
```

Configure the issuer-uri in the application.yml

```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://dittmeister.de/auth/realms/camunda-platform
```

Return 401 if not authenticated

```
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .anyRequest().authenticated()
        )
        .oauth2ResourceServer((oauth2) -> oauth2
            .jwt(Customizer.withDefaults())
        );
    return http.build();
  }
}
```

## Add Tasklist Client

https://github.com/camunda-community-hub/camunda-tasklist-client-java

```
<dependency>
    <groupId>io.camunda</groupId>
	<artifactId>camunda-tasklist-client-java</artifactId>
	<version>8.5.3.6</version>
</dependency>
```
Add Group mapper to custom client. This will add the groups to the token.
![group-mapper.png](docs%2Fgroup-mapper.png)

Implement TasklistService + Controller

```
  @GetMapping("/my-tasks")
  @ResponseBody
  public List<Task> getTasks(Authentication authentication) throws TaskListException {
    if (authentication != null && authentication.isAuthenticated()) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      String username = jwt.getClaim("preferred_username");
      return tasklistService.getAssignedTasks(username);
    }

    throw new TaskListException("User not authenticated");
  }
```