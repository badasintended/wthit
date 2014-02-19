package mcp.mobius.waila.handlers;

import java.util.List;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerEntities implements IWailaEntityProvider {

	public static int nhearts = 20;
	
	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		try{
			currenttip.add(WHITE + entity.getEntityName());
		} catch (Exception e){
			currenttip.add(WHITE + "Unknown");
		}		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if (entity instanceof EntityLivingBase){
			String hptip = "";
			
			nhearts = nhearts <= 0 ? 20 : nhearts;
			
			float  health = ((EntityLivingBase)entity).getHealth() / 2.0f;
			float  maxhp  = ((EntityLivingBase)entity).getMaxHealth() / 2.0f;;
			
			if (maxhp > 40.0f)
				currenttip.add(String.format("HP : " + WHITE + "%.0f" + GRAY + " / " + WHITE + "%.0f", health, maxhp));
			
			else{
				for (int i = 0; i < MathHelper.floor_float(health); i++){
					hptip += HEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}
				}
				
				if (((EntityLivingBase)entity).getHealth() > MathHelper.floor_float(health) * 2.0f){
					hptip += HHEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}				
				}
	
				for (int i = 0; i < MathHelper.floor_float(maxhp - health); i++){
					hptip += EHEART;
					if(hptip.length() % (nhearts * HEART.length()) == 0){
						currenttip.add(hptip);
						hptip = "";
					}				
				}
				
				if (!hptip.equals(""))
					currenttip.add(hptip);
			}
		}		

		return currenttip;	
	}

	@Override
	public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		try{
			currenttip.add(BLUE + ITALIC + getEntityMod(entity));
		} catch (Exception e){
			currenttip.add(BLUE + ITALIC + "Unknown");
		}
		return currenttip;
	}

    private static String getEntityMod(Entity entity){
    	String modName = "";
    	try{
    		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
    		ModContainer modC = er.getContainer();
    		modName = modC.getName();
    	} catch (NullPointerException e){
    		modName = "Minecraft";	
    	}
    	return modName;
    }	
	
}
