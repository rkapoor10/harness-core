/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.serviceoverridev2.mappers;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.ng.core.serviceoverride.beans.NGServiceOverridesEntity;
import io.harness.ng.core.serviceoverridev2.beans.ServiceOverrideRequestDTOV2;
import io.harness.ng.core.serviceoverridev2.beans.ServiceOverridesResponseDTOV2;

import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@OwnedBy(HarnessTeam.CDC)
@UtilityClass
public class ServiceOverridesMapperV2 {
  public NGServiceOverridesEntity toEntity(
      @NotNull String accountId, @NonNull ServiceOverrideRequestDTOV2 requestDTOV2) {
    return NGServiceOverridesEntity.builder()
        .identifier(requestDTOV2.getIdentifier())
        .accountId(accountId)
        .orgIdentifier(requestDTOV2.getOrgIdentifier())
        .projectIdentifier(requestDTOV2.getProjectIdentifier())
        .environmentRef(requestDTOV2.getEnvironmentRef())
        .serviceRef(requestDTOV2.getServiceRef())
        .infraIdentifier(requestDTOV2.getInfraIdentifier())
        .clusterIdentifier(requestDTOV2.getClusterIdentifier())
        .spec(requestDTOV2.getSpec())
        .type(requestDTOV2.getType())
        .build();
  }

  public ServiceOverridesResponseDTOV2 toResponseDTO(@NonNull NGServiceOverridesEntity entity) {
    return ServiceOverridesResponseDTOV2.builder()
        .identifier(entity.getIdentifier())
        .accountId(entity.getAccountId())
        .orgIdentifier(entity.getOrgIdentifier())
        .projectIdentifier(entity.getProjectIdentifier())
        .environmentRef(entity.getEnvironmentRef())
        .serviceRef(entity.getServiceRef())
        .infraIdentifier(entity.getInfraIdentifier())
        .clusterIdentifier(entity.getClusterIdentifier())
        .spec(entity.getSpec())
        .type(entity.getType())
        .build();
  }
}
