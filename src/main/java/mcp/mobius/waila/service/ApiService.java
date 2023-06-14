package mcp.mobius.waila.service;

import java.nio.file.Path;
import java.util.Collection;

import com.mojang.blaze3d.vertex.BufferBuilder;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.ITheme;
import mcp.mobius.waila.api.IThemeType;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import mcp.mobius.waila.gui.hud.theme.ThemeType;
import mcp.mobius.waila.mixin.GuiAccess;
import mcp.mobius.waila.plugin.PluginInfo;
import mcp.mobius.waila.util.DisplayUtil;
import mcp.mobius.waila.util.ModInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.joml.Matrix4f;

public class ApiService implements IApiService {

    @Override
    public IModInfo getModInfo(ItemStack stack) {
        Item item = stack.getItem();

        if (ResourceLocation.DEFAULT_NAMESPACE.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) {
            if (item instanceof EnchantedBookItem) {
                ListTag enchantmentsNbt = EnchantedBookItem.getEnchantments(stack);
                if (enchantmentsNbt.size() == 1) {
                    CompoundTag enchantmentNbt = enchantmentsNbt.getCompound(0);
                    ResourceLocation id = ResourceLocation.tryParse(enchantmentNbt.getString("id"));
                    if (id != null && BuiltInRegistries.ENCHANTMENT.containsKey(id)) {
                        return IModInfo.get(id.getNamespace());
                    }
                }
            } else if (item instanceof PotionItem || item instanceof TippedArrowItem) {
                Potion potionType = PotionUtils.getPotion(stack);
                ResourceLocation id = BuiltInRegistries.POTION.getKey(potionType);
                return IModInfo.get(id.getNamespace());
            } else if (item instanceof SpawnEggItem spawnEggItem) {
                ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(spawnEggItem.getType(null));
                return IModInfo.get(id.getNamespace());
            }
        }

        return IModInfo.get(item);
    }

    @Override
    public IBlacklistConfig getBlacklistConfig() {
        return Waila.BLACKLIST_CONFIG.get();
    }

    @Override
    public <T> IJsonConfig.Builder0<T> createConfigBuilder(Class<T> clazz) {
        return new JsonConfig.Builder<>(clazz);
    }

    @Override
    public IModInfo getModInfo(String namespace) {
        return ModInfo.get(namespace);
    }

    @Override
    public IPluginInfo getPluginInfo(ResourceLocation pluginId) {
        return PluginInfo.get(pluginId);
    }

    @Override
    public Collection<IPluginInfo> getAllPluginInfoFromMod(String modId) {
        return PluginInfo.getAllFromMod(modId);
    }

    @Override
    public Collection<IPluginInfo> getAllPluginInfo() {
        return PluginInfo.getAll();
    }

    @Override
    public IWailaConfig getConfig() {
        return Waila.CONFIG.get();
    }

    @Override
    public void renderComponent(GuiGraphics ctx, ITooltipComponent component, int x, int y, float delta) {
        DisplayUtil.renderComponent(ctx, component, x, y, delta);
    }

    @Override
    public int getPairComponentColonOffset() {
        return TooltipRenderer.colonOffset;
    }

    @Override
    public int getColonFontWidth() {
        return TooltipRenderer.colonWidth;
    }

    @Override
    public int getFontColor() {
        return TooltipRenderer.state.getTheme().getDefaultTextColor();
    }

    @Override
    public void fillGradient(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int start, int end) {
        DisplayUtil.fillGradient(matrix, buf, x, y, w, h, start, end);
    }

    @Override
    public void renderRectBorder(Matrix4f matrix, BufferBuilder buf, int x, int y, int w, int h, int s, int gradStart, int gradEnd) {
        DisplayUtil.renderRectBorder(matrix, buf, x, y, w, h, s, gradStart, gradEnd);
    }

    @Override
    public <T extends ITheme> IThemeType.Builder<T> createThemeTypeBuilder(Class<T> clazz) {
        return new ThemeType<>(clazz);
    }

    @Override
    public String getDefaultEnergyUnit() {
        return "E";
    }

    @Override
    public Path getConfigDir() {
        return Waila.CONFIG_DIR;
    }

    @Override
    public ResourceLocation getGuiIconsTexture() {
        return GuiAccess.wthit_guiIconsLocation();
    }

}
