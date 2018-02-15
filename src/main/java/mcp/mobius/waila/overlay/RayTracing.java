package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class RayTracing {

    private static RayTracing _instance;
    private RayTraceResult target = null;
    private ItemStack targetStack = ItemStack.EMPTY;
    private Minecraft mc = Minecraft.getMinecraft();

    private RayTracing() {
    }

    public void fire() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            this.target = mc.objectMouseOver;
            this.targetStack = ItemStack.EMPTY;
            return;
        }

        Entity viewpoint = mc.getRenderViewEntity();
        if (viewpoint == null) return;

        this.target = this.rayTrace(viewpoint, mc.playerController.getBlockReachDistance(), 0);
    }

    public RayTraceResult getTarget() {
        return this.target;
    }

    public ItemStack getTargetStack() {
        return targetStack = target != null && target.typeOfHit == RayTraceResult.Type.BLOCK ? getIdentifierStack() : ItemStack.EMPTY;
    }

    public Entity getTargetEntity() {
        return target.typeOfHit == RayTraceResult.Type.ENTITY ? getIdentifierEntity() : null;
    }

    public RayTraceResult rayTrace(Entity entity, double playerReach, float partialTicks) {
        Vec3d eyePosition = entity.getPositionEyes(partialTicks);
        Vec3d lookVector = entity.getLook(partialTicks);
        Vec3d traceEnd = eyePosition.addVector(lookVector.x * playerReach, lookVector.y * playerReach, lookVector.z * playerReach);

        return entity.getEntityWorld().rayTraceBlocks(eyePosition, traceEnd, ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true));
    }

    public ItemStack getIdentifierStack() {
        ArrayList<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return ItemStack.EMPTY;

        items.sort((stack0, stack1) -> stack1.getItemDamage() - stack0.getItemDamage());

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        if (this.target == null)
            return null;

        ArrayList<Entity> ents = new ArrayList<>();

        if (ModuleRegistrar.instance().hasOverrideEntityProviders(this.target.entityHit))
            for (List<IWailaEntityProvider> listProviders : ModuleRegistrar.instance().getOverrideEntityProviders(this.target.entityHit).values())
                for (IWailaEntityProvider provider : listProviders)
                    ents.add(provider.getWailaOverride(DataAccessorCommon.instance, ConfigHandler.instance()));

        return ents.size() > 0 ? ents.get(0) : target.entityHit;
    }

    public ArrayList<ItemStack> getIdentifierItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        if (this.target == null)
            return items;

        EntityPlayer player = mc.player;
        World world = mc.world;
        BlockPos pos = target.getBlockPos();

        Block mouseoverBlock = world.getBlockState(pos).getBlock();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (mouseoverBlock == Blocks.AIR)
            return items;

        if (ModuleRegistrar.instance().hasStackProviders(mouseoverBlock)) {
            for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(mouseoverBlock).values()) {
                for (IWailaDataProvider provider : providersList) {
                    ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack.isEmpty())
                        continue;

                    items.add(providerStack);
                }
            }
        }

        if (tileEntity != null && ModuleRegistrar.instance().hasStackProviders(tileEntity)) {
            for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(tileEntity).values()) {

                for (IWailaDataProvider provider : providersList) {
                    ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack.isEmpty())
                        continue;

                    items.add(providerStack);
                }
            }
        }

        if (!items.isEmpty())
            return items;

        ItemStack pick = mouseoverBlock.getPickBlock(world.getBlockState(pos), target, world, pos, player);
        //noinspection ConstantConditions
        if (pick == null)
            throw new NullPointerException(mouseoverBlock.getRegistryName() + " from mod " + ModIdentification.findModContainer(mouseoverBlock.getRegistryName().getResourceDomain()).getName() + " returned a null ItemStack in getPickBlock(...). Please report this to them.");

        if (!pick.isEmpty())
            items.add(pick);

        if (!items.isEmpty())
            return items;

        if (mouseoverBlock instanceof IShearable) {
            IShearable shearable = (IShearable) mouseoverBlock;
            if (shearable.isShearable(new ItemStack(Items.SHEARS), world, pos))
                items.addAll(shearable.onSheared(new ItemStack(Items.SHEARS), world, pos, 0));
        }

        if (items.isEmpty()) {
            if (Item.getItemFromBlock(mouseoverBlock) != Item.getItemFromBlock(Blocks.AIR)) {
                if (Item.getItemFromBlock(mouseoverBlock).getHasSubtypes())
                    items.add(new ItemStack(mouseoverBlock, 1, mouseoverBlock.getMetaFromState(world.getBlockState(pos))));
                else
                    items.add(new ItemStack(mouseoverBlock));
            }
        }

        return items;
    }

    public static RayTracing instance() {
        if (_instance == null)
            _instance = new RayTracing();
        return _instance;
    }

    public static RayTraceResult rayTraceServer(Entity entity, double distance) {
        double eyeHeight = entity.posY + entity.getEyeHeight();
        Vec3d headVec = new Vec3d(entity.posX, eyeHeight, entity.posZ);
        Vec3d start = new Vec3d(headVec.x, headVec.y, headVec.z);
        Vec3d lookVec = entity.getLook(1.0F);
        headVec.add(new Vec3d(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance));

        return entity.getEntityWorld().rayTraceBlocks(start, headVec, ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true));
    }
}
