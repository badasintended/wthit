package mcp.mobius.waila.gui.screens.config;

import com.google.common.base.Predicate;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.FormattingConfig;
import mcp.mobius.waila.overlay.OverlayConfig;
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

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

@SideOnly(Side.CLIENT)
public class ScreenFormatConfig extends GuiScreen {

    public static final String MOD_NAME_FORMAT = "\u00A79\u00A7o%s";
    public static final String BLOCK_FORMAT = "\u00a7r\u00a7f%s";
    public static final String FLUID_FORMAT = "\u00a7r\u00a7f%s";
    public static final String ENTITY_FORMAT = "\u00a7r\u00a7f%s";
    public static final String META_FORMAT = "\u00a77[%s@%d]";

    public static final int BACKGROUND_COLOR = 1048592;
    public static final int GRADIENT_TOP_COLOR = 5243135;
    public static final int GRADIENT_BOTTOM_COLOR = 2621567;
    public static final int FONT_COLOR = 10526880;

    private static final Predicate<String> HEX_COLOR = new Predicate<String>() {
        @Override
        public boolean apply(@Nullable String input) {
            if (input == null)
                return false;

            if (input.length() == 1)
                return input.equalsIgnoreCase("#");

            return input.startsWith("#") && input.length() < 8;
        }
    };

    private final GuiScreen parent;

    private GuiTextField nameFormat;
    private GuiTextField blockFormat;
    private GuiTextField fluidFormat;
    private GuiTextField entityFormat;
    private GuiTextField metaFormat;
    private GuiTextField backgroundColor;
    private GuiTextField gradientTop;
    private GuiTextField gradientBottom;
    private GuiTextField textColor;

    public ScreenFormatConfig(GuiScreen parent) {
        super();

        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        addButton(new GuiButton(0, width / 2 - 125, height - 30, 80, 20, I18n.translateToLocal("screen.button.ok")));
        addButton(new GuiButton(1, width / 2 - 40, height - 30, 80, 20, I18n.translateToLocal("screen.button.cancel")));
        addButton(new GuiButton(2, width / 2 + 45, height - 30, 80, 20, I18n.translateToLocal("screen.button.default")));

        nameFormat = new GuiTextField(3, fontRendererObj, width / 4, 20, 150, 16);
        nameFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.modNameFormat));
        blockFormat = new GuiTextField(4, fontRendererObj, width / 4, 40, 150, 16);
        blockFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.blockFormat));
        fluidFormat = new GuiTextField(5, fontRendererObj, width / 4, 60, 150, 16);
        fluidFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.fluidFormat));
        entityFormat = new GuiTextField(6, fontRendererObj, width / 4, 80, 150, 16);
        entityFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.entityFormat));
        metaFormat = new GuiTextField(7, fontRendererObj, width / 4, 100, 150, 16);
        metaFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.metaFormat));
        backgroundColor = new GuiTextField(8, fontRendererObj, width / 4, 120, 150, 16);
        backgroundColor.setText("#" + Integer.toHexString(new Color(OverlayConfig.bgcolor).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
        backgroundColor.setValidator(HEX_COLOR);
        gradientTop = new GuiTextField(9, fontRendererObj, width / 4, 140, 150, 16);
        gradientTop.setText("#" + Integer.toHexString(new Color(OverlayConfig.gradient1).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
        gradientTop.setValidator(HEX_COLOR);
        gradientBottom = new GuiTextField(9, fontRendererObj, width / 4, 160, 150, 16);
        gradientBottom.setText("#" + Integer.toHexString(new Color(OverlayConfig.gradient2).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
        gradientBottom.setValidator(HEX_COLOR);
        textColor = new GuiTextField(9, fontRendererObj, width / 4, 180, 150, 16);
        textColor.setText("#" + Integer.toHexString(new Color(OverlayConfig.fontcolor).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
        textColor.setValidator(HEX_COLOR);
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
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.background"), backgroundColor.xPosition + backgroundColor.width + 5, backgroundColor.yPosition + 5, 0xFFFFFF);
        backgroundColor.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.gradienttop"), gradientTop.xPosition + gradientTop.width + 5, gradientTop.yPosition + 5, 0xFFFFFF);
        gradientTop.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.gradientbottom"), gradientBottom.xPosition + gradientBottom.width + 5, gradientBottom.yPosition + 5, 0xFFFFFF);
        gradientBottom.drawTextBox();
        drawString(fontRendererObj, I18n.translateToLocal("choice.format.font"), textColor.xPosition + textColor.width + 5, textColor.yPosition + 5, 0xFFFFFF);
        textColor.drawTextBox();
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
        backgroundColor.textboxKeyTyped(typedChar, keyCode);
        gradientTop.textboxKeyTyped(typedChar, keyCode);
        gradientBottom.textboxKeyTyped(typedChar, keyCode);
        textColor.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        nameFormat.mouseClicked(mouseX, mouseY, mouseButton);
        blockFormat.mouseClicked(mouseX, mouseY, mouseButton);
        fluidFormat.mouseClicked(mouseX, mouseY, mouseButton);
        entityFormat.mouseClicked(mouseX, mouseY, mouseButton);
        metaFormat.mouseClicked(mouseX, mouseY, mouseButton);
        backgroundColor.mouseClicked(mouseX, mouseY, mouseButton);
        gradientTop.mouseClicked(mouseX, mouseY, mouseButton);
        gradientBottom.mouseClicked(mouseX, mouseY, mouseButton);
        textColor.mouseClicked(mouseX, mouseY, mouseButton);
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
                backgroundColor.setText("#" + Integer.toHexString(new Color(BACKGROUND_COLOR).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
                gradientTop.setText("#" + Integer.toHexString(new Color(GRADIENT_TOP_COLOR).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
                gradientBottom.setText("#" + Integer.toHexString(new Color(GRADIENT_BOTTOM_COLOR).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
                textColor.setText("#" + Integer.toHexString(new Color(FONT_COLOR).getRGB()).substring(2).toUpperCase(Locale.ENGLISH));
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateConfig() {
        ConfigHandler config = ConfigHandler.instance();

        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODNAMEFORMAT, nameFormat.getText());
        FormattingConfig.modNameFormat = StringEscapeUtils.unescapeJava(nameFormat.getText());

        if (blockFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BLOCKNAMEFORMAT, blockFormat.getText());
            FormattingConfig.blockFormat = StringEscapeUtils.unescapeJava(blockFormat.getText());
        }

        if (fluidFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FLUIDNAMEFORMAT, fluidFormat.getText());
            FormattingConfig.fluidFormat = StringEscapeUtils.unescapeJava(fluidFormat.getText());
        }

        if (entityFormat.getText().contains("%s")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ENTITYNAMEFORMAT, entityFormat.getText());
            FormattingConfig.entityFormat = StringEscapeUtils.unescapeJava(entityFormat.getText());
        }

        if (metaFormat.getText().contains("%s") && metaFormat.getText().contains("%d")) {
            config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATAFORMAT, metaFormat.getText());
            FormattingConfig.metaFormat = StringEscapeUtils.unescapeJava(metaFormat.getText());
        }

        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR, Color.decode(backgroundColor.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1, Color.decode(gradientTop.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2, Color.decode(gradientBottom.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR, Color.decode(textColor.getText()).getRGB());
        OverlayConfig.updateColors();
    }
}
