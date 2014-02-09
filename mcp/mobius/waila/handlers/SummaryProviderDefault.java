package mcp.mobius.waila.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaSummaryProvider;

public class SummaryProviderDefault implements IWailaSummaryProvider {

	//TODO : Redo the case for ItemBlocks
	
	public SummaryProviderDefault() {	}

	@Override
	public LinkedHashMap<String, String> getSummary(ItemStack stack, LinkedHashMap<String, String> currentSummary, IWailaConfigHandler config) {
		
		if (!(stack.getItem() instanceof ItemBlock)){
			//For now, we have either a basic item or a tool/weapon/armor
			if (this.getToolMaterialName(stack) != null){
				currentSummary.put("Material",   this.getToolMaterialName(stack));				
				currentSummary.put("Tier", 		 this.getHarvestLevel(stack) != null?String.valueOf(this.getHarvestLevel(stack)):null);
				currentSummary.put("Durability", this.getItemDurability(stack) != null?String.valueOf(this.getItemDurability(stack)):null);
			
				currentSummary.put("Harvest", 	 this.getEffectiveBlock(stack));
				if (this.getEffectiveBlock(stack) != null)
					currentSummary.put("Efficiency", this.getEfficiencyOnProperMaterial(stack) != null?String.valueOf(this.getEfficiencyOnProperMaterial(stack)):null);
			
				currentSummary.put("Enchant", 	 this.getToolEnchantability(stack) != null?String.valueOf(this.getToolEnchantability(stack)):null);		
				currentSummary.put("Damage", 	 this.getDamageVsEntity(stack) != null?String.valueOf(this.getDamageVsEntity(stack)):null);
			}
			else if (this.getArmorMaterialName(stack) != null){
				currentSummary.put("Material",   this.getArmorMaterialName(stack));
				currentSummary.put("Durability", this.getItemDurability(stack) != null?String.valueOf(this.getItemDurability(stack)):null);
				currentSummary.put("Armor Value", this.getDamageReduction(stack) != null?String.valueOf(this.getDamageReduction(stack)):null);
				currentSummary.put("Enchant", 	 this.getArmorEnchantability(stack) != null?String.valueOf(this.getArmorEnchantability(stack)):null);				
			}
		} else {
			/*
			//We have an ItemBlock
			ItemBlock itemBlock = (ItemBlock)stack.getItem();
			Block     block     = itemBlock.getBlock();
			try{currentSummary.put("Hardness", String.valueOf(block.getBlockHardness(Minecraft.getMinecraft().theWorld, 0, 0, 0)));} catch (Exception e){};
			try{currentSummary.put("Resistance", String.valueOf(block.getExplosionResistance(null)));} catch (Exception e){};
			
			try{
				ArrayList<ItemStack> droppedStacks = block.getBlockDropped(Minecraft.getMinecraft().theWorld, 0, 0, 0, stack.getItemDamage(), 3);
				if (droppedStacks.size() == 1)
					currentSummary.put("Drop", droppedStacks.get(0).getDisplayName());
				else
					for (int i=0; i < droppedStacks.size(); i++)
						currentSummary.put(String.format("Drop %s", i), droppedStacks.get(i).getDisplayName());
			}catch (Exception e){System.out.printf("%s\n",e);};

			try{
				if (block instanceof IShearable){
					IShearable shearable = (IShearable)block;
					ArrayList<ItemStack> droppedStacks = shearable.onSheared(null, Minecraft.getMinecraft().theWorld, 0, 0, 0, 3);
					if (droppedStacks.size() == 1)
						currentSummary.put("Sheared", droppedStacks.get(0).getDisplayName());
					else
						for (int i=0; i < droppedStacks.size(); i++)
							currentSummary.put(String.format("Sheared %s", i), droppedStacks.get(i).getDisplayName());
				}
			}catch (Exception e){System.out.printf("%s\n",e);};	
			*/		
		}
		
		
		
		return currentSummary;
	}

	public Item.ToolMaterial getToolMaterial(ItemStack stack){
		Class itemClass = stack.getItem().getClass();
		Item.ToolMaterial material = null;
		try{
			Field getMaterial = this.findField(itemClass, "toolMaterial");
			if (getMaterial != null){
				getMaterial.setAccessible(true);
				material = (Item.ToolMaterial)(getMaterial.get(stack.getItem()));
			}
		} catch (Exception e){}		
		return material;
	}

	public ItemArmor.ArmorMaterial getArmorMaterial(ItemStack stack){
		Class itemClass = stack.getItem().getClass();
		ItemArmor.ArmorMaterial material = null;
		try{
			Field getMaterial = this.findField(itemClass, "material");
			if (getMaterial != null){
				getMaterial.setAccessible(true);
				material = (ItemArmor.ArmorMaterial)(getMaterial.get(stack.getItem()));
			}
		} catch (Exception e){}		
		return material;
	}	
	
	public String getToolMaterialName(ItemStack stack){
		Item.ToolMaterial material = this.getToolMaterial(stack);
		if (material == null)
			return null;
		
		HashMap<String, String> matName = new HashMap<String, String>();
		matName.put("WOOD", "Wood");
		matName.put("STONE", "Stone");
		matName.put("IRON", "Iron");
		matName.put("EMERALD", "Diamond");
		matName.put("GOLD", "Gold");
		String materialName = material.toString();
		materialName = matName.containsKey(materialName)?matName.get(materialName):materialName;
		return materialName;
	}

	public String getArmorMaterialName(ItemStack stack){
		ItemArmor.ArmorMaterial material = this.getArmorMaterial(stack);
		if (material == null)
			return null;
		
		HashMap<String, String> matName = new HashMap<String, String>();
		matName.put("CLOTH", "Cloth");
		matName.put("CHAIN", "Chain");
		matName.put("IRON", "Iron");
		matName.put("DIAMOND", "Diamond");
		matName.put("GOLD", "Gold");
		String materialName = material.toString();
		materialName = matName.containsKey(materialName)?matName.get(materialName):materialName;
		return materialName;
	}	
	
	public String getEffectiveBlock(ItemStack stack){
		Class itemClass = stack.getItem().getClass();
		try{
			Field blocksEffectiveField   = itemClass.getField("blocksEffectiveAgainst");
			Block[] effectiveBlocksArray = (Block[])(blocksEffectiveField.get(stack.getItem()));
			ArrayList<Block> effectiveBlocks = new ArrayList<Block>();
			Collections.addAll(effectiveBlocks, effectiveBlocksArray);
			String effectiveAgainst = "";
			
			if (effectiveBlocks.contains(Blocks.obsidian))
				effectiveAgainst = Blocks.obsidian.getLocalizedName();
			else if (effectiveBlocks.contains(Blocks.stone))
				effectiveAgainst = Blocks.stone.getLocalizedName();
			else if (effectiveBlocks.contains(Blocks.dirt))
				effectiveAgainst = Blocks.dirt.getLocalizedName();
			else if (effectiveBlocks.contains(Blocks.log))
				effectiveAgainst = Blocks.log.getLocalizedName();
	
			return effectiveAgainst.equals("")?null:effectiveAgainst;
		}catch (Exception e){}		
		return null;
	}
	
	public Integer getHarvestLevel(ItemStack stack){
		Item.ToolMaterial material = this.getToolMaterial(stack);
		return  material!=null?material.getHarvestLevel():null;
	}
	
	public Float getEfficiencyOnProperMaterial(ItemStack stack){
		Item.ToolMaterial material = this.getToolMaterial(stack);
		return  material!=null?material.getEfficiencyOnProperMaterial():null;
	}	
	
	public Integer getToolEnchantability(ItemStack stack){
		Item.ToolMaterial material = this.getToolMaterial(stack);
		return  material!=null?material.getEnchantability():null;
	}
	
	public Float getDamageVsEntity(ItemStack stack){
		Item.ToolMaterial material = this.getToolMaterial(stack);
		return  material!=null?material.getDamageVsEntity():null;	
	}

	public Integer getItemDurability(ItemStack stack){
		return  stack.getMaxDamage() > 0?stack.getMaxDamage():null;			
	}	
	
	public Integer getDamageReduction(ItemStack stack){
		if (stack.getItem() instanceof ItemArmor){
			ItemArmor armor = (ItemArmor)stack.getItem();
			return armor.damageReduceAmount;
		}
		return  null;		
	}

	public Integer getArmorEnchantability(ItemStack stack){
		ItemArmor.ArmorMaterial material = this.getArmorMaterial(stack);
		return  material!=null?material.getEnchantability():null;
	}	
	
	public Field findField(Class lookup, String name){
		Field retField = null;
		try{
			retField = lookup.getDeclaredField(name);
		} catch (Exception e){
			if (lookup.getSuperclass() == null)
				retField = null;
			else
				retField = findField(lookup.getSuperclass(), name);
		}
		return retField;
	}	
}
