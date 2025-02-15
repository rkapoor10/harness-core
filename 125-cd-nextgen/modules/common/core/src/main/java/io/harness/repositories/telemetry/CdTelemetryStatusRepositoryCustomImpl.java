/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.repositories.telemetry;

import io.harness.telemetry.beans.CdTelemetrySentStatus;
import io.harness.telemetry.beans.CdTelemetrySentStatus.CdTelemetrySentStatusKeys;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({ @Inject }))
public class CdTelemetryStatusRepositoryCustomImpl implements CdTelemetryStatusRepositoryCustom {
  private final MongoTemplate mongoTemplate;

  // Method functioning as a lock acquiring. Using Mongo as the locking mechanism
  @Override
  public boolean updateTimestampIfOlderThan(String accountId, long olderThanTime, long updateToTime) {
    Query query = new Query().addCriteria(new Criteria()
                                              .and(CdTelemetrySentStatusKeys.accountId)
                                              .is(accountId)
                                              .and(CdTelemetrySentStatusKeys.lastSent)
                                              .lte(olderThanTime));
    Update update = new Update().set(CdTelemetrySentStatusKeys.lastSent, updateToTime);
    FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
    CdTelemetrySentStatus result;
    try {
      // Atomic lock acquiring attempt
      // Everything after this line is critical section
      result = mongoTemplate.findAndModify(query, update, options, CdTelemetrySentStatus.class);
    } catch (DuplicateKeyException e) {
      // Account ID is unique here so setting upsert to true will throw duplicate key exception if trying to create
      // So we should return failed to acquire the lock here
      return false;
    }
    return result != null;
  }
}
