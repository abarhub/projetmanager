package org.projetmanager.projetmanager.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrUtilsTest {

    @Test
    void splitString() {
        var res = StrUtils.splitString("echo test1 test2");
        assertIterableEquals(List.of("echo", "test1", "test2"), res);
    }

    @Test
    void splitStringWithQuote() {
        var res = StrUtils.splitString("echo 'test1 test2'");
        assertIterableEquals(List.of("echo", "test1 test2"), res);
    }

    @Test
    void splitStringWithDoubleQuote() {
        var res = StrUtils.splitString("echo \"test1 test2\"");
        assertIterableEquals(List.of("echo", "test1 test2"), res);
    }

    @Test
    void splitStringWithQuoteAndDoubleQuote() {
        var res = StrUtils.splitString("echo \"test1 'test2'\"");
        assertIterableEquals(List.of("echo", "test1 'test2'"), res);
    }
}