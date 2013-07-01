package mcp.mobius.waila.handlers;

import java.lang.reflect.Method;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.gui.GuiEnchantScreenaa;
import mcp.mobius.waila.gui.widget.ContainerTable;
import mcp.mobius.waila.gui.widget.Label;
import mcp.mobius.waila.gui.widget.StackDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class EnchantmentHandler implements IContainerInputHandler {

    static Class EplusApi;
    static Method Tooltip;	
	
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
			//try{
				int itemEnchantability = stackover.getItem().getItemEnchantability();	
				if (itemEnchantability == 0){return false;}

				Minecraft mc = Minecraft.getMinecraft();
				GuiEnchantScreenaa enchantScreen = new GuiEnchantScreenaa(mc.currentScreen);
				enchantScreen.widStackDisplay.setStack(stackover);
				enchantScreen.widStackName.setLabel("\u00a7f" + stackover.getDisplayName());
				enchantScreen.widStackEnchantability.setLabel(String.format("\u00a7fEnchantability : %s", itemEnchantability));
				
				Enchantment[] enchants = null;
				if (stackover.getItem() == Item.book)
					enchants = Enchantment.field_92090_c;
				else
					enchants = Enchantment.enchantmentsList;
					
				for(Enchantment enchant : enchants){
					if (enchant == null){continue;}
					if (enchant.canApplyAtEnchantingTable(stackover) || stackover.getItem() == Item.book){
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
							
							enchantScreen.widTable.addRow(new Label(enchant.getTranslatedName(lvl)),
													      new Label(String.valueOf(minLevel)),
												          new Label(String.valueOf(maxLevel)),
													      //new Label(String.valueOf(meanMinLevel)),
												          //new Label(String.valueOf(meanMaxLevel)),
												          new Label("\u00a79\u00a7o"+ this.getEnchantModName(enchant))
													  );
						
                            //String description = this.getEnchantmentToolTip(enchant);
						}
					}
				}
				mc.displayGuiScreen(enchantScreen);				
			//}
			//catch (NullPointerException e){
			//	System.out.printf("%s\n", e);
			//}
			
			
		}
		return false;
	}

	public String getEnchantModName(Enchantment enchant){
		String enchantPath = enchant.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		String enchantModName = "<Unknown>";
		for (String s: mod_Waila.instance.modSourceList.keySet())
			if (enchantPath.contains(s))
				enchantModName = mod_Waila.instance.modSourceList.get(s);
		
		if (enchantModName.equals("Minecraft Coder Pack"))
			enchantModName = "Minecraft";
		return enchantModName;
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

    private String getEnchantmentToolTip(Enchantment enchantment) {
        try {

            if (EplusApi == null) {
                EplusApi = Class.forName("eplus.api.EplusApi");
            }

            if (Tooltip == null) {
                Tooltip = EplusApi.getMethod("getEnchantmentToolTip", Enchantment.class);
            }

            return String.valueOf(Tooltip.invoke(null, enchantment));

        } catch (Exception e) {
            return "";
        }
    }	
}
