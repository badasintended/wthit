package mcp.mobius.waila.handlers.nei;
/*
import codechicken.nei.NEIClientConfig;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.gui.screens.info.ScreenEnchants;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

import java.util.Locale;
import java.util.Map;

public class HandlerEnchants implements IContainerInputHandler {

    @Override
    public boolean keyTyped(GuiContainer guiContainer, char keyChar, int keyCode) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char keyChar, int keyID) {
    }

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char keyChar, int keyID) {
        GuiContainerManager.getManager();
        ItemStack stackMouseOver = GuiContainerManager.getStackMouseOver(guiContainer);
        if (stackMouseOver == null) {
            return false;
        }

        if (keyID == NEIClientConfig.getKeyBinding(Constants.BIND_SCREEN_ENCH)) {
            int itemEnchantability = stackMouseOver.getItem().getItemEnchantability();
            if (itemEnchantability == 0) {
                return false;
            }

            Minecraft mc = Minecraft.getMinecraft();
            ScreenEnchants screen = new ScreenEnchants(mc.currentScreen);
            screen.setStack(stackMouseOver);
            screen.setName(stackMouseOver.getDisplayName());
            screen.setEnchantability(String.valueOf(itemEnchantability));

            Enchantment[] enchants;
            if (stackMouseOver.getItem() == Items.book)
                enchants = Enchantment.enchantmentsBookList;
            else
                enchants = Enchantment.enchantmentsBookList;

            for (Enchantment enchant : enchants) {
                boolean isCompatible = true;
                int level = 0;
                boolean isApplied = false;

                if (enchant == null) {
                    continue;
                }
                if (enchant.canApplyAtEnchantingTable(stackMouseOver) || stackMouseOver.getItem() == Items.book) {

                    if (stackMouseOver.isItemEnchanted()) {
                        Map stackEnchants = EnchantmentHelper.getEnchantments(stackMouseOver);
                        for (Object id : stackEnchants.keySet()) {
                            if (!enchant.canApplyTogether(Enchantment.getEnchantmentById((Integer) id)))
                                isCompatible = false;
                            if ((Integer) id == enchant.effectId) {
                                isApplied = true;
                                level = (Integer) stackEnchants.get(id);
                            }
                        }
                    }
                    for (int lvl = enchant.getMinLevel(); lvl <= enchant.getMaxLevel(); lvl++) {
                        int minEnchantEnchantability = enchant.getMinEnchantability(lvl);
                        int maxEnchantEnchantability = enchant.getMaxEnchantability(lvl);

                        int minItemEnchantability = 1;
                        int meanItemEnchantability = 1 + itemEnchantability / 4;
                        int maxItemEnchantability = 1 + itemEnchantability / 2;

                        int minModifiedEnchantability = (int) (0.85 * minItemEnchantability + 0.5);
                        int meanModifiedEnchantability = (int) (1.00 * meanItemEnchantability + 0.5);
                        int maxModifiedEnchantability = (int) (1.15 * maxItemEnchantability + 0.5);

                        int minLevel = (int) ((minEnchantEnchantability - minModifiedEnchantability) / 1.15);
                        int maxLevel = (int) ((maxEnchantEnchantability - maxModifiedEnchantability) / 0.85);

                        int meanMinLevel = (int) ((minEnchantEnchantability - meanModifiedEnchantability) / 1.0);
                        int meanMaxLevel = (int) ((maxEnchantEnchantability - meanModifiedEnchantability) / 1.0);

                        String colorcode = isCompatible ? "\u00a7f" : "\u00a7c";

                        if (isApplied && lvl == level)
                            colorcode = "\u00a7e";

                        String enchantModName = getEnchantModName(enchant);
                        if (enchantModName == "Forge") {
                            enchantModName = "Minecraft";
                        }

                        screen.addRow(colorcode + enchant.getTranslatedName(lvl),
                                colorcode + String.valueOf(minLevel),
                                colorcode + String.valueOf(maxLevel),
                                colorcode + String.valueOf(enchant.getWeight()),
                                "\u00a79\u00a7o" + enchantModName);
                    }
                }
            }
            mc.displayGuiScreen(screen);
        }
        return false;
    }

    public String getEnchantModName(Enchantment enchant) {
        String enchantPath = enchant.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        String enchantModName = "<Unknown>";
        for (String s : ModIdentification.modSource_ID.keySet())
            if (enchantPath.toLowerCase(Locale.US).contains(s.toLowerCase(Locale.US))) {
                enchantModName = ModIdentification.modSource_ID.get(s);
            }
        return enchantModName;
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int mouseX, int mouseY, int button) { return false; }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int mouseX, int mouseY, int button) {}

    @Override
    public void onMouseUp(GuiContainer guiContainer, int mouseX, int mouseY, int button) {}

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int mouseX, int mouseY, int scrolled) { return false; }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int mouseX, int mouseY, int scrolled) {}

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int mouseX, int mouseY, int button, long heldTime) {}
}
*/