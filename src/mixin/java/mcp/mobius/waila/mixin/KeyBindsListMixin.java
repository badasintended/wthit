package mcp.mobius.waila.mixin;

import java.util.List;

import mcp.mobius.waila.mixed.IClientMixinService;
import mcp.mobius.waila.mixed.MWrappedKeyBind;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin extends ContainerObjectSelectionList<KeyBindsList.Entry> {

    @SuppressWarnings("DataFlowIssue")
    KeyBindsListMixin() {
        super(null, 0, 0, 0, 0, 0);
    }

    private @Unique List<KeyMapping> wthit_wrappedKeyBinds;

    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private KeyMapping[] wthit_addKeyMappings(KeyMapping[] keyMappings) {
        wthit_wrappedKeyBinds = IClientMixinService.INSTANCE.getWrappedBinds();
        return ArrayUtils.addAll(keyMappings, wthit_wrappedKeyBinds.toArray(new KeyMapping[0]));
    }

    @Inject(method = "resetMappingAndUpdateButtons", at = @At("HEAD"))
    private void wthit_onResetMappingAndUpdateButtons(CallbackInfo ci) {
        for (var bind : wthit_wrappedKeyBinds) {
            ((MWrappedKeyBind) bind).update();
        }
        IClientMixinService.INSTANCE.saveConfig();
    }

}
