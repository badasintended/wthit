package mcp.mobius.waila.util;

import java.io.IOException;
import java.io.Reader;

@FunctionalInterface
public interface ReaderFor<T> {

    Reader read(T t) throws IOException;

}
