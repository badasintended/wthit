package mcp.mobius.waila.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccess {

    @Accessor("destroyProgress")
    float wthit_destroyProgress();

    @Accessor("destroyDelay")
    int wthit_destroyDelay();

}
