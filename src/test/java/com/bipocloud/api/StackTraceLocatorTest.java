package com.bipocloud.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class StackTraceLocatorTest {
    @Test
    void findsRootCauseInCausedBy() {
        String stack = "java.lang.RuntimeException: outer\n" +
                "\tat org.other.Lib.run(Lib.java:1)\n" +
                "Caused by: java.lang.IllegalStateException: oops\n" +
                "\tat org.other.Lib.go(Lib.java:10)\n" +
                "\tat com.bipo.Foo.bar(Foo.java:42)\n" +
                "\tat com.bipo.App.run(App.java:50)\n" +
                "\tat com.bipo.App.main(App.java:60)\n";
        StackTraceRootCause cause = new StackTraceLocator().findRootCause(stack);
        assertNotNull(cause);
        assertEquals("java.lang.IllegalStateException", cause.getType());
        StackTraceElement origin = cause.getOrigin();
        assertNotNull(origin);
        assertEquals("com.bipo.Foo", origin.getClassName());
        assertEquals("bar", origin.getMethodName());
        assertEquals("Foo.java", origin.getFileName());
        assertEquals(42, origin.getLineNumber());
        assertEquals(3, cause.getCalls().size());
        assertEquals("com.bipo.App", cause.getCalls().get(1).getClassName());
    }

    @Test
    void handlesNoCausedBy() {
        String stack = "java.lang.RuntimeException: oops\n" +
                "\tat org.other.Lib.run(Lib.java:1)\n" +
                "\tat com.bipo.App.main(App.java:10)\n";
        StackTraceRootCause cause = new StackTraceLocator().findRootCause(stack);
        assertNotNull(cause);
        assertEquals("java.lang.RuntimeException", cause.getType());
        StackTraceElement origin = cause.getOrigin();
        assertNotNull(origin);
        assertEquals("com.bipo.App", origin.getClassName());
        assertEquals("main", origin.getMethodName());
        assertEquals(1, cause.getCalls().size());
    }

    @Test
    void returnsTypeWhenNoBipoFrame() {
        String stack = "java.lang.RuntimeException: outer\n" +
                "\tat org.other.Lib.run(Lib.java:1)\n" +
                "Caused by: java.lang.IllegalStateException: oops\n" +
                "\tat org.other.Lib.go(Lib.java:10)\n";
        StackTraceRootCause cause = new StackTraceLocator().findRootCause(stack);
        assertNotNull(cause);
        assertEquals("java.lang.IllegalStateException", cause.getType());
        assertNull(cause.getOrigin());
        assertEquals(0, cause.getCalls().size());
    }
}
