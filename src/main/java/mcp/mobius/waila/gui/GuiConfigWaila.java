package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.config.*;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueBoolean;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueCycle;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueEnum;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueInput;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import org.apache.commons.lang3.StringEscapeUtils;

public class GuiConfigWaila extends GuiOptions {

    public GuiConfigWaila(Gui parent) {
        super(parent, new TranslatableTextComponent("gui.waila.configuration", Waila.NAME), () -> WailaConfig.save(Waila.config), () -> Waila.config = WailaConfig.loadConfig());
    }

    @Override
    public OptionsListWidget getOptions() {
        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30, () -> WailaConfig.save(Waila.config));
        options.add(new OptionsEntryButton(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "general")), new ButtonWidget(0, 0, 0, 100, 20, null) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                client.openGui(new GuiOptions(GuiConfigWaila.this, new TranslatableTextComponent(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "general")))) {
                    @Override
                    public OptionsListWidget getOptions() {
                        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "display_tooltip")), Waila.config.getGeneral().shouldDisplayTooltip(), val ->
                                Waila.config.getGeneral().setDisplayTooltip(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "display_fluids")), Waila.config.getGeneral().shouldDisplayFluids(), val ->
                                Waila.config.getGeneral().setDisplayFluids(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "sneaky_details")), Waila.config.getGeneral().shouldShiftForDetails(), val ->
                                Waila.config.getGeneral().setShiftForDetails(val)
                        ));
                        options.add(new OptionsEntryValueEnum<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "display_mode")), WailaConfig.DisplayMode.values(), Waila.config.getGeneral().getDisplayMode(), val ->
                                Waila.config.getGeneral().setDisplayMode(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "hide_from_players")), Waila.config.getGeneral().shouldHideFromPlayerList(), val ->
                                Waila.config.getGeneral().setHideFromPlayerList(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "hide_from_debug")), Waila.config.getGeneral().shouldHideFromDebug(), val ->
                                Waila.config.getGeneral().setHideFromDebug(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "display_item")), Waila.config.getGeneral().shouldShowItem(), val ->
                                Waila.config.getGeneral().setShowItem(val)
                        ));
                        options.add(new OptionsEntryValueBoolean(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "tts")), Waila.config.getGeneral().shouldEnableTextToSpeech(), val ->
                                Waila.config.getGeneral().setEnableTextToSpeech(val)
                        ));
                        return options;
                    }
                });
            }
        }));
        options.add(new OptionsEntryButton(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay")), new ButtonWidget(0, 0, 0, 100, 20, null) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                client.openGui(new GuiOptions(GuiConfigWaila.this, new TranslatableTextComponent(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay")))) {
                    @Override
                    public OptionsListWidget getOptions() {
                        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_pos_x")), Waila.config.getOverlay().getOverlayPosX(), val ->
                                Waila.config.getOverlay().setOverlayPosX(Math.min(1.0F, Math.max(0.0F, val)))
                        , OptionsEntryValueInput.FLOAT));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_pos_y")), Waila.config.getOverlay().getOverlayPosY(), val ->
                                Waila.config.getOverlay().setOverlayPosY(Math.min(1.0F, Math.max(0.0F, val)))
                        , OptionsEntryValueInput.FLOAT));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_scale")), Waila.config.getOverlay().getOverlayScale(), val ->
                                Waila.config.getOverlay().setOverlayScale(Math.min(2.0F, Math.max(0.1F, val)))
                        , OptionsEntryValueInput.FLOAT));
                        options.add(new OptionsEntryButton(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_color")), new ButtonWidget(0, 0, 0, 100, 20, null) {
                            @Override
                            public void onPressed(double mouseX, double mouseY) {
                                client.openGui(new GuiOptions(GuiConfigWaila.this, new TranslatableTextComponent(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_color")))) {
                                    @Override
                                    public OptionsListWidget getOptions() {
                                        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_alpha")), Waila.config.getOverlay().getColor().getRawAlpha(), val ->
                                                Waila.config.getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val)))
                                        , OptionsEntryValueInput.INTEGER));
                                        options.add(new OptionsEntryValueCycle(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay_theme")),
                                                Waila.config.getOverlay().getColor().getThemes().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                                                Waila.config.getOverlay().getColor().getTheme().getId().toString(),
                                                val ->
                                                        Waila.config.getOverlay().getColor().applyTheme(new Identifier(val))
                                                ));
                                        return options;
                                    }
                                });
                            }
                        }));
                        return options;
                    }
                });
            }
        }));
        options.add(new OptionsEntryButton(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "formatting")), new ButtonWidget(0, 0, 0, 100, 20, null) {
            @Override
            public void onPressed(double mouseX, double mouseY) {
                client.openGui(new GuiOptions(GuiConfigWaila.this, new TranslatableTextComponent(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "overlay")))) {
                    @Override
                    public OptionsListWidget getOptions() {
                        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "format_mod_name")), StringEscapeUtils.escapeJava(Waila.config.getFormatting().getModName()), val ->
                                Waila.config.getFormatting().setModName(val.isEmpty() || !val.contains("%s") ? Waila.config.getFormatting().getModName() : val)
                        ));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "format_block_name")), StringEscapeUtils.escapeJava(Waila.config.getFormatting().getBlockName()), val ->
                                Waila.config.getFormatting().setBlockName(val.isEmpty() || !val.contains("%s") ? Waila.config.getFormatting().getBlockName() : val)
                        ));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "format_fluid_name")), StringEscapeUtils.escapeJava(Waila.config.getFormatting().getFluidName()), val ->
                                Waila.config.getFormatting().setFluidName(val.isEmpty() || !val.contains("%s") ? Waila.config.getFormatting().getFluidName() : val)
                        ));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "format_entity_name")), StringEscapeUtils.escapeJava(Waila.config.getFormatting().getEntityName()), val ->
                                Waila.config.getFormatting().setEntityName(val.isEmpty() || !val.contains("%s") ? Waila.config.getFormatting().getEntityName() : val)
                        ));
                        options.add(new OptionsEntryValueInput<>(SystemUtil.createTranslationKey("config", new Identifier(Waila.MODID, "format_registry_name")), StringEscapeUtils.escapeJava(Waila.config.getFormatting().getRegistryName()), val ->
                                Waila.config.getFormatting().setRegistryName(val.isEmpty() || !val.contains("%s") ? Waila.config.getFormatting().getRegistryName() : val)
                        ));
                        return options;
                    }
                });
            }
        }));
        return options;
    }
}
