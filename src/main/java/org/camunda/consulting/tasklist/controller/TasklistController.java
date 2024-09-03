package org.camunda.consulting.tasklist.controller;

import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import java.util.List;
import java.util.Map;
import org.camunda.consulting.tasklist.service.TasklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TasklistController {

  @Autowired
  private TasklistService tasklistService;

  @GetMapping("/current-user")
  @ResponseBody
  public Map<String, Object> getCurrentUser(Authentication authentication) {
    if (authentication.getPrincipal() instanceof OidcUser) {
      OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
      return oidcUser.getAttributes();
    }

    return Map.of("error", "User is not authenticated");
  }

  @GetMapping("/my-tasks")
  @ResponseBody
  public List<Task> getTasks(Authentication authentication) throws TaskListException {
    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
    String username = oidcUser.getUserInfo().getPreferredUsername();
    return tasklistService.getAssignedTasks(username);
  }

  @GetMapping("/available-tasks")
  @ResponseBody
  public List<Task> getAvailableTasks(Authentication authentication) throws TaskListException {
    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
    String username = oidcUser.getUserInfo().getPreferredUsername();
    List <String> groups = oidcUser.getClaimAsStringList("groups");
    System.out.println(groups);
    return tasklistService.getAvailableTasks(username, groups);
  }

}
