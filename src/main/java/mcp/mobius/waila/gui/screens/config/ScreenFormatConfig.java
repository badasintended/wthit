package mcp.mobius.waila.gui.screens.config;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.FormattingConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class ScreenFormatConfig extends GuiScreen {

    public static final String MOD_NAME_FORMAT = "\u00A79\u00A7o%s";
    public static final String BLOCK_FORMAT = "\u00a7r\u00a7f%s";
    public static final String FLUID_FORMAT = "\u00a7r\u00a7f%s";
    public static final String ENTITY_FORMAT = "\u00a7r\u00a7f%s";
    public static final String META_FORMAT = "\u00a77[%s@%d]";

    private final GuiScreen parent;

    private GuiTextField nameFormat;
    private GuiTextField blockFormat;
    private GuiTextField fluidFormat;
    private GuiTextField entityFormat;
    private GuiTextField metaFormat;

    public ScreenFormatConfig(GuiScreen parent) {
        super();

        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        addButton(new GuiButton(0, width / 2 - 125, height - 50, 80, 20, I18n.translateToLocal("screen.button.ok")));
        addButton(new GuiButton(1, width / 2 - 40, height - 50, 80, 20, I18n.translateToLocal("screen.button.cancel")));
        addButton(new GuiButton(2, width / 2 + 45, height - 50, 80, 20, I18n.translateToLocal("screen.button.default")));

        nameFormat = new GuiTextField(3, fontRendererObj, width / 4, 20, 150, 16);
        nameFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.modNameFormat));
        blockFormat = new GuiTextField(3, fontRendererObj, width / 4, 40, 150, 16);
        blockFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.blockFormat));
        fluidFormat = new GuiTextField(3, fontRendererObj, width / 4, 60, 150, 16);
        fluidFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.fluidFormat));
        entityFormat = new GuiTextField(3, fontRendererObj, width / 4, 80, 150, 16);
        entityFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.entityFormat));
        metaFormat = new GuiTextField(3, fontRendererObj, width / 4, 100, 150, 16);
        metaFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.metaFormat));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawString(fontRendererObj, I18n.translateToLocal("choice.format.modname"), nameFormat.xPosition + nameFormat.width + 5, nameFormat.yPosition + 5, 0xFFFFFF);
        nameFormat.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.blockname"), blockFormat.xPosition + blockFormat.width + 5, blockFormat.yPosition + 5, 0xFFFFFF);
        blockFormat.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.fluidname"), fluidFormat.xPosition + fluidFormat.width + 5, fluidFormat.yPosition + 5, 0xFFFFFF);
        fluidFormat.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.entityname"), entityFormat.xPosition + entityFormat.width + 5, entityFormat.yPosition + 5, 0xFFFFFF);
        entityFormat.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.metadata"), metaFormat.xPosition + metaFormat.width + 5, metaFormat.yPosition + 5, 0xFFFFFF);
        metaFormat.drawTextBox();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE)
            Minecraft.getMinecraft().displayGuiScreen(parent);

        nameFormat.textboxKeyTyped(typedChar, keyCode);
        blockFormat.textboxKeyTyped(typedChar, keyCode);
        fluidFormat.textboxKeyTyped(typedChar, keyCode);
        entityFormat.textboxKeyTyped(typedChar, keyCode);
        metaFormat.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        nameFormat.mouseClicked(mouseX, mouseY, mouseButton);
        blockFormat.mouseClicked(mouseX, mouseY, mouseButton);
        fluidFormat.mouseClicked(mouseX, mouseY, mouseButton);
        entityFormat.mouseClicked(mouseX, mouseY, mouseButton);
        metaFormat.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                Minecraft.getMinecraft().displayGuiScreen(parent);
                updateConfig();
            }
            case 1: {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
            case 2: {
                nameFormat.setText(StringEscapeUtils.escapeJava(MOD_NAME_FORMAT));
                blockFormat.setText(StringEscapeUtils.escapeJava(BLOCK_FORMAT));
                fluidFormat.setText(StringEscapeUtils.escapeJava(FLUID_FORMAT));
                entityFormat.setText(StringEscapeUtils.escapeJava(ENTITY_FORMAT));
                metaFormat.setText(StringEscapeUtils.escapeJava(META_FORMAT));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateConfig() {
        ConfigHandler config = ConfigHandler.instance();

        if (nameFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODNAMEWRAPPER, nameFormat.getText());
            FormattingConfig.modNameFormat = StringEscapeUtils.unescapeJava(nameFormat.getText());
        }

        if (blockFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BLOCKNAMEWRAPPER, blockFormat.getText());
            FormattingConfig.blockFormat = StringEscapeUtils.unescapeJava(blockFormat.getText());
        }

        if (fluidFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FLUIDNAMEWRAPPER, fluidFormat.getText());
            FormattingConfig.fluidFormat = StringEscapeUtils.unescapeJava(fluidFormat.getText());
        }

        if (entityFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ENTITYNAMEWRAPPER, entityFormat.getText());
            FormattingConfig.entityFormat = StringEscapeUtils.unescapeJava(entityFormat.getText());
        }

        if (metaFormat.getText().contains("%s") && metaFormat.getText().contains("%d")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATAWRAPPER, metaFormat.getText());
            FormattingConfig.metaFormat = StringEscapeUtils.unescapeJava(metaFormat.getText());
        }
    }
}
