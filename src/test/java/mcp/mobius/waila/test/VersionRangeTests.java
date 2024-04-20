package mcp.mobius.waila.test;

import mcp.mobius.waila.mcless.version.VersionRanges;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionRangeTests {

    void test(String version, String range, boolean expected) {
        var ranges = VersionRanges.parse(range);
        Assertions.assertEquals(expected, ranges.match(version));
    }

    @Test
    void test() {
        test("1", "*", true);

        test("1", "<2", true);
        test("1", "< 2", true);

        test("3", "1 || >2", true);
        test("1.0.2", "1.0.1", false);
    }

}
