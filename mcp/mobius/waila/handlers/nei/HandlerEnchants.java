package mcp.mobius.waila.handlers.nei;

import java.util.Map;

import mcp.mobius.waila.gui.screens.info.ScreenEnchants;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class HandlerEnchants implements IContainerInputHandler {

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) { return false; }

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) { }

	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		ItemStack stackover = gui.manager.getStackMouseOver();
		if(stackover == null)
			return false;
		
		if(keyID == NEIClientConfig.getKeyBinding(Constants.BIND_SCREEN_ENCH)){
			//try{
				int itemEnchantability = stackover.getItem().getItemEnchantability();	
				if (itemEnchantability == 0){return false;}
				
				Minecraft mc = Minecraft.getMinecraft();
				ScreenEnchants screen = new ScreenEnchants(mc.currentScreen);
				screen.setStack(stackover);
				screen.setName(stackover.getDisplayName());
				screen.setEnchantability(String.valueOf(itemEnchantability));
				
				Enchantment[] enchants = null;
				if (stackover.getItem() == Items.book)
					enchants = Enchantment.enchantmentsBookList;
				else
					enchants = Enchantment.enchantmentsList;
					
				for(Enchantment enchant : enchants){
					boolean isCompatible = true;
					int     level        = 0;
					boolean isApplied    = false;
					
					if (enchant == null){continue;}
					if (enchant.canApplyAtEnchantingTable(stackover) || stackover.getItem() == Items.book){
						
						if (stackover.isItemEnchanted()){
							Map stackenchants =  EnchantmentHelper.getEnchantments(stackover);
							for (Object id : stackenchants.keySet()){
								if (!enchant.canApplyTogether(Enchantment.enchantmentsList[(Integer)id]))
									isCompatible = false;
								if ((Integer)id == enchant.effectId){
									isApplied = true;
									level     = (Integer)stackenchants.get(id);
								}
							}
						}
						
						for (int lvl = enchant.getMinLevel(); lvl <= enchant.getMaxLevel(); lvl++){
							int minEnchantEnchantability = enchant.getMinEnchantability(lvl);
							int maxEnchantEnchantability = enchant.getMaxEnchantability(lvl);
							
							int minItemEnchantability  = 1;
							int meanItemEnchantability = 1 + itemEnchantability/4;
							int maxItemEnchantability  = 1 + itemEnchantability/2;
							
							int minModifiedEnchantability =  (int)(0.85*minItemEnchantability + 0.5);
							int meanModifiedEnchantability =  (int)(1.00*meanItemEnchantability + 0.5);
							int maxModifiedEnchantability =  (int)(1.15*maxItemEnchantability + 0.5);
							
							int minLevel = (int) ((minEnchantEnchantability - minModifiedEnchantability)/1.15);
							int maxLevel = (int) ((maxEnchantEnchantability - maxModifiedEnchantability)/0.85);
							
							int meanMinLevel = (int) ((minEnchantEnchantability - meanModifiedEnchantability)/1.0);
							int meanMaxLevel = (int) ((maxEnchantEnchantability - meanModifiedEnchantability)/1.0);
							
							String colorcode = isCompatible ? "\u00a7f" : "\u00a7c";
							
							if (isApplied && lvl == level)
								colorcode = "\u00a7e";
							
							screen.addRow(colorcode + enchant.getTranslatedName(lvl),
									colorcode + String.valueOf(minLevel),
									colorcode + String.valueOf(maxLevel),
									colorcode + String.valueOf(enchant.getWeight()),
									"\u00a79\u00a7o"+ ModIdentification.nameFromObject(enchant));
						}
					}
				}
				mc.displayGuiScreen(screen);
		}
		
		return false;
	}

	/*
	public String getEnchantModName(Enchantment enchant){
		String enchantPath = enchant.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		
		//mod_Waila.log.log(Level.INFO, enchantPath);
		
		String enchantModName = "<Unknown>";
		for (String s: mod_Waila.instance.modSourceList.keySet())
			if (enchantPath.contains(s))
				enchantModName = mod_Waila.instance.modSourceList.get(s);
		
		if (enchantModName.equals("Minecraft Coder Pack"))
			enchantModName = "Minecraft";
		return enchantModName;
	}
	*/	
	
	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {	return false; }

	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey, int button) { }

	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) { }

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) { return false; }

	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) { }

	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) { }

}
