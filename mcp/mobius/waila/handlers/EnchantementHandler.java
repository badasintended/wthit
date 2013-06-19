package mcp.mobius.waila.handlers;

import mcp.mobius.waila.addons.enchantingplus.EplusModule;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class EnchantementHandler implements IContainerInputHandler {

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		ItemStack stackover = gui.manager.getStackMouseOver();
		if(stackover == null)
			return false;		
		
		if(keyID == NEIClientConfig.getKeyBinding("showenchant")){
			try{
				System.out.printf("==== ENCHANTS ====\n");
				
				int itemEnchantability = stackover.getItem().getItemEnchantability();	
				if (itemEnchantability == 0){return false;}
				
				for(Enchantment enchant : Enchantment.enchantmentsList){
					if (enchant == null){continue;}
					if (enchant.canApplyAtEnchantingTable(stackover)){
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
							
							System.out.printf("%s [%s / %s / %s / %s]\n", enchant.getTranslatedName(lvl), minLevel, maxLevel, meanMinLevel, meanMaxLevel);

                            String description = EplusModule.getEnchantmentToolTip(enchant);
						}
					}
				}
			}				
			catch (NullPointerException e){
				System.out.printf("%s\n", e);
			}
		}
		return false;
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey,
			int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey,
			int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey,
			int scrolled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey,
			int scrolled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey,
			int button, long heldTime) {
		// TODO Auto-generated method stub
		
	}

}
