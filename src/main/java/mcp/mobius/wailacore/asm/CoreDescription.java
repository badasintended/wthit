package mcp.mobius.wailacore.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions("mcp.mobius.wailacore.asm")
public class CoreDescription implements IFMLLoadingPlugin {

    public static final Logger log = LogManager.getLogger("WailaCore");
    public static File location;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"mcp.mobius.wailacore.asm.CoreTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "mcp.mobius.wailacore.asm.CoreContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("coremodLocation"))
            location = (File) data.get("coremodLocation");
    }

    @Override
    public String getAccessTransformerClass() {
        return "mcp.mobius.wailacore.asm.CoreAccessTransformer";
    }
}
