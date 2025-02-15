/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.servicelevelobjective.beans;

import io.harness.cvng.servicelevelobjective.entities.SLIRecord.SLIState;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SLIAnalyseResponse {
  private Instant timeStamp;
  private SLIState sliState;
  private long runningBadCount;
  private long runningGoodCount;
  private Long goodEventCount;
  private Long badEventCount;
}
