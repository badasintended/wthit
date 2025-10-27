package mcp.mobius.waila.mixin;

import java.util.Map;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccess {

    @Accessor("ALL")
    static Map<String, KeyMapping> wthit_all() {
        throw new AssertionError();
    }

}
