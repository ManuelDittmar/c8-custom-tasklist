package org.camunda.consulting.tasklist.controller;

import org.camunda.consulting.tasklist.service.TasklistService;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.exception.TaskListException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
    if (authentication != null && authentication.isAuthenticated()) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      return jwt.getClaims();  // Return the claims directly from the JWT token
    }

    return Map.of("error", "User is not authenticated");
  }

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

  @GetMapping("/available-tasks")
  @ResponseBody
  public List<Task> getAvailableTasks(Authentication authentication) throws TaskListException {
    if (authentication != null && authentication.isAuthenticated()) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      String username = jwt.getClaim("preferred_username");
      List<String> groups = jwt.getClaim("groups");
      return tasklistService.getAvailableTasks(username, groups);
    }

    throw new TaskListException("User not authenticated");
  }
}
