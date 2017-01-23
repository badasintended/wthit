package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RayTracing {

    private static RayTracing _instance;
    private RayTraceResult target = null;
    private ItemStack targetStack = null;
    private Entity targetEntity = null;
    private Minecraft mc = Minecraft.getMinecraft();
    private RayTracing() {
    }

    public void fire() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            this.target = mc.objectMouseOver;
            this.targetStack = null;
            return;
        }

        Entity viewpoint = mc.getRenderViewEntity();
        if (viewpoint == null) return;

        this.target = this.rayTrace(viewpoint, mc.playerController.getBlockReachDistance(), 0);

        if (this.target == null) return;
    }

    public RayTraceResult getTarget() {
        return this.target;
    }

    public ItemStack getTargetStack() {
        if (this.target != null && this.target.typeOfHit == RayTraceResult.Type.BLOCK)
            this.targetStack = this.getIdentifierStack();
        else
            this.targetStack = null;

        return this.targetStack;
    }

    public Entity getTargetEntity() {
        if (this.target.typeOfHit == RayTraceResult.Type.ENTITY)
            this.targetEntity = this.getIdentifierEntity();
        else
            this.targetEntity = null;

        return this.targetEntity;
    }

    public RayTraceResult rayTrace(Entity entity, double par1, float par3) {
        Vec3d vec3 = entity.getPositionEyes(par3);
        Vec3d vec31 = entity.getLook(par3);
        Vec3d vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);

        //if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
        if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
            return entity.getEntityWorld().rayTraceBlocks(vec3, vec32, true);
        else
            return entity.getEntityWorld().rayTraceBlocks(vec3, vec32, false);
    }

    public ItemStack getIdentifierStack() {
        ArrayList<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return null;

        Collections.sort(items, new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack stack0, ItemStack stack1) {
                return stack1.getItemDamage() - stack0.getItemDamage();
            }
        });

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        ArrayList<Entity> ents = new ArrayList<Entity>();

        if (this.target == null)
            return null;

        if (ModuleRegistrar.instance().hasOverrideEntityProviders(this.target.entityHit)) {
            for (List<IWailaEntityProvider> listProviders : ModuleRegistrar.instance().getOverrideEntityProviders(this.target.entityHit).values()) {
                for (IWailaEntityProvider provider : listProviders) {
                    ents.add(provider.getWailaOverride(DataAccessorCommon.instance, ConfigHandler.instance()));
                }
            }
        }

        if (ents.size() > 0)
            return ents.get(0);
        else
            return this.target.entityHit;
    }

    public ArrayList<ItemStack> getIdentifierItems() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        if (this.target == null)
            return items;

        EntityPlayer player = mc.player;
        World world = mc.world;
        BlockPos pos = target.getBlockPos();

        //int   blockID         = world.getBlockId(x, y, z);
        //Block mouseoverBlock  = Block.blocksList[blockID];
        Block mouseoverBlock = world.getBlockState(pos).getBlock();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (mouseoverBlock == null)
            return items;

        if (ModuleRegistrar.instance().hasStackProviders(mouseoverBlock)) {
            for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(mouseoverBlock).values()) {
                for (IWailaDataProvider provider : providersList) {
                    ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack != null) {

                        if (providerStack.getItem() == null)
                            return new ArrayList<ItemStack>();

                        items.add(providerStack);
                    }
                }
            }
        }

        if (tileEntity != null && ModuleRegistrar.instance().hasStackProviders(tileEntity)) {
            for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(tileEntity).values()) {

                for (IWailaDataProvider provider : providersList) {
                    ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack != null) {

                        if (providerStack.getItem() == null)
                            return new ArrayList<ItemStack>();

                        items.add(providerStack);
                    }
                }
            }
        }

        if (!items.isEmpty())
            return items;

        if (world.getTileEntity(pos) == null && Item.getItemFromBlock(mouseoverBlock) != null)
            items.add(mouseoverBlock.getPickBlock(world.getBlockState(pos), target, world, pos, player));

        if (!items.isEmpty())
            return items;

        ItemStack pick = mouseoverBlock.getPickBlock(world.getBlockState(pos), target, world, pos, player);//(this.target, world, pos, player);
        if (pick != null)
            items.add(pick);

        if (!items.isEmpty())
            return items;

        if (mouseoverBlock instanceof IShearable) {
            IShearable shearable = (IShearable) mouseoverBlock;
            if (shearable.isShearable(new ItemStack(Items.SHEARS), world, pos))
                items.addAll(shearable.onSheared(new ItemStack(Items.SHEARS), world, pos, 0));
        }

        if (items.isEmpty()) {
            if (Item.getItemFromBlock(mouseoverBlock) != null) {
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
        Vec3d start = new Vec3d(headVec.xCoord, headVec.yCoord, headVec.zCoord);
        Vec3d lookVec = entity.getLook(1.0F);
        headVec.add(new Vec3d(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance));

        return entity.getEntityWorld().rayTraceBlocks(start, headVec, ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true));
    }
}
