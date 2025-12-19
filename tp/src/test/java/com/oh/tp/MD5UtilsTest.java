package com.oh.tp;

import com.oh.tp.utils.MD5Utils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5UtilsTest {
    @Test
    public void testMD5() {
        String input = "7Cd@06D*";
        String expected = "4244f02c3cd25d3e7aaff1af1ec3aae4";
        assertEquals(expected, MD5Utils.md5(input));
    }

    public static void main(String[] args) {

    }
}