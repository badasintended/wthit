package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.ArrayList;
import java.util.Objects;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.EnchantmentDisplayMode;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.Nullable;

public enum ItemEntityProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final ResourceLocation AUTHOR = Options.BOOK_DETAILS.withSuffix(".author");
    private static final ResourceLocation GENERATION = Options.BOOK_DETAILS.withSuffix(".generation");
    static final ResourceLocation PAGES = ResourceLocation.withDefaultNamespace("book.pages");

    private static long lastEnchantmentTime = 0;
    private static int enchantmentIndex = 0;
    private static int curseIndex = 0;

    @Nullable
    @Override
    public Entity getOverride(IEntityAccessor accessor, IPluginConfig config) {
        return !config.getBoolean(Options.ENTITY_ITEM_ENTITY) ? EMPTY_ENTITY : null;
    }

    @Override
    public ITooltipComponent getIcon(IEntityAccessor accessor, IPluginConfig config) {
        return new ItemComponent(accessor.<ItemEntity>getEntity().getItem());
    }

    @Override
    public void appendHead(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var formatter = IWailaConfig.get().getFormatter();

        var stack = accessor.<ItemEntity>getEntity().getItem();
        tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, formatter.entityName(stack.getHoverName()));

        if (config.getBoolean(WailaConstants.CONFIG_SHOW_REGISTRY)) {
            tooltip.setLine(WailaConstants.REGISTRY_NAME_TAG, formatter.registryName(BuiltInRegistries.ITEM.getKey(stack.getItem())));
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var stack = accessor.<ItemEntity>getEntity().getItem();
        appendBookProperties(tooltip, stack, config, true);
    }

    @Override
    public void appendTail(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(WailaConstants.CONFIG_SHOW_MOD_NAME)) {
            var mod = IModInfo.get(accessor.<ItemEntity>getEntity().getItem()).getName();
            tooltip.setLine(WailaConstants.MOD_NAME_TAG, IWailaConfig.get().getFormatter().modName(mod));
        }
    }

    public static void appendBookProperties(ITooltip tooltip, ItemStack stack, IPluginConfig config, boolean showPages) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            EnchantmentDisplayMode mode = config.getEnum(Options.BOOK_ENCHANTMENT_DISPLAY_MODE);
            if (mode == EnchantmentDisplayMode.DISABLED) return;

            if (mode == EnchantmentDisplayMode.CYCLE) {
                var enchantmentTiming = config.getInt(Options.BOOK_ENCHANTMENT_CYCLE_TIMING);
                var enchantmentsTag = EnchantedBookItem.getEnchantments(stack);
                var now = System.currentTimeMillis();

                var enchantments = new ArrayList<EnchantmentInstance>();
                var curses = new ArrayList<EnchantmentInstance>();

                for (var i = 0; i < enchantmentsTag.size(); i++) {
                    var enchantmentTag = enchantmentsTag.getCompound(i);
                    var enchantment = BuiltInRegistries.ENCHANTMENT.get(EnchantmentHelper.getEnchantmentId(enchantmentTag));
                    if (enchantment == null) continue;

                    var level = EnchantmentHelper.getEnchantmentLevel(enchantmentTag);
                    var instance = new EnchantmentInstance(enchantment, level);

                    if (enchantment.isCurse()) curses.add(instance);
                    else enchantments.add(instance);
                }

                if ((now - lastEnchantmentTime) >= enchantmentTiming) {
                    lastEnchantmentTime = now;
                    enchantmentIndex++;
                    curseIndex++;

                    if (enchantmentIndex > (enchantments.size() - 1)) enchantmentIndex = 0;
                    if (curseIndex > (curses.size() - 1)) curseIndex = 0;
                }

                Component text = null;

                if (!enchantments.isEmpty()) {
                    var instance = enchantments.get(enchantmentIndex);
                    text = instance.enchantment.getFullname(instance.level);
                }

                if (!curses.isEmpty()) {
                    var instance = curses.get(curseIndex);
                    var curse = instance.enchantment.getFullname(instance.level);
                    if (text == null) text = curse;
                    else text = text.copy().append(CommonComponents.NEW_LINE).append(curse);
                }

                if (text != null) tooltip.setLine(Options.BOOK_ENCHANTMENT_DISPLAY_MODE, text);
            } else {
                var enchantments = EnchantmentHelper.getEnchantments(stack);
                MutableComponent text = null;

                if (mode == EnchantmentDisplayMode.COMBINED) {
                    MutableComponent enchantmentLine = null;
                    MutableComponent curseLine = null;

                    for (var entry : enchantments.entrySet()) {
                        var enchantment = entry.getKey();
                        int level = entry.getValue();
                        var name = enchantment.getFullname(level);

                        if (enchantment.isCurse()) {
                            if (curseLine == null) {
                                curseLine = Component.empty().append(name);
                            } else {
                                curseLine.append(Component.literal(", ")).append(name);
                            }
                        } else {
                            if (enchantmentLine == null) {
                                enchantmentLine = Component.empty().append(name);
                            } else {
                                enchantmentLine.append(Component.literal(", ")).append(name);
                            }
                        }
                    }

                    if (enchantmentLine != null) text = enchantmentLine;
                    if (curseLine != null) {
                        if (text == null) text = curseLine;
                        else text.append(CommonComponents.NEW_LINE).append(curseLine);
                    }
                } else {
                    for (var entry : enchantments.entrySet()) {
                        var name = entry.getKey().getFullname(entry.getValue());
                        if (text == null) text = Component.empty().append(name);
                        else text.append(CommonComponents.NEW_LINE).append(name);
                    }
                }

                if (text != null) tooltip.setLine(Options.BOOK_ENCHANTMENT_DISPLAY_MODE, text);
            }

        } else if (stack.is(Items.WRITTEN_BOOK)) {
            if (!config.getBoolean(Options.BOOK_WRITTEN) || !stack.hasTag()) return;

            var tag = Objects.requireNonNull(stack.getTag());
            var author = tag.getString(WrittenBookItem.TAG_AUTHOR);
            var generation = WrittenBookItem.getGeneration(stack);

            if (!StringUtil.isNullOrEmpty(author)) {
                tooltip.setLine(AUTHOR, Component.translatable("book.byAuthor", author));
            }

            tooltip.setLine(GENERATION, Component.translatable("book.generation." + generation));
            if (showPages) tooltip.setLine(PAGES, Component.translatable(Tl.Tooltip.Book.PAGES, WrittenBookItem.getPageCount(stack)));

        } else if (stack.is(Items.WRITABLE_BOOK)) {
            if (!config.getBoolean(Options.BOOK_WRITTEN) || !stack.hasTag()) return;

            if (showPages) tooltip.setLine(PAGES, Component.translatable(Tl.Tooltip.Book.PAGES, WrittenBookItem.getPageCount(stack)));
        }
    }

}
