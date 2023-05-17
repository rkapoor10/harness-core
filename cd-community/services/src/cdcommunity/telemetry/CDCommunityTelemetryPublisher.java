/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.telemetry;

import static io.harness.annotations.dev.HarnessTeam.CV;
import static io.harness.telemetry.Destination.ALL;

import io.harness.ModuleType;
import io.harness.account.utils.AccountUtils;
import io.harness.annotations.dev.OwnedBy;
import io.harness.cdcommunity.services.core.api.CDCommunityTelemetrySentStatusService;
import io.harness.cdcommunity.services.src.cdcommunity.CDCommunityLicenseUsageDTO; // how to check path
import io.harness.cdcommunity.services.src.cdcommunity.CDCommunityLicenseUsageImpl; // how to  check path
import io.harness.data.structure.EmptyPredicate;
import io.harness.telemetry.TelemetryOption;
import io.harness.telemetry.TelemetryReporter;

import com.google.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@OwnedBy(CV)
public class CDCommunityTelemetryPublisher {
  @Inject private CDCommunityLicenseUsageImpl cdCommunityLicenseUsageService;
  @Inject private TelemetryReporter telemetryReporter;

  @Inject AccountUtils accountUtils;
  @Inject private CDCommunityTelemetrySentStatusService cdCommunityTelemetrySentStatusService;

  private static final String COUNT_ACTIVE_SERVICES_LICENSES = "cdcommunity_license_services_monitored";
  // Locking for a bit less than one day. It's ok to send a bit more than less considering downtime/etc
  //(24*60*60*1000)-(10*60*1000)
  private static final long A_DAY_MINUS_TEN_MINS_IN_MILLIS = 85800000;
  private static final String ACCOUNT = "Account";
  private static final String GLOBAL_ACCOUNT_ID = "__GLOBAL_ACCOUNT_ID__";
  private static final String GROUP_TYPE = "group_type";
  private static final String GROUP_ID = "group_id";

  public void recordTelemetry() {
    log.info("CDCommmunityTelemetryPublisher recordTelemetry execute started.");
    try {
      List<String> accountIdList = accountUtils.getAllAccountIds();
      for (String accountId : accountIdList) {
        try {
          sendEvent(accountId);
        } catch (Exception e) {
          log.error("Failed to send telemetry event for account {}", accountId, e);
        }
      }
    } catch (Exception e) {
      log.error("CDCommmunityTelemetryPublisher recordTelemetry execute failed.", e);
    } finally {
      log.info("CDCommmunityTelemetryPublisher recordTelemetry execute finished.");
    }
  }

  private void sendEvent(String accountId) {
    if (EmptyPredicate.isNotEmpty(accountId) && !accountId.equals(GLOBAL_ACCOUNT_ID)) {
      if (cdCommunityTelemetrySentStatusService.updateTimestampIfOlderThan(
              accountId, System.currentTimeMillis() - A_DAY_MINUS_TEN_MINS_IN_MILLIS, System.currentTimeMillis())) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(GROUP_TYPE, ACCOUNT);
        map.put(GROUP_ID, accountId);

        CDCommunityLicenseUsageDTO cdCommunityLicenseUsageDTO =
                cdCommunityLicenseUsageService.getLicenseUsage(accountId, ModuleType.CD, new Date().getTime(), null);
        map.put(COUNT_ACTIVE_SERVICES_LICENSES, cdCommunityLicenseUsageDTO.getActiveServices().getCount());

        telemetryReporter.sendGroupEvent(accountId, null, map, Collections.singletonMap(ALL, true),
                TelemetryOption.builder().sendForCommunity(true).build());
        log.info("Scheduled CDCommmunityTelemetryPublisher event sent for account [{}], with values: [{}]", accountId, map);
      } else {
        log.info("Skipping already sent account {} in past 24 hours", accountId);
      }
    }
  }
}