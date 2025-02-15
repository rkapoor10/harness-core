/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.steps.http;

import static io.harness.rule.OwnerRule.SHALINI;
import static io.harness.rule.OwnerRule.vivekveman;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.http.HttpHeaderConfig;
import io.harness.plancreator.steps.AbstractStepNode;
import io.harness.plancreator.steps.TaskSelectorYaml;
import io.harness.plancreator.steps.common.SpecParameters;
import io.harness.plancreator.steps.http.HttpStepInfo;
import io.harness.plancreator.steps.http.HttpStepNode;
import io.harness.pms.yaml.ParameterField;
import io.harness.pms.yaml.YamlUtils;
import io.harness.rule.Owner;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class HttpStepInfoTest extends CategoryTest {
  private static String url = "https://www.google.com/";
  private static String method = "GET";
  private static List<TaskSelectorYaml> delegate = Collections.singletonList(new TaskSelectorYaml("delegatename"));
  private static List<HttpHeaderConfig> headers =
      Collections.singletonList(HttpHeaderConfig.builder().key("headerkey").value("headervalue").build());

  String basicStepYaml;
  String policySetRuntimeInput;
  String policySetExpression;
  @Before
  public void setUp() {
    basicStepYaml = "name: myHttpStep\n"
        + "identifier: myHttpStep\n"
        + "type: Http\n"
        + "timeout: 10m\n"
        + "enforce:\n"
        + "  policySets:\n"
        + "  - ps1\n"
        + "  - ps2\n"
        + "spec:\n"
        + "  url: https://www.google.com\n"
        + "  method: GET\n"
        + "  headers: \n"
        + "  outputVariables: \n";
    policySetRuntimeInput = "name: myHttpStep\n"
        + "identifier: myHttpStep\n"
        + "type: Http\n"
        + "timeout: 10m\n"
        + "enforce:\n"
        + "  policySets: <+input>\n"
        + "spec:\n"
        + "  url: https://www.google.com\n"
        + "  method: GET\n"
        + "  headers: \n"
        + "  outputVariables: \n";
    policySetExpression = "name: myHttpStep\n"
        + "identifier: myHttpStep\n"
        + "type: Http\n"
        + "timeout: 10m\n"
        + "enforce:\n"
        + "  policySets:\n"
        + "  - ps1\n"
        + "  - <+step.name>\n"
        + "spec:\n"
        + "  url: https://www.google.com\n"
        + "  method: GET\n"
        + "  headers: \n"
        + "  outputVariables: \n";
  }

  @Test
  @Owner(developers = vivekveman)
  @Category(UnitTests.class)
  public void testGetSpecParametersForNullHeaders() {
    HttpStepInfo httpStepInfo =
        HttpStepInfo.infoBuilder()
            .url(ParameterField.<String>builder().value(url).build())
            .method(ParameterField.<String>builder().value(method).build())
            .headers(null)
            .delegateSelectors(ParameterField.<List<TaskSelectorYaml>>builder().value(delegate).build())
            .build();
    SpecParameters specParameters = httpStepInfo.getSpecParameters();
    HttpStepParameters httpStepParameters = (HttpStepParameters) specParameters;
    assertThat(httpStepParameters.headers).isEmpty();
    assertThat(httpStepParameters.delegateSelectors)
        .isEqualTo(
            ParameterField.builder().value(Collections.singletonList(new TaskSelectorYaml("delegatename"))).build());
    assertThat(httpStepParameters.url.getValue()).isEqualTo(url);
    assertThat(httpStepParameters.method.getValue()).isEqualTo(method);
  }
  @Test
  @Owner(developers = vivekveman)
  @Category(UnitTests.class)
  public void testGetSpecParametersForHeaders() {
    HttpStepInfo httpStepInfo =
        HttpStepInfo.infoBuilder()
            .url(ParameterField.<String>builder().value(url).build())
            .method(ParameterField.<String>builder().value(method).build())
            .headers(headers)
            .delegateSelectors(ParameterField.<List<TaskSelectorYaml>>builder().value(delegate).build())
            .build();
    SpecParameters specParameters = httpStepInfo.getSpecParameters();
    HttpStepParameters httpStepParameters = (HttpStepParameters) specParameters;
    assertThat(httpStepParameters.headers.size()).isEqualTo(1);
    //        getValue().get(0).getDelegateSelectors())
    assertThat(httpStepParameters.headers).isEqualTo(ImmutableMap.builder().put("headerkey", "headervalue").build());
    assertThat(httpStepParameters.delegateSelectors)
        .isEqualTo(
            ParameterField.builder().value(Collections.singletonList(new TaskSelectorYaml("delegatename"))).build());
    assertThat(httpStepParameters.url.getValue()).isEqualTo(url);
    assertThat(httpStepParameters.method.getValue()).isEqualTo(method);
  }
  @Test
  @Owner(developers = SHALINI)
  @Category(UnitTests.class)
  public void testDeserializeToPolicyStepNode() throws IOException {
    HttpStepNode httpStepNode = YamlUtils.read(basicStepYaml, HttpStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getValue().size(), 2);
    httpStepNode = YamlUtils.read(policySetRuntimeInput, HttpStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getExpressionValue(), "<+input>");
    httpStepNode = YamlUtils.read(policySetExpression, HttpStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getValue().get(1), "<+step.name>");
  }

  @Test
  @Owner(developers = SHALINI)
  @Category(UnitTests.class)
  public void testDeserializeToAbstractStepNode() throws IOException {
    AbstractStepNode httpStepNode = YamlUtils.read(basicStepYaml, AbstractStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getValue().size(), 2);
    httpStepNode = YamlUtils.read(policySetRuntimeInput, AbstractStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getExpressionValue(), "<+input>");
    httpStepNode = YamlUtils.read(policySetExpression, AbstractStepNode.class);
    assertThat(httpStepNode).isNotNull();
    assertEquals(httpStepNode.getEnforce().getPolicySets().getValue().get(1), "<+step.name>");
  }

  @Test
  @Owner(developers = vivekveman)
  @Category(UnitTests.class)
  public void testValidationForMandatoryFields() throws NoSuchFieldException {
    Class<HttpBaseStepInfo> httpStepInfoClass = HttpBaseStepInfo.class;
    Field urlField = httpStepInfoClass.getDeclaredField("url");
    Field methodField = httpStepInfoClass.getDeclaredField("method");
    assertThat(urlField.getAnnotation(NotNull.class)).isNotNull();
    assertThat(methodField.getAnnotation(NotNull.class)).isNotNull();
  }
}
