package mcp.mobius.waila.utils.forge;

import java.util.Optional;

import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.fml.ModList;

public class ModIdentificationImpl extends ModIdentification {

    public static Optional<Info> getModInfoInner(String namespace) {
        return ModList.get().getMods().stream()
            .filter(m -> m.getModId().equals(namespace))
            .findFirst()
            .map(data -> new Info(data.getModId(), data.getDisplayName()));
    }

}
