package mcp.mobius.waila.api.__internal__;

import mcp.mobius.waila.api.WailaConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(NeoStub.MOD_ID)
@EventBusSubscriber(modid = NeoStub.MOD_ID)
public class NeoStub {

    static final String MOD_ID = WailaConstants.MOD_ID + "_api";

    @SubscribeEvent
    static void setup(FMLCommonSetupEvent event) {
        throw new ApiJarInRuntimeException();
    }

}
