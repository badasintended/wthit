package mcp.mobius.waila.addons.thermaldynamics;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class ThermalDynamicsModule {

    public static Class TileFluidDuct = null;

    public static void register(){
        // XXX : We register the Fluiduct
        try{
            TileFluidDuct              = Class.forName("cofh.thermaldynamics.ducts.fluid.TileFluidDuct");

            ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsFluid");
            ModuleRegistrar.instance().addConfigRemote("Thermal Dynamics", "thermaldynamics.fluidDuctsAmount");
            ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerDuct(), TileFluidDuct);
            ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerDuct(), TileFluidDuct);
            ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerDuct(), TileFluidDuct);

        } catch (Exception e){
            Waila.log.log(Level.WARN, "[Thermal Dynamics] Error while loading FluidDuct hooks." + e);
        }

        // XXX : We register the IBlockInfo interface
		/*
		try{
			IBlockInfo              = Class.forName("cofh.api.block.IBlockInfo");
			IBlockInfo_getBlockInfo = IBlockInfo.getMethod("getBlockInfo", IBlockAccess.class, int.class, int.class, int.class, ForgeDirection.class, EntityPlayer.class, List.class, boolean.class);
			
			
			//ModuleRegistrar.instance().addConfigRemote("Thermal Expansion", "thermalexpansion.energyhandler");			
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIBlockInfo(), IBlockInfo);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIBlockInfo(), IBlockInfo);
			
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thermal Expansion] Error while loading IBlockInfo hooks." + e);
		}			
		*/
    }


}
