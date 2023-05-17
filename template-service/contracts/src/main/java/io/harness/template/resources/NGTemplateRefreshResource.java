/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.template.resources;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.NGCommonEntityConstants;
import io.harness.accesscontrol.AccountIdentifier;
import io.harness.accesscontrol.OrgIdentifier;
import io.harness.accesscontrol.ProjectIdentifier;
import io.harness.annotations.dev.OwnedBy;
import io.harness.gitsync.interceptor.GitEntityFindInfoDTO;
import io.harness.gitsync.interceptor.GitEntityUpdateInfoDTO;
import io.harness.ng.core.dto.ErrorDTO;
import io.harness.ng.core.dto.FailureDTO;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.ng.core.template.RefreshRequestDTO;
import io.harness.ng.core.template.RefreshResponseDTO;
import io.harness.ng.core.template.refresh.ValidateTemplateInputsResponseDTO;
import io.harness.ng.core.template.refresh.YamlDiffResponseDTO;
import io.harness.ng.core.template.refresh.YamlFullRefreshResponseDTO;
import io.harness.security.annotations.NextGenManagerAuth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import retrofit2.http.Body;

@OwnedBy(CDC)
@Api("refresh-template")
@Path("refresh-template")
@Produces({"application/json", "application/yaml"})
@Consumes({"application/json", "application/yaml"})
@ApiResponses(value =
    {
      @ApiResponse(code = 400, response = FailureDTO.class, message = "Bad Request")
      , @ApiResponse(code = 500, response = ErrorDTO.class, message = "Internal server error")
    })
@Tag(name = "Templates", description = "This contains a list of APIs specific to the Templates Validations and Refresh")
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = NGCommonEntityConstants.BAD_REQUEST_CODE,
    description = NGCommonEntityConstants.BAD_REQUEST_PARAM_MESSAGE,
    content =
    {
      @Content(mediaType = NGCommonEntityConstants.APPLICATION_JSON_MEDIA_TYPE,
          schema = @Schema(implementation = FailureDTO.class))
      ,
          @Content(mediaType = NGCommonEntityConstants.APPLICATION_YAML_MEDIA_TYPE,
              schema = @Schema(implementation = FailureDTO.class))
    })
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = NGCommonEntityConstants.INTERNAL_SERVER_ERROR_CODE,
    description = NGCommonEntityConstants.INTERNAL_SERVER_ERROR_MESSAGE,
    content =
    {
      @Content(mediaType = NGCommonEntityConstants.APPLICATION_JSON_MEDIA_TYPE,
          schema = @Schema(implementation = ErrorDTO.class))
      ,
          @Content(mediaType = NGCommonEntityConstants.APPLICATION_YAML_MEDIA_TYPE,
              schema = @Schema(implementation = ErrorDTO.class))
    })
@NextGenManagerAuth
public interface NGTemplateRefreshResource {
  String TEMPLATE_PARAM_MESSAGE = "Template Identifier for the entity";

  @POST
  @ApiOperation(
      value = "This refreshes and update template inputs in template", nickname = "refreshAndUpdateTemplateInputs")
  @Hidden
  ResponseDTO<Boolean>
  refreshAndUpdateTemplate(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                               NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @QueryParam(
          "templateIdentifier") @NotNull String templateIdentifier,
      @Parameter(description = "Template version") @NotNull @QueryParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      String templateLabel, @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @BeanParam GitEntityUpdateInfoDTO gitEntityUpdateInfoDTO);

  @POST
  @Path("refreshed-yaml")
  @ApiOperation(value = "This refreshes and update template inputs in given yaml", nickname = "getRefreshedYaml")
  @Hidden
  ResponseDTO<RefreshResponseDTO> getRefreshedYaml(
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @Parameter(description = "YAML") @NotNull @Body RefreshRequestDTO refreshRequestDTO);

  @GET
  @Path("validate-template-inputs")
  @ApiOperation(value = "This validates whether yaml of template is valid or not", nickname = "validateTemplateInputs")
  @Hidden
  ResponseDTO<ValidateTemplateInputsResponseDTO> validateTemplateInputsForTemplate(
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @QueryParam(
          "templateIdentifier") @NotNull String templateIdentifier,
      @Parameter(description = "Template version") @NotNull @QueryParam(NGCommonEntityConstants.VERSION_LABEL_KEY)
      String versionLabel, @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo);

  @POST
  @Path("validate-template-inputs/internal")
  @ApiOperation(value = "This validates whether yaml provided is valid or not. This is to be used for pipeline service",
      nickname = "validateTemplateInputs", hidden = true)
  @Hidden
  ResponseDTO<ValidateTemplateInputsResponseDTO>
  validateTemplateInputsForYaml(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull
                                @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @Parameter(description = "YAML") @NotNull @Body RefreshRequestDTO refreshRequestDTO);

  @GET
  @Path("show-diff")
  @ApiOperation(value = "This returns original yaml and refresh yaml of template", nickname = "getYamlDiff")
  @Hidden
  ResponseDTO<YamlDiffResponseDTO> getYamlDiff(
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @QueryParam(
          "templateIdentifier") @NotNull String templateIdentifier,
      @Parameter(description = "Template version") @NotNull @QueryParam(NGCommonEntityConstants.VERSION_LABEL_KEY)
      String versionLabel, @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo);

  @POST
  @Path("refresh-all")
  @ApiOperation(value = "This does recursive refresh and update template inputs in template", nickname = "refreshAll")
  @Hidden
  ResponseDTO<Boolean> refreshAllTemplates(
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @QueryParam(
          "templateIdentifier") @NotNull String templateIdentifier,
      @Parameter(description = "Template version") @NotNull @QueryParam(NGCommonEntityConstants.VERSION_LABEL_KEY)
      String versionLabel, @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @BeanParam GitEntityUpdateInfoDTO gitEntityUpdateInfoDTO);

  @POST
  @Path("refresh-all/internal")
  @ApiOperation(value = "This does recursive refresh and update template inputs in template for given yaml",
      nickname = "refreshAll", hidden = true)
  @Hidden
  ResponseDTO<YamlFullRefreshResponseDTO>
  refreshAllTemplatesForYaml(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull
                             @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @Parameter(description = "YAML") @NotNull @Body RefreshRequestDTO refreshRequestDTO);
}
