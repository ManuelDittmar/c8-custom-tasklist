package org.camunda.consulting.tasklist;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.exception.TaskListException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TasklistConfiguration {

  @Bean
  public CamundaTaskListClient camundaTaskListClient() throws TaskListException {
    return CamundaTaskListClient
        .builder()
        .taskListUrl("https://xxx.de/tasklist")
        .selfManagedAuthentication("tasklist", "xxxx", "https://xxxx.de/auth/realms/camunda-platform/")
        .build();
  }

}
