package mcp.mobius.waila.util;

import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public class ProfilerUtil {

    public static Impl profile(String name) {
        return Impl.INSTANCE.start(name);
    }

    public enum Impl implements AutoCloseable {
        INSTANCE;

        ProfilerFiller profiler;

        private Impl start(String name) {
            profiler = Profiler.get();
            profiler.push(name);
            return this;
        }

        @Override
        public void close() {
            profiler.pop();
        }
    }

}
