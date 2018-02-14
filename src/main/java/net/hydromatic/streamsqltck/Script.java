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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/** A test script.
 *
 * <p>Consists of a query, input data, and expected output data.
 *
 * <p>It is immutable, and cannot be created from outside this package.
 * End-users should use a {@link ScriptBuilder}.
 */
public class Script {
  public final Map<String, Stream> definitions;
  public final Map<String, String> queries;
  public final List<Insert> inputs;
  public final Map<String, List> expectations;

  /** Creates a Script.
   *
   * <p>End users typically use {@link ScriptBuilder#build}.
   *
   * @param definitions Definitions of streams
   * @param queries Queries to be run by the script
   * @param inputs Input records
   * @param expectations Expected output records
   */
  Script(Map<String, Stream> definitions, Map<String, String> queries,
      List<Insert> inputs, Map<String, List> expectations) {
    this.definitions = ImmutableMap.copyOf(definitions);
    this.queries = ImmutableMap.copyOf(queries);
    this.inputs = ImmutableList.copyOf(inputs);
    this.expectations = ImmutableMap.copyOf(expectations);
  }

  /** Creates a builder that you can use to create a Script. */
  public static ScriptBuilder builder() {
    return new ScriptBuilder();
  }
}

// End Script.java
