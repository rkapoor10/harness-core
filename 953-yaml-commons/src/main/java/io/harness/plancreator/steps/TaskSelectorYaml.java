/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.plancreator.steps;

import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotation.RecasterAlias;
import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.TaskSelector;
import io.harness.pms.yaml.ParameterField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@OwnedBy(HarnessTeam.PIPELINE)
@RecasterAlias("io.harness.plancreator.steps.TaskSelectorYaml")
@NoArgsConstructor
public class TaskSelectorYaml {
  String delegateSelectors;
  String origin;
  public TaskSelectorYaml(String delegateSelectors) {
    this.delegateSelectors = delegateSelectors;
  }
  public static TaskSelector toTaskSelector(TaskSelectorYaml taskSelectorYaml) {
    String origin = taskSelectorYaml.origin != null ? taskSelectorYaml.origin : "default";
    return TaskSelector.newBuilder().setSelector(taskSelectorYaml.delegateSelectors).setOrigin(origin).build();
  }
  public static List<TaskSelector> toTaskSelector(List<TaskSelectorYaml> taskSelectorYaml) {
    if (taskSelectorYaml == null) {
      return Collections.emptyList();
    }
    return taskSelectorYaml.stream().map(TaskSelectorYaml::toTaskSelector).collect(Collectors.toList());
  }

  public static TaskSelector toTaskSelector(String delegateSelector) {
    return TaskSelector.newBuilder().setSelector(delegateSelector).setOrigin("default").build();
  }
  public static List<TaskSelector> toTaskSelector(ParameterField<List<TaskSelectorYaml>> delegateSelectors) {
    if (ParameterField.isNull(delegateSelectors)) {
      return Collections.emptyList();
    }

    if (isNotEmpty(delegateSelectors.getValue()) && delegateSelectors.getValue().get(0) instanceof TaskSelectorYaml) {
      return TaskSelectorYaml.toTaskSelector(delegateSelectors.getValue());
    }

    // Handling the case of entire list of delegate selectors being an expression explicitly as it can't be directly
    // cast to List<TaskSelectorYaml>
    List<TaskSelectorYaml> delegateSelectorsValue = delegateSelectors.getValue();
    List<TaskSelector> taskSelectors = new ArrayList<>();
    for (int i = 0; i < delegateSelectorsValue.size(); i++) {
      taskSelectors.add(toTaskSelector(String.valueOf(delegateSelectorsValue.get(i))));
    }
    return taskSelectors;
  }
}
