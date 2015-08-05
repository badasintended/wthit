package mcp.mobius.waila.handlers;

import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaFMPDecorator;
import mcp.mobius.waila.api.impl.DataAccessorFMP;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class DecoratorFMP implements IWailaBlockDecorator {

	@Override
	public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		NBTTagList list = accessor.getNBTData().getTagList("parts", 10);
		for (int i = 0; i < list.tagCount(); i++){
			NBTTagCompound subtag = list.getCompoundTagAt(i);
			String id = subtag.getString("id");

			if (ModuleRegistrar.instance().hasFMPDecorator(id)){
				DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id, accessor.getRenderingPosition(), accessor.getPartialFrame());
				
				for (List<IWailaFMPDecorator> providersList : ModuleRegistrar.instance().getFMPDecorators(id).values())
					for (IWailaFMPDecorator provider : providersList)
						provider.decorateBlock(itemStack, DataAccessorFMP.instance, config);
			}
		}
	}

	public static void register(){
		Class BlockMultipart = null;
		try{
			BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARN, "[FMP] Class not found. " + e);
			return;
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[FMP] Unhandled exception." + e);
			return;			
		}

		ModuleRegistrar.instance().registerDecorator(new DecoratorFMP(), BlockMultipart);		
	}
	
}
