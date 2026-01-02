package mcp.mobius.waila.mixin;

import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccess {

    @Accessor("key")
    InputConstants.Key wthit_key();

    @Accessor("ALL")
    static Map<String, KeyMapping> wthit_all() {
        throw new AssertionError();
    }

    @Accessor("CATEGORY_SORT_ORDER")
    static Map<String, Integer> wthit_categorySortOrder() {
        throw new AssertionError();
    }

}
