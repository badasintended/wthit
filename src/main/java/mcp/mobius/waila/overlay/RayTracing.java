package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.config.Configuration;

public class RayTracing {

	private static RayTracing _instance;
	private RayTracing(){}	
	public static RayTracing instance(){
		if(_instance == null)
			_instance = new RayTracing();
		return _instance;
	}
	
	private MovingObjectPosition target      = null;
	private ItemStack            targetStack = null;
	private Entity               targetEntity= null;
	private Minecraft            mc          = Minecraft.getMinecraft();
	
	public void fire(){
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
			this.target = mc.objectMouseOver;
			this.targetStack = null;
			return;
		}
		
		EntityLivingBase viewpoint = mc.renderViewEntity;
		if (viewpoint == null) return;
			
		this.target      = this.rayTrace(viewpoint, mc.playerController.getBlockReachDistance() - 0.5, 0);
		
		if (this.target == null) return;
	}
	
	public MovingObjectPosition getTarget(){ 
		return this.target;
	}
	
	public ItemStack getTargetStack(){
		if (this.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			this.targetStack = this.getIdentifierStack();
		else
			this.targetStack = null;		
		
		return this.targetStack;
	}
	
	public Entity getTargetEntity(){
		if (this.target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
			this.targetEntity = this.getIdentifierEntity();
		else
			this.targetEntity = null;		
		
		return this.targetEntity;
	}	
	
    public MovingObjectPosition rayTrace(EntityLivingBase entity, double par1, float par3)
    {
        Vec3 vec3  = entity.getPosition(par3);
        Vec3 vec31 = entity.getLook(par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        
        //if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
        if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
        	return entity.worldObj.rayTraceBlocks(vec3, vec32, true);
        else
        	return entity.worldObj.rayTraceBlocks(vec3, vec32, false);
    }	
	
	public ItemStack getIdentifierStack(){
        World world = mc.theWorld;
        ArrayList<ItemStack> items = this.getIdentifierItems();
        
        if (items.isEmpty())
            return null;
        
        Collections.sort(items, new Comparator<ItemStack>()
        {
            @Override
            public int compare(ItemStack stack0, ItemStack stack1)
            {
                return stack1.getItemDamage() - stack0.getItemDamage();
            }
        });

        return items.get(0);		
	}
	
	public Entity getIdentifierEntity(){
        ArrayList<Entity> ents = new ArrayList<Entity>();		
		
    	if (this.target == null)
    		return null;        
        
        if (ModuleRegistrar.instance().hasOverrideEntityProviders(this.target.entityHit))
        	ents.add(ModuleRegistrar.instance().getOverrideEntityProviders(this.target.entityHit).get(0).getWailaOverride(DataAccessorEntity.instance, ConfigHandler.instance()));
        
        if(ents.size() > 0)
            return ents.get(0);
        else 
        	return this.target.entityHit;
	}	
	
    public ArrayList<ItemStack> getIdentifierItems()
    {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        
    	if (this.target == null)
    		return items;
    	
    	EntityPlayer player = mc.thePlayer;
    	World world = mc.theWorld; 
    	
        int x = this.target.blockX;
        int y = this.target.blockY;
        int z = this.target.blockZ;
        //int   blockID         = world.getBlockId(x, y, z);
        //Block mouseoverBlock  = Block.blocksList[blockID];
        Block mouseoverBlock  = world.getBlock(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z); 
        if (mouseoverBlock == null) return items;
        
        if (ModuleRegistrar.instance().hasStackProviders(mouseoverBlock)){
        	ItemStack providerStack = ModuleRegistrar.instance().getStackProviders(mouseoverBlock).get(0).getWailaStack(DataAccessorBlock.instance, ConfigHandler.instance());
        	if (providerStack != null)
        		items.add(providerStack);
        }
        
        if (tileEntity != null &&  ModuleRegistrar.instance().hasStackProviders(tileEntity)){
        	ItemStack providerStack = ModuleRegistrar.instance().getStackProviders(tileEntity).get(0).getWailaStack(DataAccessorBlock.instance, ConfigHandler.instance());
        	if (providerStack != null)
        		items.add(providerStack);
        }        
        
        if(items.size() > 0)
            return items;

        if (world.getTileEntity(x, y, z) == null){
	        try{
	        	ItemStack block = new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z));

	        	//System.out.printf("%s %s %s\n", block, block.getDisplayName(), block.getItemDamage());	        	
	        	
	        	if (block.getItem() != null)
	        		items.add(block);
	        	//else
	        	//	items.add(new ItemStack(new ItemBlock(mouseoverBlock)));
	        	//else
	        	//	items.add(new ItemStack(Item.getItemFromBlock(mouseoverBlock)));
	        	
	        	
	        } catch(Exception e){
	        }
        }

        if(items.size() > 0)
            return items;

        try{
        ItemStack pick = mouseoverBlock.getPickBlock(this.target, world, x, y, z);
        if(pick != null)
            items.add(pick);
        }catch(Exception e){}
        
        if(items.size() > 0)
            return items;           

        /*
        try
        {
            items.addAll(mouseoverBlock.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        }
        catch(Exception e){}
        
        if(items.size() > 0)
            return items;         
        */
        
        if(mouseoverBlock instanceof IShearable)
        {
            IShearable shearable = (IShearable)mouseoverBlock;
            if(shearable.isShearable(new ItemStack(Items.shears), world, x, y, z))
            {
                items.addAll(shearable.onSheared(new ItemStack(Items.shears), world, x, y, z, 0));
            }
        }
        
        if(items.size() == 0)
           items.add(0, new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z)));
        
        return items;
    }
    
}
