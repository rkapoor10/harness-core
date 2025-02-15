/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cvng.core.services;

import java.time.Duration;

public interface CVNextGenConstants {
  String CV_NEXTGEN_RESOURCE_PREFIX = "cv-nextgen";
  String DELEGATE_DATA_COLLECTION = "delegate-data-collection";
  String CVNG_LOG_RESOURCE_PATH = "cvng-log";

  String CVNG_RISK_CATEGORY_PATH = "risk-category";
  String LOG_RECORD_RESOURCE_PATH = "log-record";
  String HOST_RECORD_RESOURCE_PATH = "host-record";
  String DELEGATE_DATA_COLLECTION_TASK = "delegate-data-collection-task";
  String VERIFICATION_SERVICE_SECRET = "VERIFICATION_SERVICE_SECRET";
  String ERROR_TRACKING_SERVICE_SECRET = "ERROR_TRACKING_SERVICE_SECRET";
  String CV_NEXT_GEN_SERVICE_ENDPOINTS_PREFIX = CV_NEXTGEN_RESOURCE_PREFIX + "/service";
  // TODO: move this to duration
  long CV_ANALYSIS_WINDOW_MINUTES = 5;
  String CV_DATA_COLLECTION_PATH = CV_NEXT_GEN_SERVICE_ENDPOINTS_PREFIX + "/cv-data-collection-task";
  String CD_CURRENT_GEN_CHANGE_EVENTS_PATH = CV_NEXT_GEN_SERVICE_ENDPOINTS_PREFIX + "/change-events";
  Duration DATA_COLLECTION_DELAY = Duration.ofMinutes(2);
  String PERFORMANCE_PACK_IDENTIFIER = "Performance";
  String ERRORS_PACK_IDENTIFIER = "Errors";
  String INFRASTRUCTURE_PACK_IDENTIFIER = "Infrastructure";
  String CUSTOM_PACK_IDENTIFIER = "Custom";
  String SPLUNK_RESOURCE_PATH = "cv-nextgen/splunk/";
  String SPLUNK_SAVED_SEARCH_PATH = "saved-searches";
  String SPLUNK_VALIDATION_RESPONSE_PATH = "validation";
  String ACTIVITY_RESOURCE = "activity";
  String KUBERNETES_RESOURCE = "kubernetes";
  String CHANGE_EVENT_RESOURCE = "change-event";
  int CVNG_MAX_PARALLEL_THREADS = 20;
  int CVNG_TIMELINE_BUCKET_COUNT = 48;
  int SRM_STATEMACHINE_MAX_THREADS = 20;
  int LOG_RECORD_THRESHOLD = 500;
  String SLASH_DELIMITER = "/";
  String ACCOUNT_IDENTIFIER_KEY = "accountIdentifier";
  String ORG_IDENTIFIER_KEY = "orgIdentifier";
  String PROJECT_IDENTIFIER_KEY = "projectIdentifier";
  String RESOURCE_IDENTIFIER_KEY = "identifier";
  String ACCOUNT_IDENTIFIER_KEY_PATH = "account/";
  String ORG_IDENTIFIER_KEY_PATH = "org/";
  String PROJECT_IDENTIFIER_KEY_PATH = "project/";
  String RESOURCE_IDENTIFIER_KEY_PATH = "identifier/";
  String ACCOUNT_PATH = ACCOUNT_IDENTIFIER_KEY_PATH + "{" + ACCOUNT_IDENTIFIER_KEY + "}";
  String ACCOUNT_PATH_WITH_RESOURCE_IDENTIFIER =
      ACCOUNT_PATH + SLASH_DELIMITER + RESOURCE_IDENTIFIER_KEY_PATH + "{" + RESOURCE_IDENTIFIER_KEY + "}";
  String ORG_PATH = ACCOUNT_PATH + SLASH_DELIMITER + ORG_IDENTIFIER_KEY_PATH + "{" + ORG_IDENTIFIER_KEY + "}";
  String ORG_PATH_WITH_RESOURCE_IDENTIFIER =
      ORG_PATH + SLASH_DELIMITER + RESOURCE_IDENTIFIER_KEY_PATH + "{" + RESOURCE_IDENTIFIER_KEY + "}";
  String PROJECT_PATH = ORG_PATH + SLASH_DELIMITER + PROJECT_IDENTIFIER_KEY_PATH + "{" + PROJECT_IDENTIFIER_KEY + "}";
  String PROJECT_PATH_WITH_RESOURCE_IDENTIFIER =
      PROJECT_PATH + SLASH_DELIMITER + RESOURCE_IDENTIFIER_KEY_PATH + "{" + RESOURCE_IDENTIFIER_KEY + "}";
  String RESOURCE_IDENTIFIER_PATH =
      SLASH_DELIMITER + RESOURCE_IDENTIFIER_KEY_PATH + "{" + RESOURCE_IDENTIFIER_KEY + "}";
  String CHANGE_EVENT_NG_PROJECT_PATH = PROJECT_PATH + "/change-event";
  String CHANGE_EVENT_NG_ACCOUNT_PATH = ACCOUNT_PATH + "/change-event";
  String HEALTH_SOURCE_PATH = PROJECT_PATH + "/health-source";
  String VERIFY_STEP_PATH = PROJECT_PATH + "/verify-step";
  String SLO_V2_PATH = SLASH_DELIMITER + "slo/v2";
  String DOWNTIME_PATH = SLASH_DELIMITER + "downtime";
  String ANNOTATION_PATH = SLASH_DELIMITER + "annotation";
  String SLO_NG_ACCOUNT_PATH = ACCOUNT_PATH + SLO_V2_PATH;
  String SLO_NG_ORG_PATH = ORG_PATH + SLO_V2_PATH;
  String SLO_NG_PROJECT_PATH = PROJECT_PATH + SLO_V2_PATH;
  String DOWNTIME_PROJECT_PATH = PROJECT_PATH + DOWNTIME_PATH;
  String ANNOTATION_PROJECT_PATH = PROJECT_PATH + ANNOTATION_PATH;
  String ANNOTATION_ACCOUNT_PATH = ACCOUNT_PATH + ANNOTATION_PATH;
  String MONITORED_SERVICE_YAML_ROOT = "monitoredService";
  String ACCOUNTS_RESOURCE_PATH = "account/{" + ACCOUNT_IDENTIFIER_KEY + "}";
  String ORGS_RESOURCE_PATH = ACCOUNTS_RESOURCE_PATH + "/orgs/{" + ORG_IDENTIFIER_KEY + "}";
  String PROJECTS_RESOURCE_PATH = ORGS_RESOURCE_PATH + "/projects/{" + PROJECT_IDENTIFIER_KEY + "}";
  String VERIFICATION_IDENTIFIER_KEY = "verifyStepExecutionId";
  String VERIFICATIONS_RESOURCE_PATH = PROJECTS_RESOURCE_PATH + "/verifications/{" + VERIFICATION_IDENTIFIER_KEY + "}";
  String LOG_FEEDBACK_RESOURCE_PATH = PROJECT_PATH + "/log-feedback";
  String LOG_FEEDBACK_ID = "logFeedbackId";
  String LOG_FEEDBACK_ID_RESOURCE_PATH = "/{" + LOG_FEEDBACK_ID + "}";
  String LOG_FEEDBACK_HISTORY_RESOURCE_PATH = "/{" + LOG_FEEDBACK_ID + "}"
      + "/history";
}
