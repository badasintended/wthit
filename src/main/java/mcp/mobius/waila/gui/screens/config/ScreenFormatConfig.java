package mcp.mobius.waila.gui.screens.config;

import com.google.common.base.Predicate;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.config.ColorConfig;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    private static final Predicate<String> HEX_COLOR = input -> {
        if (input == null)
            return false;

        if (input.length() == 1)
            return input.equalsIgnoreCase("#");

        return input.startsWith("#") && input.substring(1).matches("^[a-zA-Z0-9]*$") && input.length() < 8;
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

        buttonList.add(new GuiButton(0, width / 2 - 125, height - 25, 80, 20, I18n.format("screen.button.ok")));
        buttonList.add(new GuiButton(1, width / 2 - 40, height - 25, 80, 20, I18n.format("screen.button.cancel")));
        buttonList.add(new GuiButton(2, width / 2 + 45, height - 25, 80, 20, I18n.format("screen.button.default")));
        int textFieldOffset = 0;
        if (!ColorConfig.ACTIVE_CONFIGS.isEmpty()) {
            buttonList.add(new ButtonCycleTheme(this, 3, width / 2 - 75, height - 48, 150, 20, ColorConfig.ACTIVE_CONFIGS));
            textFieldOffset = -10;
        }

        nameFormat = new GuiTextField(3, fontRenderer, width / 4, 20 + textFieldOffset, 150, 16);
        nameFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.modNameFormat));
        blockFormat = new GuiTextField(4, fontRenderer, width / 4, 40 + textFieldOffset, 150, 16);
        blockFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.blockFormat));
        fluidFormat = new GuiTextField(5, fontRenderer, width / 4, 60 + textFieldOffset, 150, 16);
        fluidFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.fluidFormat));
        entityFormat = new GuiTextField(6, fontRenderer, width / 4, 80 + textFieldOffset, 150, 16);
        entityFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.entityFormat));
        metaFormat = new GuiTextField(7, fontRenderer, width / 4, 100 + textFieldOffset, 150, 16);
        metaFormat.setText(StringEscapeUtils.escapeJava(FormattingConfig.metaFormat));
        backgroundColor = new GuiTextField(8, fontRenderer, width / 4, 120 + textFieldOffset, 150, 16);
        backgroundColor.setText(OverlayConfig.toHex(new Color(OverlayConfig.bgcolor)));
        backgroundColor.setValidator(HEX_COLOR);
        gradientTop = new GuiTextField(9, fontRenderer, width / 4, 140 + textFieldOffset, 150, 16);
        gradientTop.setText(OverlayConfig.toHex(new Color(OverlayConfig.gradient1)));
        gradientTop.setValidator(HEX_COLOR);
        gradientBottom = new GuiTextField(9, fontRenderer, width / 4, 160 + textFieldOffset, 150, 16);
        gradientBottom.setText(OverlayConfig.toHex(new Color(OverlayConfig.gradient2)));
        gradientBottom.setValidator(HEX_COLOR);
        textColor = new GuiTextField(9, fontRenderer, width / 4, 180 + textFieldOffset, 150, 16);
        textColor.setText(OverlayConfig.toHex(new Color(OverlayConfig.fontcolor)));
        textColor.setValidator(HEX_COLOR);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawString(fontRenderer, I18n.format("choice.format.modname"), nameFormat.x + nameFormat.width + 5, nameFormat.y + 5, 0xFFFFFF);
        nameFormat.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.blockname"), blockFormat.x + blockFormat.width + 5, blockFormat.y + 5, 0xFFFFFF);
        blockFormat.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.fluidname"), fluidFormat.x + fluidFormat.width + 5, fluidFormat.y + 5, 0xFFFFFF);
        fluidFormat.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.entityname"), entityFormat.x + entityFormat.width + 5, entityFormat.y + 5, 0xFFFFFF);
        entityFormat.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.metadata"), metaFormat.x + metaFormat.width + 5, metaFormat.y + 5, 0xFFFFFF);
        metaFormat.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.background"), backgroundColor.x + backgroundColor.width + 5, backgroundColor.y + 5, 0xFFFFFF);
        backgroundColor.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.gradienttop"), gradientTop.x + gradientTop.width + 5, gradientTop.y + 5, 0xFFFFFF);
        gradientTop.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.gradientbottom"), gradientBottom.x + gradientBottom.width + 5, gradientBottom.y + 5, 0xFFFFFF);
        gradientBottom.drawTextBox();
        drawString(fontRenderer, I18n.format("choice.format.font"), textColor.x + textColor.width + 5, textColor.y + 5, 0xFFFFFF);
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
                backgroundColor.setText(OverlayConfig.toHex(new Color(BACKGROUND_COLOR)));
                gradientTop.setText(OverlayConfig.toHex(new Color(GRADIENT_TOP_COLOR)));
                gradientBottom.setText(OverlayConfig.toHex(new Color(GRADIENT_BOTTOM_COLOR)));
                textColor.setText(OverlayConfig.toHex(new Color(FONT_COLOR)));
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

        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR, OverlayConfig.fromHex(backgroundColor.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1, OverlayConfig.fromHex(gradientTop.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2, OverlayConfig.fromHex(gradientBottom.getText()).getRGB());
        config.setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR, OverlayConfig.fromHex(textColor.getText()).getRGB());
        OverlayConfig.updateColors();
    }

    public static class ButtonCycleTheme extends GuiButton {

        private static int cachedIndex = 0;

        private final ScreenFormatConfig parent;
        private List<ColorConfig> configs;
        private int index;

        public ButtonCycleTheme(ScreenFormatConfig parent, int buttonId, int x, int y, int widthIn, int heightIn, List<ColorConfig> colorConfigs) {
            super(buttonId, x, y, widthIn, heightIn, "");

            this.configs = colorConfigs;
            this.parent = parent;
            this.index = cachedIndex;
        }

        public ButtonCycleTheme(ScreenFormatConfig parent, int buttonId, int x, int y, List<ColorConfig> colorConfigs) {
            this(parent, buttonId, x, y, 200, 20, colorConfigs);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            String toDraw = configs.get(index).getName();
            if (net.minecraft.client.resources.I18n.hasKey(toDraw))
                toDraw = net.minecraft.client.resources.I18n.format(toDraw);

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                toDraw = TextFormatting.GREEN + net.minecraft.client.resources.I18n.format("screen.button.exporttheme");

            displayString = toDraw;
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    ColorConfig config = new ColorConfig(
                            RandomStringUtils.randomAlphanumeric(10),
                            OverlayConfig.fromHex(parent.backgroundColor.getText()),
                            OverlayConfig.fromHex(parent.gradientTop.getText()),
                            OverlayConfig.fromHex(parent.gradientBottom.getText()),
                            OverlayConfig.fromHex(parent.textColor.getText())
                    );
                    String json = OverlayConfig.GSON.toJson(config);
                    try {
                        FileWriter fileWriter = new FileWriter(new File(Waila.themeDir, config.getName() + ".json"));
                        fileWriter.write(json);
                        fileWriter.close();
                    } catch (Exception e) {
                        return false;
                    }
                    return true;
                }

                index++;
                if (index >= configs.size())
                    index = 0;

                cachedIndex = index;

                configs.get(index).apply(parent.backgroundColor, parent.gradientTop, parent.gradientBottom, parent.textColor);
                return true;
            }

            return false;
        }
    }
}
