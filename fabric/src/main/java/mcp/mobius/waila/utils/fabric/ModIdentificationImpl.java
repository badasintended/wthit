package mcp.mobius.waila.utils.fabric;

import java.util.Optional;

import mcp.mobius.waila.utils.ModIdentification;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModIdentificationImpl extends ModIdentification {

    public static Optional<Info> getModInfoInner(String namespace) {
        return FabricLoader.getInstance().getAllMods().stream()
            .map(ModContainer::getMetadata)
            .filter(m -> m.getId().equals(namespace))
            .findFirst()
            .map(data -> new Info(data.getId(), data.getName()));
    }

}
