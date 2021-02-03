package mcp.mobius.waila.gui;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.config.OptionsEntryButton;
import mcp.mobius.waila.gui.config.OptionsListWidget;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueBoolean;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueCycle;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueEnum;
import mcp.mobius.waila.gui.config.value.OptionsEntryValueInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringEscapeUtils;

public class GuiConfigWaila extends GuiOptions {

    public GuiConfigWaila(Screen parent) {
        super(parent, new TranslatableText("gui.waila.configuration", Waila.NAME), Waila.CONFIG::save, Waila.CONFIG::invalidate);
    }

    @Override
    public OptionsListWidget getOptions() {
        OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30, Waila.CONFIG::save);
        options.add(new OptionsEntryButton("config.waila.general", new ButtonWidget(0, 0, 100, 20, LiteralText.EMPTY, w ->
            client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.general")) {
                @Override
                public OptionsListWidget getOptions() {
                    OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                    options.add(new OptionsEntryValueBoolean("config.waila.display_tooltip", Waila.CONFIG.get().getGeneral().shouldDisplayTooltip(), val ->
                        Waila.CONFIG.get().getGeneral().setDisplayTooltip(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.display_fluids", Waila.CONFIG.get().getGeneral().shouldDisplayFluids(), val ->
                        Waila.CONFIG.get().getGeneral().setDisplayFluids(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.sneaky_details", Waila.CONFIG.get().getGeneral().shouldShiftForDetails(), val ->
                        Waila.CONFIG.get().getGeneral().setShiftForDetails(val)
                    ));
                    options.add(new OptionsEntryValueEnum<>("config.waila.display_mode", WailaConfig.DisplayMode.values(), Waila.CONFIG.get().getGeneral().getDisplayMode(), val ->
                        Waila.CONFIG.get().getGeneral().setDisplayMode(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.hide_from_players", Waila.CONFIG.get().getGeneral().shouldHideFromPlayerList(), val ->
                        Waila.CONFIG.get().getGeneral().setHideFromPlayerList(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.hide_from_debug", Waila.CONFIG.get().getGeneral().shouldHideFromDebug(), val ->
                        Waila.CONFIG.get().getGeneral().setHideFromDebug(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.display_item", Waila.CONFIG.get().getGeneral().shouldShowItem(), val ->
                        Waila.CONFIG.get().getGeneral().setShowItem(val)
                    ));
                    options.add(new OptionsEntryValueBoolean("config.waila.tts", Waila.CONFIG.get().getGeneral().shouldEnableTextToSpeech(), val ->
                        Waila.CONFIG.get().getGeneral().setEnableTextToSpeech(val)
                    ));
                    return options;
                }
            }))));
        options.add(new OptionsEntryButton("config.waila.overlay", new ButtonWidget(0, 0, 100, 20, LiteralText.EMPTY, w ->
            client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay")) {
                @Override
                public OptionsListWidget getOptions() {
                    OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                    options.add(new OptionsEntryButton("config.waila.overlay_pos", new ButtonWidget(0, 0, 100, 20, LiteralText.EMPTY, w ->
                        client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay_pos")) {
                            @Override
                            public OptionsListWidget getOptions() {
                                OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                                options.add(new OptionsEntryValueInput<>("config.waila.overlay_pos_x", Waila.CONFIG.get().getOverlay().getPosition().getX(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setX(val), OptionsEntryValueInput.INTEGER));
                                options.add(new OptionsEntryValueInput<>("config.waila.overlay_pos_y", Waila.CONFIG.get().getOverlay().getPosition().getY(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setY(val), OptionsEntryValueInput.INTEGER));
                                options.add(new OptionsEntryValueEnum<>("config.waila.overlay_anchor_x", WailaConfig.ConfigOverlay.Position.HorizontalAlignment.values(), Waila.CONFIG.get().getOverlay().getPosition().getAnchorX(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setAnchorX(val)));
                                options.add(new OptionsEntryValueEnum<>("config.waila.overlay_anchor_y", WailaConfig.ConfigOverlay.Position.VerticalAlignment.values(), Waila.CONFIG.get().getOverlay().getPosition().getAnchorY(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setAnchorY(val)));
                                options.add(new OptionsEntryValueEnum<>("config.waila.overlay_align_x", WailaConfig.ConfigOverlay.Position.HorizontalAlignment.values(), Waila.CONFIG.get().getOverlay().getPosition().getAlignX(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setAlignX(val)));
                                options.add(new OptionsEntryValueEnum<>("config.waila.overlay_align_y", WailaConfig.ConfigOverlay.Position.VerticalAlignment.values(), Waila.CONFIG.get().getOverlay().getPosition().getAlignY(), val ->
                                    Waila.CONFIG.get().getOverlay().getPosition().setAlignY(val)));
                                return options;
                            }
                        }))));
                    options.add(new OptionsEntryValueInput<>("config.waila.overlay_scale", Waila.CONFIG.get().getOverlay().getScale(), val ->
                        Waila.CONFIG.get().getOverlay().setScale(MathHelper.clamp(val, 0.0F, 1.0F)), OptionsEntryValueInput.FLOAT));
                    options.add(new OptionsEntryButton("config.waila.overlay_color", new ButtonWidget(0, 0, 100, 20, LiteralText.EMPTY, w ->
                        client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay_color")) {
                            @Override
                            public OptionsListWidget getOptions() {
                                OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                                options.add(new OptionsEntryValueInput<>("config.waila.overlay_alpha", Waila.CONFIG.get().getOverlay().getColor().getRawAlpha(), val ->
                                    Waila.CONFIG.get().getOverlay().getColor().setAlpha(Math.min(100, Math.max(0, val)))
                                    , OptionsEntryValueInput.INTEGER));
                                options.add(new OptionsEntryValueCycle("config.waila.overlay_theme",
                                    Waila.CONFIG.get().getOverlay().getColor().getThemes().stream().map(t -> t.getId().toString()).sorted(String::compareToIgnoreCase).toArray(String[]::new),
                                    Waila.CONFIG.get().getOverlay().getColor().getTheme().getId().toString(),
                                    val ->
                                        Waila.CONFIG.get().getOverlay().getColor().applyTheme(new Identifier(val))
                                ));
                                return options;
                            }
                        }))));
                    return options;
                }
            }))));
        options.add(new OptionsEntryButton("config.waila.formatting", new ButtonWidget(0, 0, 100, 20, LiteralText.EMPTY, w ->
            client.openScreen(new GuiOptions(this, new TranslatableText("config.waila.overlay")) {
                @Override
                public OptionsListWidget getOptions() {
                    OptionsListWidget options = new OptionsListWidget(this, client, width + 45, height, 32, height - 32, 30);
                    options.add(new OptionsEntryValueInput<>("config.waila.format_mod_name", StringEscapeUtils.escapeJava(Waila.CONFIG.get().getFormatting().getModName()), val ->
                        Waila.CONFIG.get().getFormatting().setModName(val.isEmpty() || !val.contains("%s") ? Waila.CONFIG.get().getFormatting().getModName() : val)
                    ));
                    options.add(new OptionsEntryValueInput<>("config.waila.format_block_name", StringEscapeUtils.escapeJava(Waila.CONFIG.get().getFormatting().getBlockName()), val ->
                        Waila.CONFIG.get().getFormatting().setBlockName(val.isEmpty() || !val.contains("%s") ? Waila.CONFIG.get().getFormatting().getBlockName() : val)
                    ));
                    options.add(new OptionsEntryValueInput<>("config.waila.format_fluid_name", StringEscapeUtils.escapeJava(Waila.CONFIG.get().getFormatting().getFluidName()), val ->
                        Waila.CONFIG.get().getFormatting().setFluidName(val.isEmpty() || !val.contains("%s") ? Waila.CONFIG.get().getFormatting().getFluidName() : val)
                    ));
                    options.add(new OptionsEntryValueInput<>("config.waila.format_entity_name", StringEscapeUtils.escapeJava(Waila.CONFIG.get().getFormatting().getEntityName()), val ->
                        Waila.CONFIG.get().getFormatting().setEntityName(val.isEmpty() || !val.contains("%s") ? Waila.CONFIG.get().getFormatting().getEntityName() : val)
                    ));
                    options.add(new OptionsEntryValueInput<>("config.waila.format_registry_name", StringEscapeUtils.escapeJava(Waila.CONFIG.get().getFormatting().getRegistryName()), val ->
                        Waila.CONFIG.get().getFormatting().setRegistryName(val.isEmpty() || !val.contains("%s") ? Waila.CONFIG.get().getFormatting().getRegistryName() : val)
                    ));
                    return options;
                }
            }))));
        return options;
    }

}
