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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Builder for {@link Script}.
 *
 * <p>Example use:
 *
 * <blockquote>
 *     Script s =
 *       Script.builder()
 *         .definitions()
 *         .stream("ORDERS",
 *           "(TIMESTAMP rowtime, INT orderId, VARCHAR(20) product)")
 *         .end()
 *         .query("Q", "select orderId from orders where product = 'milk'")
 *         .input()
 *         .insert("ORDERS", 0, 100, "beer")
 *         .insert("ORDERS", 1, 101, "milk")
 *         .end()
 *         .expect()
 *         .row("Q", 101)
 *         .end()
 *         .build();
 * </blockquote>. */
public class ScriptBuilder {
  private final Map<String, Stream> definitions = new LinkedHashMap<>();
  private final Map<String, String> queries = new LinkedHashMap<>();
  private final List<Insert> inputs = new ArrayList<>();
  private final Map<String, List> expectations = new LinkedHashMap<>();

  ScriptBuilder() {}

  /** Starts building the definitions of this script. You can add multiple
   * {@link DefinitionsBuilder#stream(String)} elements, followed
   * by {@link DefinitionsBuilder#end end}. */
  public DefinitionsBuilder definitions() {
    return new DefinitionsBuilder();
  }

  /** Adds a query to this script. Every query has a name. */
  public ScriptBuilder query(String name, String sql) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(sql);
    if (queries.put(name, sql) != null) {
      throw new IllegalArgumentException("duplicate query " + name);
    }
    return this;
  }

  /** Starts building the expectations of this script. You can add multiple
   * {@link ExpectationsBuilder#row row} elements,
   * followed by {@link ExpectationsBuilder#end end}. */
  public ExpectationsBuilder expect() {
    return new ExpectationsBuilder();
  }

  public Script build() {
    return new Script(definitions, queries, inputs, expectations);
  }

  /** Starts building the inputs of this script. You can add multiple
   * {@link InputsBuilder#insert insert} elements,
   * followed by {@link InputsBuilder#end end}. */
  public InputsBuilder input() {
    return new InputsBuilder();
  }

  /** Builds the definitions section of a script. */
  public class DefinitionsBuilder {
    private DefinitionsBuilder() {}

    public StreamBuilder stream(String name) {
      Preconditions.checkNotNull(name);
      return new StreamBuilder(this, name);
    }

    /** Ends the definitions section and returns the parent ScriptBuilder. */
    public ScriptBuilder end() {
      return ScriptBuilder.this;
    }

    /** Applies this DefinitionsBuilder to an action. */
    public DefinitionsBuilder apply(Consumer<DefinitionsBuilder> o) {
      o.accept(this);
      return this;
    }

    private void addStream(String streamName, List<Column> columns) {
      final Stream stream = new Stream(streamName, columns);
      if (definitions.put(streamName, stream) != null) {
        throw new IllegalArgumentException("duplicate stream " + streamName);
      }
    }
  }

  /** Builds a stream definition.
   *
   * <p>Created via {@link DefinitionsBuilder#stream}. */
  public class StreamBuilder {
    private DefinitionsBuilder definitionsBuilder;
    final String streamName;
    final List<Column> columns = new ArrayList<>();

    StreamBuilder(DefinitionsBuilder definitionsBuilder, String streamName) {
      this.definitionsBuilder = definitionsBuilder;
      this.streamName = streamName;
    }

    /** Finishes the definition of this stream, registers it, and returns the
     * parent {@link DefinitionsBuilder}. */
    public DefinitionsBuilder end() {
      definitionsBuilder.addStream(streamName, columns);
      return definitionsBuilder;
    }

    /** Adds a column. */
    public StreamBuilder column(String name, String type, Integer precision,
        Integer scale, boolean nullable) {
      columns.add(new Column(name, type, precision, scale, nullable));
      return this;
    }

    /** Changes the nullability of the previous column added.
     *
     * @throws IndexOutOfBoundsException if there was no previous column */
    public StreamBuilder notNull() {
      final int index = columns.size() - 1;
      final Column column = columns.get(index);
      columns.set(index, column.withNullable(false));
      return this;
    }

    /** Adds a BOOLEAN column. */
    public StreamBuilder boolean_(String columnName) {
      return column(columnName, "BOOLEAN", null, null, true);
    }

    /** Adds a CHAR(precision) column. */
    public StreamBuilder char_(String columnName, int precision) {
      return column(columnName, "CHAR", precision, null, true);
    }

    /** Adds a VARCHAR(precision) column. */
    public StreamBuilder varchar(String columnName, int precision) {
      return column(columnName, "VARCHAR", precision, null, true);
    }

    /** Adds a BINARY(precision) column. */
    public StreamBuilder binary(String columnName, int precision) {
      return column(columnName, "BINARY", precision, null, true);
    }

    /** Adds a VARBINARY(precision) column. */
    public StreamBuilder varbinary(String columnName, int precision) {
      return column(columnName, "VARBINARY", precision, null, true);
    }

    /** Adds a BIGINT column. */
    public StreamBuilder bigint(String columnName) {
      return column(columnName, "BIGINT", null, null, true);
    }

    /** Adds an INTEGER column. */
    public StreamBuilder integer(String columnName) {
      return column(columnName, "INTEGER", null, null, true);
    }

    /** Adds a SMALLINT column. */
    public StreamBuilder smallint(String columnName) {
      return column(columnName, "SMALLINT", null, null, true);
    }

    /** Adds a TINYINT column. */
    public StreamBuilder tinyint(String columnName) {
      return column(columnName, "TINYINT", null, null, true);
    }

    /** Adds a DOUBLE column. */
    public StreamBuilder double_(String columnName) {
      return column(columnName, "DOUBLE", null, null, true);
    }

    /** Adds a REAL column. */
    public StreamBuilder real(String columnName) {
      return column(columnName, "REAL", null, null, true);
    }

    /** Adds a DECIMAL(precision, scale) column. */
    public StreamBuilder real(String columnName, int precision, int scale) {
      return column(columnName, "DECIMAL", precision, scale, true);
    }

    /** Adds a DATE column. */
    public StreamBuilder date(String columnName) {
      return column(columnName, "DATE", null, null, true);
    }

    /** Adds a TIMESTAMP column. */
    public StreamBuilder timestamp(String columnName) {
      return column(columnName, "TIMESTAMP", null, null, true);
    }

    /** Adds a TIMESTAMP column with scale. */
    public StreamBuilder timestamp(String columnName, int scale) {
      return column(columnName, "TIMESTAMP", null, scale, true);
    }

    /** Adds a TIME column. */
    public StreamBuilder time(String columnName) {
      return column(columnName, "TIME", null, null, true);
    }

    /** Adds a TIME column with scale. */
    public StreamBuilder time(String columnName, int scale) {
      return column(columnName, "TIME", null, scale, true);
    }
  }

  /** Builds the inputs section of a script. */
  public class InputsBuilder {
    private InputsBuilder() {}

    public InputsBuilder insert(String streamName, Object... values) {
      Preconditions.checkNotNull(streamName);
      if (!definitions.containsKey(streamName)) {
        throw new IllegalArgumentException("unknown target " + streamName
            + "; must occur in the definitions");
      }
      inputs.add(new Insert(streamName, Arrays.asList(values)));
      return this;
    }

    public InputsBuilder apply(Consumer<InputsBuilder> action) {
      action.accept(this);
      return this;
    }

    /** Ends the inputs section and returns the parent ScriptBuilder. */
    public ScriptBuilder end() {
      return ScriptBuilder.this;
    }
  }

  /** Builds the expectations section of a script. */
  public class ExpectationsBuilder {
    private ExpectationsBuilder() {}

    public ExpectationsBuilder row(String queryName, Object... values) {
      Preconditions.checkNotNull(queryName);
      Preconditions.checkNotNull(values);
      if (!queries.containsKey(queryName)) {
        throw new IllegalArgumentException("unknown query " + queryName);
      }
      expectations.put(queryName, ImmutableList.copyOf(values));
      return this;
    }

    /** Ends the expectations section and returns the parent ScriptBuilder. */
    public ScriptBuilder end() {
      return ScriptBuilder.this;
    }

    public ExpectationsBuilder apply(Consumer<ExpectationsBuilder> action) {
      action.accept(this);
      return this;
    }
  }

}

// End ScriptBuilder.java
