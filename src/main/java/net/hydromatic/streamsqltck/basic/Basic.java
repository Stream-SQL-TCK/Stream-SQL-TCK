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
package net.hydromatic.streamsqltck.basic;

import net.hydromatic.streamsqltck.Script;
import net.hydromatic.streamsqltck.ScriptBuilder;

/** A collection of scripts that test basic streaming SQL functionality. */
public class Basic {
  public Script select() {
    return Script.builder()
        .definitions()
        .apply(Basic::defineOrders)
        .end()
        .query("Q", "select * from Orders")
        .input()
        .insert("ORDERS", 0, 100, "beer")
        .insert("ORDERS", 1, 101, "milk")
        .end()
        .expect()
        .row("Q", 0, 100, "beer")
        .row("Q", 0, 101, "milk")
        .end()
        .build();
  }

  public Script selectWhere() {
    return Script.builder()
        .definitions()
        .apply(Basic::defineOrders)
        .end()
        .query("Q", "select orderId from orders where product = 'milk'")
        .input()
        .insert("ORDERS", 0, 100, "beer")
        .insert("ORDERS", 1, 101, "milk")
        .end()
        .expect()
        .row("Q", 101)
        .end()
        .build();
  }

  private static void defineOrders(ScriptBuilder.DefinitionsBuilder b) {
    b.stream("Orders")
        .timestamp("rowtime").notNull()
        .integer("orderId").notNull()
        .varchar("product", 20).notNull()
        .end();
  }
}

// End Basic.java
