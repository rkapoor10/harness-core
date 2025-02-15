/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.dashboards;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;

import io.harness.annotations.dev.OwnedBy;

import lombok.Builder;
import lombok.Data;

@OwnedBy(PIPELINE)
@Data
@Builder
public class ServiceDashboardInfo {
  String name;
  String identifier;
  String orgIdentifier;
  String projectIdentifier;
  String accountIdentifier;

  long totalDeploymentsCount;
  long successDeploymentsCount;
  long failureDeploymentsCount;
  @Builder.Default double totalDeploymentsChangeRate = DashboardHelper.MAX_VALUE;

  long instancesCount;
  @Builder.Default double instancesCountChangeRate = DashboardHelper.MAX_VALUE;
}
