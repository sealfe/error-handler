package com.bipocloud.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StackTraceLocatorTest {
    @Test
    void locatesBusinessFrame() {
        String stack = "java.lang.RuntimeException: oops\n" +
                "\tat org.other.Lib.run(Lib.java:1)\n" +
                "\tat com.bipo.Foo.bar(Foo.java:42)\n" +
                "\tat com.bipo.App.main(App.java:10)";
        StackTraceElement element = new StackTraceLocator().locateBusinessFrame(stack);
        assertNotNull(element);
        assertEquals("com.bipo.Foo", element.getClassName());
        assertEquals("bar", element.getMethodName());
        assertEquals("Foo.java", element.getFileName());
        assertEquals(42, element.getLineNumber());
    }
}
