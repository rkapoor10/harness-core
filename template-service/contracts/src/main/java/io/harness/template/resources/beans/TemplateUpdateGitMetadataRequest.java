/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.template.resources.beans;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;

import io.harness.annotations.dev.OwnedBy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("TemplateUpdateGitDetailsRequest")
@Schema(name = "TemplateUpdateGitDetailsRequest",
    description = "Lists down request params for template update git details request")
@OwnedBy(PIPELINE)
public class TemplateUpdateGitMetadataRequest {
  @Schema(description = "filepath to be updated") String filepath;
  @Schema(description = "repo name to be updated") String repoName;
  @Schema(description = "connector ref to be udpated") String connectorRef;
}
