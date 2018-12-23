package mcp.mobius.waila.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.world.World;

import java.util.List;

/**
 * Callback class interface used to provide Entity tooltip informations to Waila.<br>
 * All methods in this interface shouldn't to be called by the implementing mod. An instance of the class is to be
 * registered to Waila via the {@link IWailaRegistrar} instance provided in the original registration callback method
 * (cf. {@link IWailaRegistrar} documentation for more information).
 *
 * @author ProfMobius
 */
public interface IWailaEntityProvider {

    /**
     * Callback used to override the default Waila lookup system.</br>
     * Will only be called if the implementing class is registered via {@link IWailaRegistrar#registerOverrideEntityProvider}.</br>
     *
     * @param accessor Contains most of the relevant information about the current environment.
     * @param config   Current configuration of Waila.
     * @return null if override is not required, an Entity otherwise.
     */
    default Entity getOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
     * Will only be called if the implementing class is registered via {@link IWailaRegistrar#registerComponentProvider(IWailaEntityProvider, TooltipPosition, Class)}.</br>
     * You are supposed to always return the modified input currenttip.</br>
     * <p>
     * This method is only called on the client side. If you require data from the server, you should also implement
     * {@link IServerDataProvider#appendServerData(CompoundTag, ServerPlayerEntity, World, Object)} and add the data to the {@link CompoundTag}
     * there, which can then be read back using {@link IWailaDataAccessor#getServerData()} ()}. If you rely on the client knowing
     * the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip    Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
     * @param accessor   Contains most of the relevant information about the current environment.
     * @param config     Current configuration of Waila.
     */
    default void appendHead(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
     * Will only be called if the implementing class is registered via {@link IWailaRegistrar#registerComponentProvider(IWailaEntityProvider, TooltipPosition, Class)}.</br>
     * You are supposed to always return the modified input currenttip.</br>
     * <p>
     * This method is only called on the client side. If you require data from the server, you should also implement
     * {@link IServerDataProvider#appendServerData(CompoundTag, ServerPlayerEntity, World, Object)} and add the data to the {@link CompoundTag}
     * there, which can then be read back using {@link IWailaDataAccessor#getServerData()} ()}. If you rely on the client knowing
     * the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip    Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
     * @param accessor   Contains most of the relevant information about the current environment.
     * @param config     Current configuration of Waila.
     */
    default void appendBody(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

    }

    /**
     * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
     * Will only be called if the implementing class is registered via {@link IWailaRegistrar#registerComponentProvider(IWailaEntityProvider, TooltipPosition, Class)}.</br>
     * You are supposed to always return the modified input currenttip.</br>
     * <p>
     * This method is only called on the client side. If you require data from the server, you should also implement
     * {@link IServerDataProvider#appendServerData(CompoundTag, ServerPlayerEntity, World, Object)} and add the data to the {@link CompoundTag}
     * there, which can then be read back using {@link IWailaDataAccessor#getServerData()} ()}. If you rely on the client knowing
     * the data you need, you are not guaranteed to have the proper values.
     *
     * @param tooltip    Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
     * @param accessor   Contains most of the relevant information about the current environment.
     * @param config     Current configuration of Waila.
     */
    default void appendTail(List<TextComponent> tooltip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

    }
}
