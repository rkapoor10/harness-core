/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.overview.service;

import static io.harness.rule.OwnerRule.ABHISHEK;
import static io.harness.rule.OwnerRule.SOURABH;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.harness.category.element.UnitTests;
import io.harness.cdng.envGroup.beans.EnvironmentGroupEntity;
import io.harness.entities.RollbackStatus;
import io.harness.models.ActiveServiceInstanceInfoWithEnvType;
import io.harness.ng.core.environment.beans.EnvironmentType;
import io.harness.ng.overview.dto.InstanceGroupedByEnvironmentList;
import io.harness.ng.overview.dto.InstanceGroupedOnArtifactList;
import io.harness.pms.contracts.execution.Status;
import io.harness.rule.Owner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Page;

public class DashboardServiceHelperTest {
  private static final String ENV_1 = "env1";
  private static final String ENV_2 = "env2";
  private static final String INFRA_1 = "infra1";
  private static final String INFRA_2 = "infra2";
  private static final String DISPLAY_NAME_1 = "displayName1";
  private static final String DISPLAY_NAME_2 = "displayName2";
  private static final String ACCOUNT_ID = "accountId";
  private static final String ORG_ID = "orgId";
  private static final String PROJECT_ID = "projectId";
  private static final String SERVICE_ID = "serviceId";
  private static final String IMAGE = "image";
  private static final String TAG = "tag";
  private static final String STATUS = "status";

  private String instanceKey1 = "instanceKey1";
  private String infraMappingId1 = "infraMappingId1";
  private String instanceKey2 = "instanceKey2";
  private String infraMappingId2 = "infraMappingId2";
  private String lastPipelineExecutionName;
  private String lastPipelineExecutionId;
  private String stageNodeExecutionId;
  private Status stageStatus;
  private String stageSetupId;
  private RollbackStatus rollbackStatus;
  private Map<String, String> envIdToNameMap;
  private Map<String, String> infraIdToNameMap;
  private Map<String,
      Map<EnvironmentType, Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>>>>
      instanceCountMap;

  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> instanceGroupedByArtifactList1;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> instanceGroupedByArtifactList2;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> instanceGroupedByArtifactList3;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> instanceGroupedByArtifactList4;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByInfrastructureList1;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByInfrastructureList2;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByInfrastructureList3;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByClusterList1;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByClusterList2;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByClusterList3;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType> instanceGroupedByEnvironmentTypeList1;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType> instanceGroupedByEnvironmentTypeList2;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType>
      instanceGroupedByEnvironmentTypeListGitOps1;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType>
      instanceGroupedByEnvironmentTypeListGitOps2;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment> instanceGroupedByEnvironmentList;
  private List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment> instanceGroupedByEnvironmentListGitOps;

  @Before
  public void setup() {
    instanceGroupedByArtifactList1 = new ArrayList<>();
    instanceGroupedByArtifactList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
                                           .instanceKey(instanceKey2)
                                           .infrastructureMappingId(infraMappingId2)
                                           .artifact(DISPLAY_NAME_2)
                                           .lastDeployedAt(2l)
                                           .count(1)
                                           .build());
    instanceGroupedByArtifactList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
                                           .instanceKey(instanceKey1)
                                           .infrastructureMappingId(infraMappingId1)
                                           .artifact(DISPLAY_NAME_1)
                                           .lastDeployedAt(1l)
                                           .count(1)
                                           .build());
    instanceGroupedByArtifactList2 = new ArrayList<>();
    instanceGroupedByArtifactList2.add(InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
                                           .instanceKey(instanceKey1)
                                           .infrastructureMappingId(infraMappingId1)
                                           .artifact(DISPLAY_NAME_1)
                                           .lastDeployedAt(3l)
                                           .count(1)
                                           .build());
    instanceGroupedByArtifactList3 = new ArrayList<>();
    instanceGroupedByArtifactList3.add(InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
                                           .instanceKey(instanceKey1)
                                           .infrastructureMappingId(infraMappingId1)
                                           .artifact(DISPLAY_NAME_1)
                                           .lastDeployedAt(4l)
                                           .count(1)
                                           .build());
    instanceGroupedByArtifactList4 = new ArrayList<>();
    instanceGroupedByArtifactList4.add(InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
                                           .instanceKey(instanceKey1)
                                           .infrastructureMappingId(infraMappingId1)
                                           .artifact(DISPLAY_NAME_1)
                                           .lastDeployedAt(5l)
                                           .count(1)
                                           .build());

    instanceGroupedByInfrastructureList1 = new ArrayList<>();
    instanceGroupedByInfrastructureList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                                 .infrastructureId(INFRA_2)
                                                 .infrastructureName(INFRA_2)
                                                 .lastDeployedAt(3l)
                                                 .instanceGroupedByArtifactList(instanceGroupedByArtifactList2)
                                                 .build());
    instanceGroupedByInfrastructureList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                                 .infrastructureId(INFRA_1)
                                                 .infrastructureName(INFRA_1)
                                                 .lastDeployedAt(2l)
                                                 .instanceGroupedByArtifactList(instanceGroupedByArtifactList1)
                                                 .build());
    instanceGroupedByInfrastructureList2 = new ArrayList<>();
    instanceGroupedByInfrastructureList2.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                                 .infrastructureId(INFRA_1)
                                                 .infrastructureName(INFRA_1)
                                                 .lastDeployedAt(4l)
                                                 .instanceGroupedByArtifactList(instanceGroupedByArtifactList3)
                                                 .build());
    instanceGroupedByInfrastructureList3 = new ArrayList<>();
    instanceGroupedByInfrastructureList3.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                                 .infrastructureId(INFRA_1)
                                                 .infrastructureName(INFRA_1)
                                                 .lastDeployedAt(5l)
                                                 .instanceGroupedByArtifactList(instanceGroupedByArtifactList4)
                                                 .build());

    instanceGroupedByClusterList1 = new ArrayList<>();
    instanceGroupedByClusterList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                          .clusterId(INFRA_2)
                                          .agentId(INFRA_2)
                                          .lastDeployedAt(3l)
                                          .instanceGroupedByArtifactList(instanceGroupedByArtifactList2)
                                          .build());
    instanceGroupedByClusterList1.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                          .clusterId(INFRA_1)
                                          .agentId(INFRA_1)
                                          .lastDeployedAt(2l)
                                          .instanceGroupedByArtifactList(instanceGroupedByArtifactList1)
                                          .build());
    instanceGroupedByClusterList2 = new ArrayList<>();
    instanceGroupedByClusterList2.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                          .clusterId(INFRA_1)
                                          .agentId(INFRA_1)
                                          .lastDeployedAt(4l)
                                          .instanceGroupedByArtifactList(instanceGroupedByArtifactList3)
                                          .build());
    instanceGroupedByClusterList3 = new ArrayList<>();
    instanceGroupedByClusterList3.add(InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure.builder()
                                          .clusterId(INFRA_1)
                                          .agentId(INFRA_1)
                                          .lastDeployedAt(5l)
                                          .instanceGroupedByArtifactList(instanceGroupedByArtifactList4)
                                          .build());

    instanceGroupedByEnvironmentTypeList1 = new ArrayList<>();
    instanceGroupedByEnvironmentTypeList1.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.Production)
            .lastDeployedAt(4l)
            .instanceGroupedByInfrastructureList(instanceGroupedByInfrastructureList2)
            .build());
    instanceGroupedByEnvironmentTypeList1.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.PreProduction)
            .lastDeployedAt(3l)
            .instanceGroupedByInfrastructureList(instanceGroupedByInfrastructureList1)
            .build());
    instanceGroupedByEnvironmentTypeList2 = new ArrayList<>();
    instanceGroupedByEnvironmentTypeList2.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.Production)
            .lastDeployedAt(5l)
            .instanceGroupedByInfrastructureList(instanceGroupedByInfrastructureList3)
            .build());

    instanceGroupedByEnvironmentTypeListGitOps1 = new ArrayList<>();
    instanceGroupedByEnvironmentTypeListGitOps1.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.Production)
            .lastDeployedAt(4l)
            .instanceGroupedByInfrastructureList(instanceGroupedByClusterList2)
            .build());
    instanceGroupedByEnvironmentTypeListGitOps1.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.PreProduction)
            .lastDeployedAt(3l)
            .instanceGroupedByInfrastructureList(instanceGroupedByClusterList1)
            .build());
    instanceGroupedByEnvironmentTypeListGitOps2 = new ArrayList<>();
    instanceGroupedByEnvironmentTypeListGitOps2.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType.builder()
            .environmentType(EnvironmentType.Production)
            .lastDeployedAt(5l)
            .instanceGroupedByInfrastructureList(instanceGroupedByClusterList3)
            .build());

    instanceGroupedByEnvironmentList = new ArrayList<>();
    instanceGroupedByEnvironmentList.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment.builder()
            .envId(ENV_2)
            .instanceGroupedByEnvironmentTypeList(instanceGroupedByEnvironmentTypeList2)
            .envGroups(new ArrayList<>())
            .envName(ENV_2)
            .lastDeployedAt(5l)
            .build());
    instanceGroupedByEnvironmentList.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment.builder()
            .envId(ENV_1)
            .envGroups(new ArrayList<>())
            .instanceGroupedByEnvironmentTypeList(instanceGroupedByEnvironmentTypeList1)
            .envName(ENV_1)
            .lastDeployedAt(4l)
            .build());

    instanceGroupedByEnvironmentListGitOps = new ArrayList<>();
    instanceGroupedByEnvironmentListGitOps.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment.builder()
            .envId(ENV_2)
            .envGroups(new ArrayList<>())
            .instanceGroupedByEnvironmentTypeList(instanceGroupedByEnvironmentTypeListGitOps2)
            .envName(ENV_2)
            .lastDeployedAt(5l)
            .build());
    instanceGroupedByEnvironmentListGitOps.add(
        InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment.builder()
            .envId(ENV_1)
            .envGroups(new ArrayList<>())
            .instanceGroupedByEnvironmentTypeList(instanceGroupedByEnvironmentTypeListGitOps1)
            .envName(ENV_1)
            .lastDeployedAt(4l)
            .build());

    envIdToNameMap = new HashMap<>();
    envIdToNameMap.put(ENV_1, ENV_1);
    envIdToNameMap.put(ENV_2, ENV_2);

    infraIdToNameMap = new HashMap<>();
    infraIdToNameMap.put(INFRA_1, INFRA_1);
    infraIdToNameMap.put(INFRA_2, INFRA_2);

    instanceCountMap = getInstanceCountMap();
  }

  private List<ActiveServiceInstanceInfoWithEnvType> getActiveServiceInstanceInfoWithEnvTypeListNonGitOps() {
    List<ActiveServiceInstanceInfoWithEnvType> activeServiceInstanceInfoWithEnvTypeList = new ArrayList<>();
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1,
        ENV_1, ENV_1, EnvironmentType.PreProduction, INFRA_1, INFRA_1, null, null, 1l, DISPLAY_NAME_1, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey2, infraMappingId2,
        ENV_1, ENV_1, EnvironmentType.PreProduction, INFRA_1, INFRA_1, null, null, 2l, DISPLAY_NAME_2, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1,
        ENV_1, ENV_1, EnvironmentType.PreProduction, INFRA_2, INFRA_2, null, null, 3l, DISPLAY_NAME_1, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(
        new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1, ENV_1, ENV_1,
            EnvironmentType.Production, INFRA_1, INFRA_1, null, null, 4l, DISPLAY_NAME_1, 1, lastPipelineExecutionName,
            lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId, rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(
        new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1, ENV_2, ENV_2,
            EnvironmentType.Production, INFRA_1, INFRA_1, null, null, 5l, DISPLAY_NAME_1, 1, lastPipelineExecutionName,
            lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId, rollbackStatus));
    return activeServiceInstanceInfoWithEnvTypeList;
  }

  private List<ActiveServiceInstanceInfoWithEnvType> getActiveServiceInstanceInfoWithEnvTypeListGitOps() {
    List<ActiveServiceInstanceInfoWithEnvType> activeServiceInstanceInfoWithEnvTypeList = new ArrayList<>();
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1,
        ENV_1, ENV_1, EnvironmentType.PreProduction, null, null, INFRA_1, INFRA_1, 1l, DISPLAY_NAME_1, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey2, infraMappingId2,
        ENV_1, ENV_1, EnvironmentType.PreProduction, null, null, INFRA_1, INFRA_1, 2l, DISPLAY_NAME_2, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1,
        ENV_1, ENV_1, EnvironmentType.PreProduction, null, null, INFRA_2, INFRA_2, 3l, DISPLAY_NAME_1, 1,
        lastPipelineExecutionName, lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId,
        rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(
        new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1, ENV_1, ENV_1,
            EnvironmentType.Production, null, null, INFRA_1, INFRA_1, 4l, DISPLAY_NAME_1, 1, lastPipelineExecutionName,
            lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId, rollbackStatus));
    activeServiceInstanceInfoWithEnvTypeList.add(
        new ActiveServiceInstanceInfoWithEnvType(instanceKey1, infraMappingId1, ENV_2, ENV_2,
            EnvironmentType.Production, null, null, INFRA_1, INFRA_1, 5l, DISPLAY_NAME_1, 1, lastPipelineExecutionName,
            lastPipelineExecutionId, stageNodeExecutionId, stageStatus, stageSetupId, rollbackStatus));
    return activeServiceInstanceInfoWithEnvTypeList;
  }

  private InstanceGroupedOnArtifactList getInstanceGroupedOnArtifactList(boolean isGitOps) {
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure> instanceGroupedOnInfrastructure2 =
        new ArrayList<>();
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure> instanceGroupedOnInfrastructure3 =
        new ArrayList<>();
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure> instanceGroupedOnInfrastructure4 =
        new ArrayList<>();
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure> instanceGroupedOnInfrastructure1 =
        new ArrayList<>();
    InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure
        .InstanceGroupedOnInfrastructureBuilder infrastructureBuilder =
        InstanceGroupedOnArtifactList.InstanceGroupedOnInfrastructure.builder().count(1);

    if (isGitOps) {
      instanceGroupedOnInfrastructure1.add(
          infrastructureBuilder.clusterId(INFRA_2).agentId(INFRA_2).lastDeployedAt(3l).build());
      instanceGroupedOnInfrastructure1.add(
          infrastructureBuilder.clusterId(INFRA_1).agentId(INFRA_1).lastDeployedAt(1l).build());
    } else {
      instanceGroupedOnInfrastructure1.add(
          infrastructureBuilder.infrastructureId(INFRA_2).infrastructureName(INFRA_2).lastDeployedAt(3l).build());
      instanceGroupedOnInfrastructure1.add(
          infrastructureBuilder.infrastructureId(INFRA_1).infrastructureName(INFRA_1).lastDeployedAt(1l).build());
    }

    instanceGroupedOnInfrastructure2.add(infrastructureBuilder.lastDeployedAt(4l).build());
    instanceGroupedOnInfrastructure3.add(infrastructureBuilder.lastDeployedAt(2l).build());
    instanceGroupedOnInfrastructure4.add(infrastructureBuilder.lastDeployedAt(5l).build());

    InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironmentType
        .InstanceGroupedOnEnvironmentTypeBuilder environmentTypeBuilder =
        InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironmentType.builder().environmentType(
            EnvironmentType.Production);
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironmentType> instanceGroupedOnEnvironmentType1 =
        new ArrayList<>();
    instanceGroupedOnEnvironmentType1.add(
        environmentTypeBuilder.instanceGroupedOnInfrastructureList(instanceGroupedOnInfrastructure2)
            .lastDeployedAt(4l)
            .build());
    instanceGroupedOnEnvironmentType1.add(environmentTypeBuilder.environmentType(EnvironmentType.PreProduction)
                                              .lastDeployedAt(3l)
                                              .instanceGroupedOnInfrastructureList(instanceGroupedOnInfrastructure1)
                                              .build());
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironmentType> instanceGroupedOnEnvironmentType2 =
        new ArrayList<>();
    instanceGroupedOnEnvironmentType2.add(
        environmentTypeBuilder.instanceGroupedOnInfrastructureList(instanceGroupedOnInfrastructure3)
            .lastDeployedAt(2l)
            .build());
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironmentType> instanceGroupedOnEnvironmentType3 =
        new ArrayList<>();
    instanceGroupedOnEnvironmentType3.add(environmentTypeBuilder.environmentType(EnvironmentType.Production)
                                              .instanceGroupedOnInfrastructureList(instanceGroupedOnInfrastructure4)
                                              .lastDeployedAt(5l)
                                              .build());

    InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironment.InstanceGroupedOnEnvironmentBuilder environmentBuilder =
        InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironment.builder()
            .envId(ENV_2)
            .envName(ENV_2)
            .lastDeployedAt(5l)
            .instanceGroupedOnEnvironmentTypeList(instanceGroupedOnEnvironmentType3);
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironment> instanceGroupedOnEnvironment1 = new ArrayList<>();
    instanceGroupedOnEnvironment1.add(environmentBuilder.build());
    instanceGroupedOnEnvironment1.add(environmentBuilder.envId(ENV_1)
                                          .envName(ENV_1)
                                          .lastDeployedAt(4l)
                                          .instanceGroupedOnEnvironmentTypeList(instanceGroupedOnEnvironmentType1)
                                          .build());
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnEnvironment> instanceGroupedOnEnvironment2 = new ArrayList<>();
    instanceGroupedOnEnvironment2.add(environmentBuilder.lastDeployedAt(2l)
                                          .instanceGroupedOnEnvironmentTypeList(instanceGroupedOnEnvironmentType2)
                                          .build());

    InstanceGroupedOnArtifactList.InstanceGroupedOnArtifact.InstanceGroupedOnArtifactBuilder artifactBuilder =
        InstanceGroupedOnArtifactList.InstanceGroupedOnArtifact.builder()
            .artifact(DISPLAY_NAME_1)
            .lastDeployedAt(5l)
            .instanceGroupedOnEnvironmentList(instanceGroupedOnEnvironment1);
    List<InstanceGroupedOnArtifactList.InstanceGroupedOnArtifact> instanceGroupedOnArtifact = new ArrayList<>();
    instanceGroupedOnArtifact.add(artifactBuilder.build());
    instanceGroupedOnArtifact.add(artifactBuilder.artifact(DISPLAY_NAME_2)
                                      .lastDeployedAt(2l)
                                      .instanceGroupedOnEnvironmentList(instanceGroupedOnEnvironment2)
                                      .build());

    return InstanceGroupedOnArtifactList.builder().instanceGroupedOnArtifactList(instanceGroupedOnArtifact).build();
  }

  private Map<String,
      Map<EnvironmentType, Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>>>>
  getInstanceCountMap() {
    Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> buildToCountMap1 = new HashMap<>();
    buildToCountMap1.put(DISPLAY_NAME_1,
        InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
            .instanceKey(instanceKey1)
            .infrastructureMappingId(infraMappingId1)
            .count(1)
            .artifact(DISPLAY_NAME_1)
            .lastDeployedAt(1l)
            .build());
    buildToCountMap1.put(DISPLAY_NAME_2,
        InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
            .instanceKey(instanceKey2)
            .infrastructureMappingId(infraMappingId2)
            .artifact(DISPLAY_NAME_2)
            .count(1)
            .lastDeployedAt(2l)
            .build());
    Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> buildToCountMap2 = new HashMap<>();
    buildToCountMap2.put(DISPLAY_NAME_1,
        InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
            .instanceKey(instanceKey1)
            .infrastructureMappingId(infraMappingId1)
            .artifact(DISPLAY_NAME_1)
            .count(1)
            .lastDeployedAt(3l)
            .build());
    Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> buildToCountMap3 = new HashMap<>();
    buildToCountMap3.put(DISPLAY_NAME_1,
        InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
            .instanceKey(instanceKey1)
            .infrastructureMappingId(infraMappingId1)
            .artifact(DISPLAY_NAME_1)
            .count(1)
            .lastDeployedAt(4l)
            .build());
    Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> buildToCountMap4 = new HashMap<>();
    buildToCountMap4.put(DISPLAY_NAME_1,
        InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact.builder()
            .instanceKey(instanceKey1)
            .infrastructureMappingId(infraMappingId1)
            .artifact(DISPLAY_NAME_1)
            .count(1)
            .lastDeployedAt(5l)
            .build());

    Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>> infraToBuildMap1 =
        new HashMap<>();
    infraToBuildMap1.put(INFRA_1, buildToCountMap1);
    infraToBuildMap1.put(INFRA_2, buildToCountMap2);
    Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>> infraToBuildMap2 =
        new HashMap<>();
    infraToBuildMap2.put(INFRA_1, buildToCountMap3);
    Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>> infraToBuildMap3 =
        new HashMap<>();
    infraToBuildMap3.put(INFRA_1, buildToCountMap4);

    Map<EnvironmentType, Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>>>
        environmentTypeToInfraMap1 = new HashMap<>();
    environmentTypeToInfraMap1.put(EnvironmentType.PreProduction, infraToBuildMap1);
    environmentTypeToInfraMap1.put(EnvironmentType.Production, infraToBuildMap2);
    Map<EnvironmentType, Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>>>
        environmentTypeToInfraMap2 = new HashMap<>();
    environmentTypeToInfraMap2.put(EnvironmentType.Production, infraToBuildMap3);

    Map<String,
        Map<EnvironmentType, Map<String, Map<String, InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact>>>>
        environmentToTypeMap = new HashMap<>();
    environmentToTypeMap.put(ENV_1, environmentTypeToInfraMap1);
    environmentToTypeMap.put(ENV_2, environmentTypeToInfraMap2);

    return environmentToTypeMap;
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByEnvironmentListHelper_NonGitOps() {
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.getInstanceGroupedByEnvironmentListHelper(
            null, getActiveServiceInstanceInfoWithEnvTypeListNonGitOps(), false, null);
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList2 =
        InstanceGroupedByEnvironmentList.builder()
            .instanceGroupedByEnvironmentList(instanceGroupedByEnvironmentList)
            .build();
    assertThat(instanceGroupedByEnvironmentList1).isEqualTo(instanceGroupedByEnvironmentList2);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByEnvironmentListHelper_GitOps() {
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.getInstanceGroupedByEnvironmentListHelper(
            null, getActiveServiceInstanceInfoWithEnvTypeListGitOps(), true, null);
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList2 =
        InstanceGroupedByEnvironmentList.builder()
            .instanceGroupedByEnvironmentList(instanceGroupedByEnvironmentListGitOps)
            .build();
    assertThat(instanceGroupedByEnvironmentList1).isEqualTo(instanceGroupedByEnvironmentList2);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupByEnvironment_NonGitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment> instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.groupByEnvironment(
            instanceCountMap, infraIdToNameMap, envIdToNameMap, Collections.emptyMap(), false);
    assertThat(instanceGroupedByEnvironmentList1).isEqualTo(instanceGroupedByEnvironmentList);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupByEnvironment_GitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironment> instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.groupByEnvironment(
            instanceCountMap, infraIdToNameMap, envIdToNameMap, Collections.emptyMap(), true);
    assertThat(instanceGroupedByEnvironmentList1).isEqualTo(instanceGroupedByEnvironmentListGitOps);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupedByEnvironmentTypes_NonGitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType> instanceGroupedByEnvironmentTypeListResult =
        DashboardServiceHelper.groupedByEnvironmentTypes(instanceCountMap.get(ENV_1), infraIdToNameMap, false);
    assertThat(instanceGroupedByEnvironmentTypeListResult).isEqualTo(instanceGroupedByEnvironmentTypeList1);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupedByEnvironmentTypes_GitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByEnvironmentType> instanceGroupedByEnvironmentTypeListResult =
        DashboardServiceHelper.groupedByEnvironmentTypes(instanceCountMap.get(ENV_1), infraIdToNameMap, true);
    assertThat(instanceGroupedByEnvironmentTypeListResult).isEqualTo(instanceGroupedByEnvironmentTypeListGitOps1);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupedByInfrastructure_NonGitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByInfrastructureResult =
        DashboardServiceHelper.groupedByInfrastructures(
            instanceCountMap.get(ENV_1).get(EnvironmentType.PreProduction), infraIdToNameMap, false);
    assertThat(instanceGroupedByInfrastructureResult).isEqualTo(instanceGroupedByInfrastructureList1);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupedByInfrastructure_GitOps() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByInfrastructure> instanceGroupedByInfrastructureResult =
        DashboardServiceHelper.groupedByInfrastructures(
            instanceCountMap.get(ENV_1).get(EnvironmentType.PreProduction), infraIdToNameMap, true);
    assertThat(instanceGroupedByInfrastructureResult).isEqualTo(instanceGroupedByClusterList1);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_groupedByArtifacts() {
    List<InstanceGroupedByEnvironmentList.InstanceGroupedByArtifact> instanceGroupedByArtifactListResult =
        DashboardServiceHelper.groupedByArtifacts(
            instanceCountMap.get(ENV_1).get(EnvironmentType.PreProduction).get(INFRA_1));
    assertThat(instanceGroupedByArtifactListResult).isEqualTo(instanceGroupedByArtifactList1);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByArtifactListHelper_NonGitOps() {
    InstanceGroupedOnArtifactList instanceGroupedOnArtifactList =
        DashboardServiceHelper.getInstanceGroupedByArtifactListHelper(
            getActiveServiceInstanceInfoWithEnvTypeListNonGitOps(), false, null, null);
    assertThat(instanceGroupedOnArtifactList).isEqualTo(getInstanceGroupedOnArtifactList(false));
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByArtifactListHelper_GitOps() {
    InstanceGroupedOnArtifactList instanceGroupedOnArtifactList =
        DashboardServiceHelper.getInstanceGroupedByArtifactListHelper(
            getActiveServiceInstanceInfoWithEnvTypeListGitOps(), true, null, null);
    assertThat(instanceGroupedOnArtifactList).isEqualTo(getInstanceGroupedOnArtifactList(true));
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_buildOpenTaskQuery() {
    String query =
        "select DISTINCT ON(pipeline_execution_summary_cd_id) pipeline_execution_summary_cd_id, execution_failure_details from service_infra_info where accountid = 'accountId' and orgidentifier = 'orgId' and projectidentifier = 'projectId' and service_id = 'serviceId' and service_startts > 1000 order by pipeline_execution_summary_cd_id, service_endts DESC";
    assertThat(query).isEqualTo(
        DashboardServiceHelper.buildOpenTaskQuery(ACCOUNT_ID, ORG_ID, PROJECT_ID, SERVICE_ID, 1000l));
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_queryToFetchExecutionIdAndArtifactDetails() {
    String query =
        "select accountid, orgidentifier, projectidentifier, service_id, service_name, artifact_display_name, artifact_image, tag, pipeline_execution_summary_cd_id, service_startts from service_infra_info where accountid = 'accountId' and orgidentifier = 'orgId' and projectidentifier = 'projectId' and service_id is not null and service_startts >= 3 and service_startts <= 6 and service_id = 'serviceId' and artifact_display_name = 'displayName1' and artifact_image = 'image' and tag = 'tag'";
    String queryResult = DashboardServiceHelper.queryToFetchExecutionIdAndArtifactDetails(
        ACCOUNT_ID, ORG_ID, PROJECT_ID, SERVICE_ID, 3l, 6l, IMAGE, TAG, DISPLAY_NAME_1);
    assertThat(query).isEqualTo(queryResult);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_queryToFetchStatusOfExecution() {
    String query =
        "select id, status from pipeline_execution_summary_cd where accountid = 'accountId' and orgidentifier = 'orgId' and projectidentifier = 'projectId' and id = any (?) and status = 'status'";
    String queryResult = DashboardServiceHelper.queryToFetchStatusOfExecution(ACCOUNT_ID, ORG_ID, PROJECT_ID, STATUS);
    assertThat(query).isEqualTo(queryResult);
  }

  @Test
  @Owner(developers = ABHISHEK)
  @Category(UnitTests.class)
  public void test_getScopeEqualityCriteria() {
    String criteria = "accountid = 'accountId' and orgidentifier = 'orgId' and projectidentifier = 'projectId'";
    String criteriaResult = DashboardServiceHelper.getScopeEqualityCriteria(ACCOUNT_ID, ORG_ID, PROJECT_ID);
    assertThat(criteria).isEqualTo(criteriaResult);
    criteria = "accountid = 'accountId' and orgidentifier = 'orgId'";
    criteriaResult = DashboardServiceHelper.getScopeEqualityCriteria(ACCOUNT_ID, ORG_ID, null);
    assertThat(criteria).isEqualTo(criteriaResult);
    criteria = "accountid = 'accountId'";
    criteriaResult = DashboardServiceHelper.getScopeEqualityCriteria(ACCOUNT_ID, null, null);
    assertThat(criteria).isEqualTo(criteriaResult);
  }

  @Test
  @Owner(developers = SOURABH)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByEnvironmentListHelperRevamp() {
    Page<EnvironmentGroupEntity> page = mock(Page.class);
    when(page.getContent()).thenReturn(Arrays.asList(getEnvGrp("envGrp1"), getEnvGrp("envGrp2")));
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.getInstanceGroupedByEnvironmentListHelper(
            "envGrp1", getActiveServiceInstanceInfoWithEnvTypeListGitOps(), true, page);

    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList().size()).isEqualTo(1);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList()
                   .get(0)
                   .getInstanceGroupedByEnvironmentTypeList()
                   .size())
        .isEqualTo(2);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList().get(0).getEnvGroups().size())
        .isEqualTo(2);
  }

  @Test
  @Owner(developers = SOURABH)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByEnvironmentListHelperRevampForAllEnvGrp() {
    Page<EnvironmentGroupEntity> page = mock(Page.class);
    when(page.getContent()).thenReturn(Arrays.asList(getEnvGrp("envGrp1"), getEnvGrp("envGrp2")));
    InstanceGroupedByEnvironmentList instanceGroupedByEnvironmentList1 =
        DashboardServiceHelper.getInstanceGroupedByEnvironmentListHelper(
            null, getActiveServiceInstanceInfoWithEnvTypeListGitOps(), true, page);

    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList().size()).isEqualTo(2);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList()
                   .get(0)
                   .getInstanceGroupedByEnvironmentTypeList()
                   .size())
        .isEqualTo(1);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList()
                   .get(1)
                   .getInstanceGroupedByEnvironmentTypeList()
                   .size())
        .isEqualTo(2);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList().get(0).getEnvGroups().size())
        .isEqualTo(0);
    assertThat(instanceGroupedByEnvironmentList1.getInstanceGroupedByEnvironmentList().get(1).getEnvGroups().size())
        .isEqualTo(2);
  }

  @Test
  @Owner(developers = SOURABH)
  @Category(UnitTests.class)
  public void test_getInstanceGroupedByArtifactListHelperRevamp() {
    Page<EnvironmentGroupEntity> page = mock(Page.class);
    when(page.getContent()).thenReturn(Arrays.asList(getEnvGrp("envGrp1"), getEnvGrp("envGrp2")));
    InstanceGroupedOnArtifactList instanceGroupedOnArtifactList =
        DashboardServiceHelper.getInstanceGroupedByArtifactListHelper(
            getActiveServiceInstanceInfoWithEnvTypeListNonGitOps(), false, page, null);
    assertThat(instanceGroupedOnArtifactList).isEqualTo(getInstanceGroupedOnArtifactList(false));
    assertThat(instanceGroupedOnArtifactList.getInstanceGroupedOnArtifactList().size()).isEqualTo(2);
    assertThat(instanceGroupedOnArtifactList.getInstanceGroupedOnArtifactList()
                   .get(0)
                   .getInstanceGroupedOnEnvironmentList()
                   .size())
        .isEqualTo(2);
    assertThat(instanceGroupedOnArtifactList.getInstanceGroupedOnArtifactList()
                   .get(1)
                   .getInstanceGroupedOnEnvironmentList()
                   .size())
        .isEqualTo(1);
  }

  private EnvironmentGroupEntity getEnvGrp(String envGrp) {
    return EnvironmentGroupEntity.builder()
        .name(envGrp)
        .accountId(ACCOUNT_ID)
        .orgIdentifier(ORG_ID)
        .projectIdentifier(PROJECT_ID)
        .identifier(envGrp)
        .envIdentifiers(Arrays.asList(ENV_1))
        .build();
  }
}