package mcp.mobius.waila.mixin;

import mcp.mobius.waila.mixed.IClientMixinService;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin extends ContainerObjectSelectionList<KeyBindsList.Entry> {

    @SuppressWarnings("DataFlowIssue")
    KeyBindsListMixin() {
        super(null, 0, 0, 0, 0, 0);
    }

    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private KeyMapping[] wthit_addKeyMappings(KeyMapping[] keyMappings) {
        return ArrayUtils.addAll(keyMappings, IClientMixinService.INSTANCE.getWrappedBinds().toArray(new KeyMapping[0]));
    }

}
