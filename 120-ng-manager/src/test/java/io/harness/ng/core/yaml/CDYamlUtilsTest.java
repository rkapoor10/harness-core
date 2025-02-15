/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CDYamlUtilsTest extends CategoryTest {
  @Test
  @Owner(developers = OwnerRule.YOGESH)
  @Category(UnitTests.class)
  public void testWriteString() throws JsonProcessingException {
    assertThat(CDYamlUtils.writeString(Map.of("k", "Some Name")).replaceFirst("---\n", "")).isEqualTo("k: Some Name\n");
    assertThat(CDYamlUtils.writeString(Map.of("k", "42")).replaceFirst("---\n", "")).isEqualTo("k: \"42\"\n");
    assertThat(CDYamlUtils.writeString(Map.of("k", new IntNode(42))).replaceFirst("---\n", "")).isEqualTo("k: 42\n");
    assertThat(CDYamlUtils.writeString(Map.of("k", new TextNode("42"))).replaceFirst("---\n", ""))
        .isEqualTo("k: \"42\"\n");
    assertThat(CDYamlUtils.writeString(Map.of("k", "true")).replaceFirst("---\n", "")).isEqualTo("k: \"true\"\n");
    assertThat(CDYamlUtils.writeString(Map.of("k", "Some \n Name")).replaceFirst("---\n", ""))
        .isEqualTo("k: \"Some \\n Name\"\n");
  }

  @Test
  @Owner(developers = OwnerRule.YOGESH)
  @Category(UnitTests.class)
  public void testWriteYamlString() {
    // should not quote a simple string
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", "foobar"))).isEqualTo("k: foobar\n");
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", "Some Name"))).isEqualTo("k: Some Name\n");
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", "42"))).isEqualTo("k: \"42\"\n");
    // should not quote an int node
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", new IntNode(42)))).isEqualTo("k: 42\n");
    // should not remove quotes from a text node containing an int
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", new TextNode("42")))).isEqualTo("k: \"42\"\n");
    // should quote a boolean
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", "true"))).isEqualTo("k: \"true\"\n");
    assertThat(CDYamlUtils.writeYamlString(Map.of("k", "Some \n Name"))).isEqualTo("k: \"Some \\n Name\"\n");
    assertThat(CDYamlUtils.writeYamlString(Map.of("k",
                   "abc\n"
                       + "foo\n"
                       + "bar")))
        .isEqualTo("k: |-\n"
            + "  abc\n"
            + "  foo\n"
            + "  bar\n");
  }
}
