package org.camunda.consulting.tasklist.service;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TasklistService {

  @Autowired
  private CamundaTaskListClient camundaTaskListClient;

  public List<Task> getAssignedTasks(String user) throws TaskListException {
    return camundaTaskListClient.getAssigneeTasks(user, TaskState.CREATED, 100).getItems();
  }

  public List<Task> getAvailableTasks(String user, List<String> groups) throws TaskListException {

    List<Task> availableTasks = new ArrayList<>();

    TaskSearch candidateUser = new TaskSearch()
        .setAssigned(false)
        .setState(TaskState.CREATED)
        .setCandidateUser(user);

    availableTasks.addAll(camundaTaskListClient.getTasks(candidateUser).getItems());

    if (groups != null && !groups.isEmpty()) {
      TaskSearch candidateGroup = new TaskSearch()
          .setAssigned(false)
          .setState(TaskState.CREATED)
          .setCandidateGroups(groups);

      availableTasks.addAll(camundaTaskListClient.getTasks(candidateGroup).getItems());
    }

    // Remove duplicates by id
    List<Task> uniqueTasks = availableTasks.stream()
        .collect(Collectors.collectingAndThen(
            Collectors.toMap(Task::getId, task -> task, (existing, replacement) -> existing),
            map -> new ArrayList<>(map.values())
        ));

    return uniqueTasks;
  }


}
