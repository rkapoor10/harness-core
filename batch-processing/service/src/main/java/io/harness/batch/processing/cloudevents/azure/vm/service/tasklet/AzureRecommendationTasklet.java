/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.batch.processing.cloudevents.azure.vm.service.tasklet;

import io.harness.batch.processing.ccm.CCMJobConstants;
import io.harness.batch.processing.cloudevents.azure.vm.service.AzureRecommendationService;
import io.harness.batch.processing.cloudevents.azure.vm.service.helper.AzureConfigHelper;
import io.harness.ccm.commons.beans.JobConstants;
import io.harness.ccm.commons.dao.recommendation.AzureRecommendationDAO;
import io.harness.ccm.commons.entities.azure.AzureRecommendation;

import software.wings.beans.AzureAccountAttributes;

import com.google.inject.Singleton;
import io.fabric8.utils.Lists;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Singleton
public class AzureRecommendationTasklet implements Tasklet {
  @Autowired private AzureConfigHelper azureConfigHelper;
  @Autowired private AzureRecommendationService azureRecommendationService;
  @Autowired private AzureRecommendationDAO azureRecommendationDAO;

  @Override
  public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
    log.info("Running the Azure recommendation job");
    final JobConstants jobConstants = CCMJobConstants.fromContext(chunkContext);
    String accountId = jobConstants.getAccountId();
    Instant startTime = Instant.ofEpochMilli(jobConstants.getJobStartTime());
    Map<String, AzureAccountAttributes> azureAccountAttributes = azureConfigHelper.getAzureAccountAttributes(accountId);

    if (!azureAccountAttributes.isEmpty()) {
      for (Map.Entry<String, AzureAccountAttributes> azureAccountAttributesEntry : azureAccountAttributes.entrySet()) {
        List<AzureRecommendation> azureRecommendationList =
            azureRecommendationService.getRecommendations(accountId, azureAccountAttributesEntry.getValue());
        if (!Lists.isNullOrEmpty(azureRecommendationList)) {
          for (AzureRecommendation azureRecommendation : azureRecommendationList) {
            try {
              azureRecommendation = azureRecommendationDAO.saveRecommendation(azureRecommendation);
              azureRecommendationDAO.upsertCeRecommendation(
                  azureRecommendation, startTime, getResourceGroupId(azureRecommendation.getRecommendationId()));
            } catch (Exception e) {
              log.error("Couldn't save Azure recommendation: {}", azureRecommendation, e);
            }
          }
        }
      }
    }
    return null;
  }

  private String getResourceGroupId(String recommendationId) {
    String[] splitRecommendationId = recommendationId.split("/");
    if (splitRecommendationId.length > 4) {
      return splitRecommendationId[4];
    }
    return null;
  }
}
