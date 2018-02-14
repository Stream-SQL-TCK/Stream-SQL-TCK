/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hydromatic.streamsqltck;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/** Unit tests for {@link net.hydromatic.streamsqltck.ScriptBuilder}. */
public class ScriptBuilderTest {
  @Test public void testDuplicateStreamNames() {
    try {
      Script.builder()
          .definitions()
          .stream("S0")
          .integer("x")
          .end()
          .stream("S1")
          .integer("x")
          .end()
          .stream("S2")
          .boolean_("b")
          .end()
          .stream("S1")
          .integer("x")
          .end();
      fail("expected throw");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("duplicate stream S1"));
    }
  }

  @Test public void testQueryNames() {
    try {
      Script.builder()
          .definitions()
          .stream("S1")
          .integer("x")
          .end()
          .end()
          .query("Q1", "values 1")
          .query("Q2", "values 2")
          .query("Q1", "values 3");
      fail("expected throw");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), is("duplicate query Q1"));
    }
  }

  /** Tests a script whose definitions section is empty. */
  @Test public void testNoDefinitions() {
    final Script script = Script.builder()
        .definitions()
        .end()
        .query("Q", "values 1")
        .expect()
        .end()
        .build();
    assertThat(script.definitions.isEmpty(), is(true));
  }

  /** Tests {@link net.hydromatic.streamsqltck.ScriptBuilder.DefinitionsBuilder#apply}
   * and similar. */
  @Test public void testApply() {
    final Script script = Script.builder()
        .definitions()
        .apply(ScriptBuilderTest::defineOrders)
        .end()
        .query("Q", "values 1")
        .input()
        .apply(ScriptBuilderTest::defineInput)
        .end()
        .expect()
        .apply(ScriptBuilderTest::defineOutput)
        .end()
        .build();
    assertThat(script.definitions.containsKey("ORDERS"), is(true));
    final Stream ordersStream = script.definitions.get("ORDERS");
    assertThat(ordersStream.name, is("ORDERS"));
    assertThat(ordersStream.columns.size(), is(3));
    assertThat(ordersStream.columns.get(0).type, is("TIMESTAMP"));
    assertThat(ordersStream.columns.get(0).precision, CoreMatchers.nullValue());
    assertThat(ordersStream.columns.get(0).scale, CoreMatchers.nullValue());
    assertThat(ordersStream.columns.get(0).nullable, is(false));
    assertThat(ordersStream.columns.get(2).type, is("VARCHAR"));
    assertThat(ordersStream.columns.get(2).precision, is(1000));
    assertThat(ordersStream.columns.get(2).scale, CoreMatchers.nullValue());
    assertThat(ordersStream.columns.get(2).nullable, is(true));
    assertThat(script.queries.containsKey("Q"), is(true));
    assertThat(script.inputs.size(), is(2));
    assertThat(script.inputs.get(0).name, is("ORDERS"));
    assertThat(script.inputs.get(0).values.size(), is(3));
    assertThat(script.inputs.get(0).values.get(0), is(0));
    assertThat(script.inputs.get(0).values.get(1), is(100));
    assertThat(script.inputs.get(0).values.get(2), is("beer"));
    assertThat(script.expectations.containsKey("Q"), is(true));
  }

  private static void defineOrders(ScriptBuilder.DefinitionsBuilder b) {
    b.stream("ORDERS")
        .timestamp("rowtime").notNull()
        .integer("orderId").notNull()
        .varchar("comments", 1000)
        .end();
  }

  private static void defineOutput(ScriptBuilder.ExpectationsBuilder b) {
    b.row("Q");
  }

  private static void defineInput(ScriptBuilder.InputsBuilder b) {
    b.insert("ORDERS", 0, 100, "beer");
    b.insert("ORDERS", 1, 101, "milk");
  }
}

// End ScriptBuilderTest.java
