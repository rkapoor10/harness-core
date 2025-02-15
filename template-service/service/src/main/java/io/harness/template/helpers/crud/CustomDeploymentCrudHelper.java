/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.template.helpers.crud;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.customDeployment.remote.CustomDeploymentResourceClient;
import io.harness.eventsframework.schemas.entity.EntityDetailProtoDTO;
import io.harness.ng.core.customDeployment.CustomDeploymentYamlRequestDTO;
import io.harness.remote.client.NGRestUtils;
import io.harness.template.entity.TemplateEntity;

import com.google.inject.Inject;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@OwnedBy(HarnessTeam.CDP)
@AllArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__({ @Inject }))
@Slf4j
public class CustomDeploymentCrudHelper implements TemplateCrudHelper {
  @Inject CustomDeploymentResourceClient customDeploymentResourceClient;

  @Override
  public boolean supportsReferences() {
    return true;
  }

  @Override
  public List<EntityDetailProtoDTO> getReferences(TemplateEntity templateEntity, String entityYaml) {
    CustomDeploymentYamlRequestDTO requestDTO = CustomDeploymentYamlRequestDTO.builder().entityYaml(entityYaml).build();
    log.info("Getting entity references for custom deployment template");
    return NGRestUtils.getResponse(customDeploymentResourceClient.getEntityReferences(templateEntity.getAccountId(),
        templateEntity.getOrgIdentifier(), templateEntity.getProjectIdentifier(), requestDTO));
  }
}
