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

/** Definition of a Column in a Stream.
 *
 * <p>It is immutable. */
public class Column {
  /** Column name. */
  public final String name;

  /** Column type. Generally one of the standard SQL types: BIGINT, INTEGER,
   * SMALLINT, TINYINT, DOUBLE, REAL, DECIMAL, BOOLEAN, CHAR, VARCHAR, BINARY,
   * VARBINARY. */
  public final String type;

  /** Precision of type; may be null. */
  public final Integer precision;

  /** Scale of type; may be null. */
  public final Integer scale;

  /** Whether column allows NULL values. */
  public final boolean nullable;

  /** Creates a Column. */
  Column(String name, String type, Integer precision, Integer scale,
      boolean nullable) {
    this.name = Preconditions.checkNotNull(name);
    this.type = Preconditions.checkNotNull(type);
    this.precision = precision;
    this.scale = scale;
    this.nullable = nullable;
  }

  /** Returns a copy of this column with a given value for
   * {@link #nullable}. */
  Column withNullable(boolean nullable) {
    return new Column(name, type, precision, scale, nullable);
  }
}

// End Column.java
