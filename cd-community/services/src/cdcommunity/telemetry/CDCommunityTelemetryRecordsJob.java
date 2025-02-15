/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.telemetry.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class CdCommunityTelemetryRecordsJob {
  public static final int TWENTY_FOUR_HRS_IN_SECS = 86400;

  @Inject @Named("CdCommunityTelemetryPublisherExecutor") private ScheduledExecutorService executorService;
  @Inject private CdCommunityTelemetryPublisher publisher;

  public void scheduleTasks() {
    long initialDelay = 180;

    try {
      log.info("CdCommunityTelemetryRecordsJob scheduler starting");
      executorService.scheduleAtFixedRate(
              () -> publisher.recordTelemetry(), initialDelay, TWENTY_FOUR_HRS_IN_SECS, TimeUnit.SECONDS);
      log.info("CdCommunityTelemetryRecordsJob scheduler started");
    } catch (Exception e) {
      log.error("Exception while creating the scheduled job to track Cd Community license services", e);
    }
  }
}
