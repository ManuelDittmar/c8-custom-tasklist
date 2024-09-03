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

So currently we have:
1. ✔️ A custom login procedure
2. ❌ No SSO Integration
3. ❌ No mapping to Camunda

## Configure SSO
Next you have to add the oauth client dependency.

```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
```
Create App in Identity

![custom-app.png](docs%2Fcustom-app.png)

Client can be configured via the application.yaml

```
spring:
  security:
    oauth2:
      client:
        registration:
          custom:
            client-id: custom-tasklist
            client-secret: xxxxx
            scope: openid, profile, email
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8080/login/oauth2/code/custom
        provider:
          custom:
            issuer-uri: https://xxxx.de/auth/realms/camunda-platform
```

Now on startup, you will be redirected to Keycloak.

![keycloak-login.png](docs%2Fkeycloak-login.png)

So currently we have:
1. ✔️ A custom login procedure
2. ✔️ SSO Integration
3. ❌ No mapping to Camunda

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
    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
    String username = oidcUser.getUserInfo().getPreferredUsername();
    return tasklistService.getAssignedTasks(username);
  }
```

Now we have:
1. ✔️ A custom login procedure
2. ✔️ SSO Integration
3. ✔️ No mapping to Camunda