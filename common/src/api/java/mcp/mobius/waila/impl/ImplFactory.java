package mcp.mobius.waila.impl;

import mcp.mobius.waila.api.IDrawableText;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IWailaConfig;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class ImplFactory {

    protected static ImplFactory implFactory = null;

    public static ImplFactory getInstance() {
        return implFactory;
    }

    public abstract IDrawableText createDrawableText();

    public abstract <T> IJsonConfig.Builder0<T> createJsonConfigBuilder(Class<T> clazz);

    public abstract IModInfo getModInfo(String namespace);

    public abstract IWailaConfig getConfig();

}
