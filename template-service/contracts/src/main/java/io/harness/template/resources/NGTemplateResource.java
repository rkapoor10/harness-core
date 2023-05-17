/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.template.resources;

import static io.harness.NGCommonEntityConstants.FORCE_DELETE_MESSAGE;
import static io.harness.annotations.dev.HarnessTeam.CDC;

import static javax.ws.rs.core.HttpHeaders.IF_MATCH;

import io.harness.NGCommonEntityConstants;
import io.harness.NGResourceFilterConstants;
import io.harness.accesscontrol.AccountIdentifier;
import io.harness.accesscontrol.OrgIdentifier;
import io.harness.accesscontrol.ProjectIdentifier;
import io.harness.accesscontrol.ResourceIdentifier;
import io.harness.annotations.dev.OwnedBy;
import io.harness.encryption.Scope;
import io.harness.eventsframework.schemas.entity.EntityDetailProtoDTO;
import io.harness.gitaware.helper.GitImportInfoDTO;
import io.harness.gitaware.helper.TemplateMoveConfigRequestDTO;
import io.harness.gitsync.interceptor.GitEntityCreateInfoDTO;
import io.harness.gitsync.interceptor.GitEntityDeleteInfoDTO;
import io.harness.gitsync.interceptor.GitEntityFindInfoDTO;
import io.harness.gitsync.interceptor.GitEntityUpdateInfoDTO;
import io.harness.ng.beans.PageResponse;
import io.harness.ng.core.dto.ErrorDTO;
import io.harness.ng.core.dto.FailureDTO;
import io.harness.ng.core.dto.ResponseDTO;
import io.harness.ng.core.entitysetupusage.dto.EntitySetupUsageDTO;
import io.harness.ng.core.template.TemplateApplyRequestDTO;
import io.harness.ng.core.template.TemplateListType;
import io.harness.ng.core.template.TemplateMergeResponseDTO;
import io.harness.ng.core.template.TemplateMetadataSummaryResponseDTO;
import io.harness.ng.core.template.TemplateReferenceRequestDTO;
import io.harness.ng.core.template.TemplateResponseDTO;
import io.harness.ng.core.template.TemplateRetainVariablesRequestDTO;
import io.harness.ng.core.template.TemplateRetainVariablesResponse;
import io.harness.ng.core.template.TemplateSummaryResponseDTO;
import io.harness.ng.core.template.TemplateWithInputsResponseDTO;
import io.harness.pms.variables.VariableMergeServiceResponse;
import io.harness.security.annotations.NextGenManagerAuth;
import io.harness.template.resources.beans.NGTemplateConstants;
import io.harness.template.resources.beans.TemplateDeleteListRequestDTO;
import io.harness.template.resources.beans.TemplateFilterPropertiesDTO;
import io.harness.template.resources.beans.TemplateImportRequestDTO;
import io.harness.template.resources.beans.TemplateImportSaveResponse;
import io.harness.template.resources.beans.TemplateListRepoResponse;
import io.harness.template.resources.beans.TemplateMoveConfigResponse;
import io.harness.template.resources.beans.TemplateUpdateGitMetadataRequest;
import io.harness.template.resources.beans.TemplateUpdateGitMetadataResponse;
import io.harness.template.resources.beans.TemplateWrapperResponseDTO;
import io.harness.template.resources.beans.yaml.NGTemplateConfig;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.domain.Page;
import retrofit2.http.Body;

@OwnedBy(CDC)
@Api("templates")
@Path("templates")
@Produces({"application/json", "application/yaml"})
@Consumes({"application/json", "application/yaml"})
@ApiResponses(value =
    {
      @ApiResponse(code = 400, response = FailureDTO.class, message = "Bad Request")
      , @ApiResponse(code = 500, response = ErrorDTO.class, message = "Internal server error")
    })
@Tag(name = "Templates", description = "This contains a list of APIs specific to the Templates")
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
public interface NGTemplateResource {
  String TEMPLATE = "TEMPLATE";
  String INCLUDE_ALL_TEMPLATES_ACCESSIBLE = "includeAllTemplatesAvailableAtScope";
  String TEMPLATE_PARAM_MESSAGE = "Template Identifier for the entity";

  @GET
  @Path("{templateIdentifier}")
  @ApiOperation(value = "Gets Template", nickname = "getTemplate")
  @Operation(operationId = "getTemplate", summary = "Get Template",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the saved Template")
      })
  ResponseDTO<TemplateResponseDTO>
  get(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @QueryParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "Specifies whether Template is deleted or not") @QueryParam(
          NGCommonEntityConstants.DELETED_KEY) @DefaultValue("false") boolean deleted,
      @Parameter(description = "This contains details of Git Entity like Git Branch info")
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @QueryParam("loadFromFallbackBranch") @DefaultValue("false") boolean loadFromFallbackBranch);

  @POST
  @ApiOperation(value = "Creates a Template", nickname = "createTemplate")
  @Operation(operationId = "createTemplate", summary = "Create a Template",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the created Template")
      })
  ResponseDTO<TemplateWrapperResponseDTO>
  create(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
             NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = "This contains details of Git Entity like Git Branch, Git Repository to be created")
      @BeanParam GitEntityCreateInfoDTO gitEntityCreateInfo,
      @RequestBody(required = true, description = "Template YAML",
          content =
          {
            @Content(examples = @ExampleObject(name = "Create", summary = "Sample Create Template YAML",
                         value = NGTemplateConstants.API_SAMPLE_TEMPLATE_YAML, description = "Sample Template YAML"))
          }) @NotNull String templateYaml,
      @Parameter(description = "Specify true if Default Template is to be set") @QueryParam(
          "setDefaultTemplate") @DefaultValue("false") boolean setDefaultTemplate,
      @Parameter(description = "Comments") @QueryParam("comments") String comments,
      @Parameter(
          description =
              "When isNewTemplate flag is set user will not be able to create a new version for an existing template")
      @QueryParam("isNewTemplate") @DefaultValue("false") @ApiParam(hidden = true) boolean isNewTemplate);

  @PUT
  @Path("/updateStableTemplate/{templateIdentifier}/{versionLabel}")
  @ApiOperation(value = "Updating stable template version", nickname = "updateStableTemplate")
  @Operation(operationId = "updateStableTemplate", summary = "Update Stable Template Version",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the updated Template Version")
      })
  ResponseDTO<String>
  updateStableTemplate(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                           NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @PathParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "This contains details of Git Entity like Git Branch info to be updated")
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @Parameter(description = "Comments") @QueryParam("comments") String comments);

  @PUT
  @Path("/update/{templateIdentifier}/{versionLabel}")
  @ApiOperation(value = "Updating existing template version", nickname = "updateExistingTemplateVersion")
  @Operation(operationId = "updateExistingTemplateVersion", summary = "Update Template Version",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the updated Template Version")
      })
  ResponseDTO<TemplateWrapperResponseDTO>
  updateExistingTemplateLabel(@HeaderParam(IF_MATCH) String ifMatch,
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @PathParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "This contains details of Git Entity like Git Branch information to be updated")
      @BeanParam GitEntityUpdateInfoDTO gitEntityInfo,
      @RequestBody(required = true, description = "Template YAML",
          content =
          {
            @Content(examples = @ExampleObject(name = "Update", summary = "Sample Update Template YAML",
                         value = NGTemplateConstants.API_SAMPLE_TEMPLATE_YAML, description = "Sample Template YAML"))
          }) @NotNull String templateYaml,
      @Parameter(description = "Specify true if Default Template is to be set") @QueryParam(
          "setDefaultTemplate") @DefaultValue("false") boolean setDefaultTemplate,
      @Parameter(description = "Comments") @QueryParam("comments") String comments);

  @DELETE
  @Path("/{templateIdentifier}/{versionLabel}")
  @ApiOperation(value = "Deletes template version", nickname = "deleteTemplateVersion")
  @Operation(operationId = "deleteTemplateVersion", summary = "Delete Template Version",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns true if the Template is deleted")
      })
  ResponseDTO<Boolean>
  deleteTemplate(@HeaderParam(IF_MATCH) String ifMatch,
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @NotNull @PathParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "This contains details of Git Entity like Git Branch information to be deleted")
      @BeanParam GitEntityDeleteInfoDTO entityDeleteInfo, @QueryParam("comments") String comments,
      @Parameter(description = FORCE_DELETE_MESSAGE) @QueryParam(NGCommonEntityConstants.FORCE_DELETE) @DefaultValue(
          "false") boolean forceDelete);

  @DELETE
  @Path("/{templateIdentifier}")
  @ApiOperation(value = "Delete Template Versions", nickname = "deleteTemplateVersionsOfIdentifier")
  @Operation(operationId = "deleteTemplateVersionsOfIdentifier", summary = "Delete Template Versions",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "default",
            description = "Returns true if the Template VersionLabels of a Template Identifier are deleted")
      })
  @Hidden
  ResponseDTO<Boolean>
  deleteTemplateVersionsOfParticularIdentifier(
      @Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
          NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "List of Template Version Labels to be deleted")
      @Body TemplateDeleteListRequestDTO templateDeleteListRequestDTO,
      @Parameter(description = "This contains details of Git Entity like Git Branch information to be deleted")
      @BeanParam GitEntityDeleteInfoDTO entityDeleteInfo,
      @Parameter(description = "Comments") @QueryParam("comments") String comments,
      @Parameter(description = FORCE_DELETE_MESSAGE) @QueryParam(NGCommonEntityConstants.FORCE_DELETE) @DefaultValue(
          "false") boolean forceDelete);

  @POST
  @Path("/list")
  @ApiOperation(value = "Gets all template list", nickname = "getTemplateList")
  @Operation(operationId = "getTemplateList", summary = "Get Templates",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns a list of all the Templates")
      })
  @Hidden
  // will return non deleted templates only
  ResponseDTO<Page<TemplateSummaryResponseDTO>>
  listTemplates(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                    NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = NGCommonEntityConstants.PAGE_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PAGE) @DefaultValue("0") int page,
      @Parameter(description = NGCommonEntityConstants.SIZE_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.SIZE) @DefaultValue("25") int size,
      @Parameter(
          description =
              "Specifies sorting criteria of the list. Like sorting based on the last updated entity, alphabetical sorting in an ascending or descending order")
      @QueryParam("sort") List<String> sort,
      @Parameter(description = "The word to be searched and included in the list response") @QueryParam(
          NGResourceFilterConstants.SEARCH_TERM_KEY) String searchTerm,
      @Parameter(description = "Filter Identifier") @QueryParam("filterIdentifier") String filterIdentifier,
      @Parameter(description = "Template List Type") @NotNull @QueryParam(
          "templateListType") TemplateListType templateListType,
      @Parameter(description = "Specify true if all accessible Templates are to be included") @QueryParam(
          INCLUDE_ALL_TEMPLATES_ACCESSIBLE) Boolean includeAllTemplatesAccessibleAtScope,
      @Parameter(description = "This contains details of Git Entity like Git Branch info")
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @Parameter(description = "This contains details of Template filters based on Template Types and Template Names ")
      @Body TemplateFilterPropertiesDTO filterProperties,
      @QueryParam("getDistinctFromBranches") Boolean getDistinctFromBranches);

  @POST
  @Path("/list-metadata")
  @ApiOperation(value = "Gets all template list", nickname = "getTemplateMetadataList")
  @Operation(operationId = "getTemplateMetadataList", summary = "Gets all metadata of template list",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns a list of all the metadata of all Templates")
      })

  ResponseDTO<Page<TemplateMetadataSummaryResponseDTO>>
  listTemplateMetadata(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                           NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountIdentifier,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgIdentifier,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @Parameter(description = NGCommonEntityConstants.PAGE_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PAGE) @DefaultValue("0") int page,
      @Parameter(description = NGCommonEntityConstants.SIZE_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.SIZE) @DefaultValue("25") int size,
      @Parameter(
          description =
              "Specifies sorting criteria of the list. Like sorting based on the last updated entity, alphabetical sorting in an ascending or descending order")
      @QueryParam("sort") List<String> sort,
      @Parameter(description = "The word to be searched and included in the list response") @QueryParam(
          NGResourceFilterConstants.SEARCH_TERM_KEY) String searchTerm,
      @Parameter(description = "Filter Identifier") @QueryParam("filterIdentifier") String filterIdentifier,
      @Parameter(description = "Template List Type") @NotNull @QueryParam(
          "templateListType") TemplateListType templateListType,
      @Parameter(description = "Specify true if all accessible Templates are to be included") @QueryParam(
          INCLUDE_ALL_TEMPLATES_ACCESSIBLE) boolean includeAllTemplatesAccessibleAtScope,
      @Parameter(description = "This contains details of Template filters based on Template Types and Template Names ")
      @Body TemplateFilterPropertiesDTO filterProperties,
      @QueryParam("getDistinctFromBranches") boolean getDistinctFromBranches);

  @PUT
  @Path("/updateTemplateSettings/{templateIdentifier}")
  @ApiOperation(value = "Updating template settings, template scope and template stable version",
      nickname = "updateTemplateSettings")
  @Operation(operationId = "updateTemplateSettings",
      summary = "Updating Template Settings, Template Scope and Template Stable Version",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "default",
            description = "Returns true if Template Settings, Template Scope and Template Stable Version are updated")
      })
  @Hidden
  ResponseDTO<Boolean>
  updateTemplateSettings(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                             NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Update Stable Template Version") @QueryParam(
          "updateStableTemplateVersion") String updateStableTemplateVersion,
      @Parameter(description = "Current Scope") @QueryParam("currentScope") Scope currentScope,
      @Parameter(description = "Update Scope") @QueryParam("updateScope") Scope updateScope,
      @Parameter(description = "This contains details of Git Entity like Git Branch info")
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @QueryParam("getDistinctFromBranches") Boolean getDistinctFromBranches);

  @GET
  @Path("/templateInputs/{templateIdentifier}")
  @ApiOperation(value = "Gets template input set yaml", nickname = "getTemplateInputSetYaml")
  @Operation(operationId = "getTemplateInputSetYaml", summary = "Gets Template Input Set YAML",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the Template Input Set YAML")
      })
  ResponseDTO<String>
  getTemplateInputsYaml(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                            NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Template Label") @NotNull @QueryParam(NGCommonEntityConstants.VERSION_LABEL_KEY)
      String templateLabel, @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @Parameter(
          description = "This contains details of Git Entity") @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo);

  @GET
  @Path("/entitySetupUsage/{templateIdentifier}")
  @ApiOperation(value = "Get Entities referring this template", nickname = "listTemplateUsage")
  @Hidden
  ResponseDTO<PageResponse<EntitySetupUsageDTO>> listTemplateEntityUsage(
      @QueryParam(NGResourceFilterConstants.PAGE_KEY) @DefaultValue("0") int page,
      @QueryParam(NGResourceFilterConstants.SIZE_KEY) @DefaultValue("100") int size,
      @NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountIdentifier,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgIdentifier,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @QueryParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "Is Stable Template") @QueryParam(NGCommonEntityConstants.IS_STABLE_TEMPLATE)
      boolean isStableTemplate, @QueryParam(NGResourceFilterConstants.SEARCH_TERM_KEY) String searchTerm);

  @GET
  @Path("/templateWithInputs/{templateIdentifier}")
  @ApiOperation(value = "Get Template along with Input Set YAML", nickname = "getTemplateAlongWithInputSetYaml")
  @Operation(operationId = "getTemplateAlongWithInputSetYaml", summary = "Gets Template along with Input Set YAML",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the Template along with Input Set YAML")
      })
  @Hidden
  ResponseDTO<TemplateWithInputsResponseDTO>
  getTemplateAlongWithInputsYaml(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull
                                 @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Template Label") @NotNull @QueryParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String templateLabel,
      @Parameter(
          description = "This contains details of Git Entity") @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache);

  @POST
  @Path("/mergeTemplateInputs/")
  @ApiOperation(value = "Get merged Template input YAML", nickname = "getsMergedTemplateInputYaml")
  @Operation(operationId = "getsMergedTemplateInputYaml", summary = "Gets merged Template input YAML",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns the Merged YAML")
      })
  @Hidden
  ResponseDTO<TemplateRetainVariablesResponse>
  getMergedTemplateInputsYaml(
      @NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @NotNull TemplateRetainVariablesRequestDTO templateRetainVariablesRequestDTO);

  @POST
  @Path("/applyTemplates")
  @ApiOperation(value = "Gets complete yaml with templateRefs resolved", nickname = "getYamlWithTemplateRefsResolved")
  @Operation(operationId = "getYamlWithTemplateRefsResolved", summary = "Gets complete yaml with templateRefs resolved",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Gets complete yaml with templateRefs resolved")
      })
  @Hidden
  ResponseDTO<TemplateMergeResponseDTO>
  applyTemplates(@NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @QueryParam(NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @QueryParam(NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo, @NotNull TemplateApplyRequestDTO templateApplyRequestDTO,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @QueryParam("AppendInputSetValidator") @DefaultValue("false") boolean appendInputSetValidator);

  @POST
  @Path("v2/applyTemplates")
  @ApiOperation(value = "Gets complete yaml with templateRefs resolved", nickname = "getYamlWithTemplateRefsResolvedV2")
  @Operation(operationId = "getYamlWithTemplateRefsResolvedV2",
      summary = "Gets complete yaml with templateRefs resolved",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Gets complete yaml with templateRefs resolved")
      })
  @Hidden
  ResponseDTO<TemplateMergeResponseDTO>
  applyTemplatesV2(@NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @QueryParam(NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @QueryParam(NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo, @NotNull TemplateApplyRequestDTO templateApplyRequestDTO,
      @HeaderParam("Load-From-Cache") @DefaultValue("false") String loadFromCache,
      @QueryParam("AppendInputSetValidator") @DefaultValue("false") boolean appendInputSetValidator);

  @GET
  @ApiOperation(value = "dummy api for checking template schema", nickname = "dummyApiForSwaggerSchemaCheck")
  @Path("/dummyApiForSwaggerSchemaCheck")
  @Hidden
  // DO NOT DELETE THIS WITHOUT CONFIRMING WITH UI
  ResponseDTO<NGTemplateConfig> dummyApiForSwaggerSchemaCheck();

  @POST
  @Path("/variables")
  @Operation(operationId = "createVariables", summary = "Get all the Variables",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "default",
            description = "Returns all Variables used that are valid to be used as expression in template.")
      })
  @ApiOperation(value = "Create variables for Template", nickname = "createVariables")
  @Hidden
  ResponseDTO<VariableMergeServiceResponse>
  createVariables(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE,
                      required = true) @NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE, required = true) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE, required = true) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) String projectId,
      @RequestBody(required = true, description = "Template YAML") @NotNull @ApiParam(hidden = true) String yaml);

  @POST
  @Path("/v2/variables")
  @Operation(operationId = "createVariablesV2",
      summary = "Get all the Variables which can be used as expression in the Template.",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "default",
            description = "Returns all Variables used that are valid to be used as expression in template.")
      })
  @ApiOperation(value = "Create variables for Template", nickname = "createVariablesV2")
  @Hidden
  ResponseDTO<VariableMergeServiceResponse>
  createVariablesV2(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE,
                        required = true) @NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE, required = true) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE, required = true) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) String projectId,
      @RequestBody(required = true, description = "Template YAML") @NotNull @ApiParam(hidden = true) String yaml);

  @GET
  @Path("validateUniqueIdentifier")
  @ApiOperation(value = "Validate Identifier is unique", nickname = "validateTheIdentifierIsUnique")
  @Operation(operationId = "validateTheIdentifierIsUnique",
      summary =
          "Validate template identifier is unique by Account Identifier, Organization Identifier, Project Identifier, Template Identifier and Version Label",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "default",
            description = "It returns true if the Identifier is unique and false if the Identifier is not unique")
      })
  @Hidden
  ResponseDTO<Boolean>
  validateTheIdentifierIsUnique(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE, required = true)
                                @NotBlank @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountIdentifier,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgIdentifier,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.IDENTIFIER_KEY) @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @QueryParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel);

  @POST
  @Path("templateReferences")
  @ApiOperation(value = "get all Template entity references", nickname = "getTemplateReferences")
  @Operation(operationId = "getTemplateReferences", summary = "get all Template entity references",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "get all Template entity references")
      })
  @Hidden
  ResponseDTO<List<EntityDetailProtoDTO>>
  getTemplateReferences(@NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountId,
      @QueryParam(NGCommonEntityConstants.ORG_KEY) String orgId,
      @QueryParam(NGCommonEntityConstants.PROJECT_KEY) String projectId,
      @BeanParam GitEntityFindInfoDTO gitEntityBasicInfo,
      @NotNull TemplateReferenceRequestDTO templateReferenceRequestDTO);

  @POST
  @Path("/import/{templateIdentifier}")
  @ApiOperation(value = "import template from git", nickname = "importTemplate")
  @Operation(operationId = "importTemplate", summary = "import template from git",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "import template from git")
      })
  @Hidden
  ResponseDTO<TemplateImportSaveResponse>
  importTemplateFromGit(@NotNull @QueryParam(NGCommonEntityConstants.ACCOUNT_KEY) String accountIdentifier,
      @QueryParam(NGCommonEntityConstants.ORG_KEY) String orgIdentifier,
      @QueryParam(NGCommonEntityConstants.PROJECT_KEY) String projectIdentifier,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @BeanParam GitImportInfoDTO gitImportInfoDTO, TemplateImportRequestDTO templateImportRequestDTO);

  @GET
  @Path("/list-repo")
  @Hidden
  @ApiOperation(value = "Gets all repo list", nickname = "getRepositoryList")
  @Operation(operationId = "getRepositoryList", summary = "Gets the list of all repositories",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Returns a list of all the repositories of all Templates")
      })

  ResponseDTO<TemplateListRepoResponse>
  listRepos(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountIdentifier,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgIdentifier,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @Parameter(description = "Specify true if all accessible Templates are to be included") @QueryParam(
          INCLUDE_ALL_TEMPLATES_ACCESSIBLE) boolean includeAllTemplatesAccessibleAtScope);

  @POST
  @Path("/move-config/{templateIdentifier}")
  @ApiOperation(value = "Move Template YAML from inline to remote", nickname = "moveTemplateConfigs")
  @Operation(operationId = "moveTemplateConfigs", summary = "Move Template YAML from inline to remote",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "default", description = "Fetches Template YAML from Harness DB and creates a remote entity")
      })
  ResponseDTO<TemplateMoveConfigResponse>
  moveConfig(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                 NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountId,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgId,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectId,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @BeanParam TemplateMoveConfigRequestDTO templateMoveConfigRequestDTO);

  @POST
  @Path("/update/git-metadata/{templateIdentifier}/{versionLabel}")
  @ApiOperation(value = "Update git metadata details for a remote template", nickname = "updateGitDetails")
  @Operation(operationId = "updateGitDetails", summary = "Update git metadata details for a remote template",
      responses =
      {
        @io.swagger.v3.oas.annotations.responses.
        ApiResponse(responseCode = "default", description = "Update git metadata details for a remote template")
      })
  ResponseDTO<TemplateUpdateGitMetadataResponse>
  updateGitMetadataDetails(@Parameter(description = NGCommonEntityConstants.ACCOUNT_PARAM_MESSAGE) @NotNull @QueryParam(
                               NGCommonEntityConstants.ACCOUNT_KEY) @AccountIdentifier String accountIdentifier,
      @Parameter(description = NGCommonEntityConstants.ORG_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.ORG_KEY) @OrgIdentifier String orgIdentifier,
      @Parameter(description = NGCommonEntityConstants.PROJECT_PARAM_MESSAGE) @QueryParam(
          NGCommonEntityConstants.PROJECT_KEY) @ProjectIdentifier String projectIdentifier,
      @Parameter(description = TEMPLATE_PARAM_MESSAGE) @PathParam(
          "templateIdentifier") @ResourceIdentifier String templateIdentifier,
      @Parameter(description = "Version Label") @PathParam(
          NGCommonEntityConstants.VERSION_LABEL_KEY) String versionLabel,
      @Parameter(description = "This contains details of Git Entity like Git Branch info to be updated")
      TemplateUpdateGitMetadataRequest request);
}