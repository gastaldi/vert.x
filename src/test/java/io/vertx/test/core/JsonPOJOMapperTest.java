/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.test.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.vertx.core.json.JsonObject;

/**
 * @author <a href="https://github.com/lukehutch">Luke Hutchison</a>
 */
public class JsonPOJOMapperTest extends VertxTestBase {

  public static class MyType {
    public int a;
    public String b;
    public HashMap<String, Object> c = new HashMap<>();
    public List<MyType> d = new ArrayList<>();
    public List<Integer> e = new ArrayList<>();
  }

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testSerialization() {
    MyType myObj0 = new MyType() {{
      a = -1;
      b = "obj0";
      c.put("z", Arrays.asList(7, 8));
      e.add(9);
    }};
    MyType myObj1 = new MyType() {{
      a = 5;
      b = "obj1";
      c.put("x", "1");
      c.put("y", 2);
      d.add(myObj0);
      e.add(3);
    }};
    
    JsonObject jsonObject1 = JsonObject.mapFrom(myObj1);
    String jsonStr1 = jsonObject1.encode();
    assertEquals("{\"a\":5,\"b\":\"obj1\",\"c\":{\"x\":\"1\",\"y\":2},\"d\":["
        +"{\"a\":-1,\"b\":\"obj0\",\"c\":{\"z\":[7,8]},\"d\":[],\"e\":[9]}"
        + "],\"e\":[3]}", jsonStr1);

    MyType myObj1Roundtrip = jsonObject1.mapTo(MyType.class);
    assertEquals(myObj1Roundtrip.a, 5);
    assertEquals(myObj1Roundtrip.b, "obj1");
    assertEquals(myObj1Roundtrip.c.get("x"), "1");
    assertEquals(myObj1Roundtrip.c.get("y"), new Integer(2));
    assertEquals(myObj1Roundtrip.e, Arrays.asList(3));
    MyType myObj0Roundtrip = myObj1Roundtrip.d.get(0);
    assertEquals(myObj0Roundtrip.a, -1);
    assertEquals(myObj0Roundtrip.b, "obj0");
    assertEquals(myObj0Roundtrip.c.get("z"), Arrays.asList(7, 8));
    assertEquals(myObj0Roundtrip.e, Arrays.asList(9));
    
    boolean caughtCycle = false;
    try {
      myObj0.d.add(myObj0);
      JsonObject.mapFrom(myObj0);
    } catch (IllegalArgumentException e) {
      caughtCycle = true;
    }
    if (!caughtCycle) {
      fail();
    }
  }
}
