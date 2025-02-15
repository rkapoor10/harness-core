/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.iacm;

import static io.harness.annotations.dev.HarnessTeam.IACM;

import io.harness.EntityType;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.FeatureName;
import io.harness.beans.steps.nodes.iacm.IACMTemplateStepNode;
import io.harness.beans.steps.nodes.iacm.IACMTerraformPlanStepNode;
import io.harness.iacm.plan.creator.step.IACMTemplateStepPlanCreator;
import io.harness.iacm.plan.creator.step.IACMTerraformPlanStepPlanCreator;
import io.harness.pms.contracts.steps.StepCategory;
import io.harness.pms.contracts.steps.StepType;
import io.harness.pms.sdk.core.plan.creation.creators.PartialPlanCreator;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@OwnedBy(IACM)
public enum IACMStepType {
  IACM_TERRAFORM_PLAN("IACMTerraformPlan", FeatureName.IACM_ENABLED, IACMTerraformPlanStepNode.class,
      EntityType.IACM_TERRAFORM_PLAN, new IACMTerraformPlanStepPlanCreator(), new String[] {"IACM"}),
  IACM_TEMPLATE("IACMTemplate", FeatureName.IACM_ENABLED, IACMTemplateStepNode.class, EntityType.IACM_TEMPLATE,
      new IACMTemplateStepPlanCreator(), new String[] {"IACM"});
  @Getter private String name;
  @Getter private FeatureName featureName;
  @Getter private Class<?> node;
  @Getter private EntityType entityType;
  @Getter private PartialPlanCreator<?> planCreator;
  private String[] stepCategories;

  public StepType getStepType() {
    return StepType.newBuilder().setType(this.name).setStepCategory(StepCategory.STEP).build();
  }

  public Stream<String> getStepCategories() {
    return Arrays.asList(this.stepCategories).stream();
  }
}
