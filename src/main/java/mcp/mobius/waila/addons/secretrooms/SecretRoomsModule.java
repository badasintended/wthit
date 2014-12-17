package mcp.mobius.waila.addons.secretrooms;

public class SecretRoomsModule {

	public static Class  TileEntityCamo = null;
	public static Class  BlockTorchLever = null;
	public static Class  BlockCamoTrapDoor = null;
	
	public static void register(){
		/*
		try{
			Class SecretRooms = Class.forName("com.github.AbrarSyed.secretroomsmod.common.SecretRooms");
			Waila.log.log(Level.INFO, "SecretRooms mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[SecretRooms] SecretRooms mod not found.");
			return;
		}			
		
		try{
			TileEntityCamo    = Class.forName("com.github.AbrarSyed.secretroomsmod.blocks.TileEntityCamo");
			BlockTorchLever   = Class.forName("com.github.AbrarSyed.secretroomsmod.blocks.BlockTorchLever");
			BlockCamoTrapDoor = Class.forName("com.github.AbrarSyed.secretroomsmod.blocks.BlockCamoTrapDoor");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[SecretRooms] Class not found. " + e);
			return;
		} catch (Exception e) {
			Waila.log.log(Level.WARNING, "[SecretRooms] Error while loading module. " + e);
			return;
		}			

		ModuleRegistrar.instance().addConfig("SecretRooms", "secretrooms.hide");
		ModuleRegistrar.instance().registerStackProvider(new HUDTileEntityCamo(), TileEntityCamo);
		ModuleRegistrar.instance().registerStackProvider(new HUDTileEntityCamo(), BlockTorchLever);
		ModuleRegistrar.instance().registerStackProvider(new HUDTileEntityCamo(), BlockCamoTrapDoor);
		ModuleRegistrar.instance().registerSyncedNBTKey("copyID",   TileEntityCamo);
		ModuleRegistrar.instance().registerSyncedNBTKey("copyMeta", TileEntityCamo);
		*/
	}		
	
}
