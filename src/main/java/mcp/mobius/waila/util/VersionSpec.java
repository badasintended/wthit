package mcp.mobius.waila.util;

public class VersionSpec {

    public static VersionSpec parse(String spec) {
        var split = spec.split("/ /");

        for (var part : split) {

        }
    }

    enum Comparison {
        LESS_THAN, LESS_THAN_OR_EQUAL,
        GREATER_THAN, GREATER_THAN_OR_EQUAL,
        EXACTLY
    }

    enum Combination {
        AND, OR
    }

}
