/*
 * Copyright 2018 The StartupOS Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.startupos.common.flags;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.startupos.common.flags.testpackage1.FlagDescTestClass;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FlagsTest {
  public static final String TESTFLAGS_PACKAGE = FlagDescTestClass.class.getPackage().getName();

  @Before
  public void setup() {
    if (Flags.getFlags() != null) {
      Flags.getFlags().clear();
    }
  }

  @Test
  public void defaultsTest() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(Flags.parse(new String[0], Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", "default value", FlagDescTestClass.getStringFlagValue());
    assertEquals("flag should have value", true, FlagDescTestClass.booleanFlag.get());
    assertEquals("flag should have value", new Integer(123), FlagDescTestClass.integerFlag.get());
    assertEquals("flag should have value", new Long(123456789L), FlagDescTestClass.longFlag.get());
    assertEquals("flag should have value", new Double(1.23), FlagDescTestClass.doubleFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void onlyFlagTest() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(
                new String[] {"--string_flag", "some value"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", "some value", FlagDescTestClass.getStringFlagValue());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void onlyArgsTest() throws Exception {
    List<String> nonFlags =
        Arrays.asList(Flags.parse(new String[] {"just", "args"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals(
        "should inject flag value", "default value", FlagDescTestClass.getStringFlagValue());
    assertArrayEquals(
        "Arguments should be: just, args",
        nonFlags.toArray(new String[0]),
        new String[] {"just", "args"});
  }

  @Test
  public void flagsThenArgsTest() throws Exception {
    List<String> nonFlags =
        Arrays.asList(
            Flags.parse(
                new String[] {"--string_flag", "some value", "just", "args"},
                Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("should inject flag value", "some value", FlagDescTestClass.getStringFlagValue());
    assertArrayEquals(
        "Arguments should be: just, args",
        nonFlags.toArray(new String[0]),
        new String[] {"just", "args"});
  }

  @Test
  public void firstArgsThenFlagsTest() throws Exception {
    List<String> nonFlags =
        Arrays.asList(
            Flags.parse(
                new String[] {"just", "args", "--string_flag", "some value"},
                Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("should inject flag value", "some value", FlagDescTestClass.getStringFlagValue());
    assertArrayEquals(
        "Arguments should be: just, args",
        nonFlags.toArray(new String[0]),
        new String[] {"just", "args"});
  }

  @Test
  public void printUsageTest() throws Exception {
    PrintStream stdout = System.out;
    ByteArrayOutputStream catchStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(catchStream));

    Flags.parse(new String[0], Arrays.asList(TESTFLAGS_PACKAGE));
    Flags.printUsage();
    String result = new String(catchStream.toByteArray());

    System.setOut(stdout);
    System.out.print(result);

    assertTrue("should print flag name", result.contains("string_flag"));
    assertTrue("should print flag description", result.contains("A flag description"));
  }

  @Test
  public void testBooleanFlagSetToFalse() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(
                new String[] {"--boolean_flag", "false"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have false value", false, FlagDescTestClass.booleanFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testBooleanFlagSetToTrue() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--boolean_flag", "true"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have true value", true, FlagDescTestClass.booleanFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testBooleanFlagSetToFalseUsingNoX() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--noboolean_flag"}, Arrays.asList(TESTFLAGS_PACKAGE)));
    assertEquals("flag should have false value", false, FlagDescTestClass.booleanFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testBooleanFlagSetToFalseUsingX() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--boolean_flag"}, Arrays.asList(TESTFLAGS_PACKAGE)));
    assertEquals("flag should have true value", true, FlagDescTestClass.booleanFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testStringFlag() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--string_flag", "abcd"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", "abcd", FlagDescTestClass.getStringFlagValue());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testIntegerFlag() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--integer_flag", "1234"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", new Integer(1234), FlagDescTestClass.integerFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testLongFlag() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(
                new String[] {"--long_flag", "987654321"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", new Long(987654321L), FlagDescTestClass.longFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }

  @Test
  public void testDoubleFlag() throws Exception {
    List<String> leftOverArgs =
        Arrays.asList(
            Flags.parse(new String[] {"--double_flag", "9.87"}, Arrays.asList(TESTFLAGS_PACKAGE)));

    assertEquals("flag should have value", new Double(9.87), FlagDescTestClass.doubleFlag.get());
    assertEquals(leftOverArgs.size(), 0);
  }
}

