package mcp.mobius.waila.mcless.version;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.IntPredicate;

public class VersionRanges {

    private static final String OPERATORS = "<>=|&";

    private final Deque<Deque<Range>> or;

    public static VersionRanges parse(String spec) {
        if (spec.equals("*")) return new VersionRanges(new ArrayDeque<>());

        var or = new ArrayDeque<Deque<Range>>();
        or.push(new ArrayDeque<>());

        var split = spec.trim().split("\\s+");
        var lastOp = "";

        for (var part : split) {
            var opBuilder = new StringBuilder();

            for (var c : part.toCharArray()) {
                if (OPERATORS.indexOf(c) > -1) opBuilder.append(c);
                else break;
            }

            var op = opBuilder.toString();
            var version = part.substring(op.length());

            if (op.isEmpty()) op = lastOp;
            lastOp = "";

            if (version.isEmpty()) {
                // @formatter:off
                switch (op) {
                    case "|", "||" -> or.push(new ArrayDeque<>());
                    case "&", "&&" -> {}
                }
                // @formatter:on

                lastOp = op;
                continue;
            }

            var and = Objects.requireNonNull(or.peek());

            // @formatter:off
            switch (op) {
                case "<"  -> and.push(new Range(version, v -> v <  0));
                case "<=" -> and.push(new Range(version, v -> v <= 0));
                case ">"  -> and.push(new Range(version, v -> v >  0));
                case ">=" -> and.push(new Range(version, v -> v >= 0));
                case "=", "==", "" -> and.push(new Range(version, v -> v == 0));
                default -> throw new IllegalArgumentException("Unknown operator " + op);
            }
            // @formatter:on
        }

        if (!lastOp.isEmpty()) {
            throw new IllegalArgumentException("Dangling operator " + lastOp);
        }

        return new VersionRanges(or);
    }

    private VersionRanges(Deque<Deque<Range>> or) {
        this.or = or;
    }

    public boolean match(String version) {
        if (or.isEmpty()) return true;

        for (var and : or) {
            var res = true;
            for (var range : and) {
                res = res && range.predicate.test(FlexVerComparator.compare(version, range.version));
            }

            if (res) return true;
        }

        return false;
    }

    record Range(String version, IntPredicate predicate) {

    }

}
