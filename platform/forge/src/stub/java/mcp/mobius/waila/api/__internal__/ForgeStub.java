package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.WailaConstants;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(ForgeStub.MOD_ID)
@Mod.EventBusSubscriber(modid = ForgeStub.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeStub {

    static final String MOD_ID = WailaConstants.MOD_ID + "_api";

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        throw new ApiJarInRuntimeException();
    }

}
