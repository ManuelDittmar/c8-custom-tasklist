package org.camunda.consulting.tasklist;

import io.camunda.common.auth.Authentication;
import io.camunda.common.auth.JwtConfig;
import io.camunda.common.auth.JwtCredential;
import io.camunda.common.auth.Product;
import io.camunda.common.auth.SelfManagedAuthentication;
import io.camunda.common.auth.identity.IdentityConfig;
import io.camunda.common.auth.identity.IdentityContainer;
import io.camunda.identity.sdk.Identity;
import io.camunda.identity.sdk.IdentityConfiguration;
import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.exception.TaskListException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class TasklistConfiguration {

  private final ClientRegistrationRepository clientRegistrationRepository;

  public TasklistConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Value("${tasklist.url}")
  private String taskListUrl;

  @Bean
  public CamundaTaskListClient camundaTaskListClient() throws TaskListException {
    ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("custom");

    String clientId = clientRegistration.getClientId();
    String clientSecret = clientRegistration.getClientSecret();
    String issuerUri = clientRegistration.getProviderDetails().getIssuerUri();

    IdentityConfig identityConfig = new IdentityConfig();
    IdentityConfiguration identityConfiguration = new IdentityConfiguration.Builder()
        .withType("MICROSOFT")
        .withClientId(clientId)
        .withClientSecret(clientSecret)
        .withAudience(clientId)
        .withIssuer(issuerUri)
        .withIssuerBackendUrl(issuerUri)
        .withBaseUrl(taskListUrl)
        .build();

    Identity identity = new Identity(identityConfiguration);
    identityConfig.addProduct(Product.TASKLIST, new IdentityContainer(identity, identityConfiguration));

    JwtConfig jwtConfig = new JwtConfig();
    jwtConfig.addProduct(Product.TASKLIST, new JwtCredential(clientId, clientSecret, clientId, issuerUri));

    Authentication authentication = SelfManagedAuthentication.builder()
        .withIdentityConfig(identityConfig)
        .withJwtConfig(jwtConfig)
        .build();

    return CamundaTaskListClient
        .builder()
        .taskListUrl(taskListUrl)
        .authentication(authentication)
        .build();
  }
}
