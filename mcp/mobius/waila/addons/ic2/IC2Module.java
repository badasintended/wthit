package mcp.mobius.waila.addons.ic2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class IC2Module {

	public static Class TileEntityStandardMachine = null;
	public static Field TESM_DefaultEnergyStorage = null;	
	public static Field TESM_DefaultEnergyConsume = null;
	public static Field TESM_DefaultOperationLength = null;	
	public static Field TESM_MaxEnergy            = null;	
	
	public static Class TileEntityBaseGenerator   = null;
	public static Field TEBG_MaxStorage           = null;
	public static Field TEBG_Production           = null;	
	
	public static Class TileEntityElectricBlock   = null;
	public static Field TEEB_MaxStorage           = null;	
	public static Field TEEB_Output               = null;	
	
	public static Class  IEnergySink         = null;
	public static Method IES_GetMaxSafeInput = null;
	
	/* Some required items */
	public static Class     IC2Items           = null;
	public static Field     TransformerUpgrade = null;
	public static ItemStack TransformerUpgradeStack   = null;
	public static Field     EnergyStorageUpgrade      = null;
	public static ItemStack EnergyStorageUpgradeStack = null;		
	public static Field     OverclockerUpgrade        = null;
	public static ItemStack OverclockerUpgradeStack   = null;
	
	public static void register(){

		
		try{
			TileEntityStandardMachine = Class.forName("ic2.core.block.machine.tileentity.TileEntityStandardMachine");
			TESM_DefaultEnergyStorage = TileEntityStandardMachine.getField("defaultEnergyStorage");
			TESM_DefaultEnergyConsume = TileEntityStandardMachine.getField("defaultEnergyConsume");
			TESM_DefaultOperationLength = TileEntityStandardMachine.getField("defaultOperationLength");
			TESM_MaxEnergy            = TileEntityStandardMachine.getField("maxEnergy");			
			
			TileEntityBaseGenerator   = Class.forName("ic2.core.block.generator.tileentity.TileEntityBaseGenerator");
			TEBG_MaxStorage           = TileEntityBaseGenerator.getField("maxStorage");
			TEBG_Production           = TileEntityBaseGenerator.getField("production");			

			TileEntityElectricBlock   = Class.forName("ic2.core.block.wiring.TileEntityElectricBlock");
			TEEB_MaxStorage           = TileEntityElectricBlock.getField("maxStorage");	
			TEEB_Output               = TileEntityElectricBlock.getField("output");
			
			IEnergySink               = Class.forName("ic2.api.energy.tile.IEnergySink");			
			IES_GetMaxSafeInput       = IEnergySink.getMethod("getMaxSafeInput");
			
			IC2Items                  = Class.forName("ic2.core.Ic2Items");
			TransformerUpgrade        = IC2Items.getField("transformerUpgrade"); 
			EnergyStorageUpgrade      = IC2Items.getField("energyStorageUpgrade");
			OverclockerUpgrade        = IC2Items.getField("overclockerUpgrade");
			
			TransformerUpgradeStack   = (ItemStack)TransformerUpgrade.get(null);
			EnergyStorageUpgradeStack = (ItemStack)EnergyStorageUpgrade.get(null);			
			OverclockerUpgradeStack   = (ItemStack)OverclockerUpgrade.get(null);
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Class not found. " + e);
			return;
		} catch (NoSuchMethodException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Method not found." + e);
			return;			
		} catch (NoSuchFieldException e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Field not found." + e);
			return;			
		} catch (Exception e){
			mod_Waila.log.log(Level.WARNING, "[IC2] Unhandled exception." + e);
			return;			
		}	
		
		
		ExternalModulesHandler.instance().addConfigRemote("IndustrialCraft2", "ic2.storage");
		ExternalModulesHandler.instance().addConfig("IndustrialCraft2", "ic2.outputeu");
		ExternalModulesHandler.instance().addConfig("IndustrialCraft2", "ic2.inputeuother");		
		
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTEStandardMachine(), TileEntityStandardMachine);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTEBaseGenerator(),   TileEntityBaseGenerator);
		ExternalModulesHandler.instance().registerBodyProvider(new HUDHandlerTEElectricBlock(),   TileEntityElectricBlock);
	}

	/* Retuns the current stored energy if available in the nbt and config is true */
	public static double getStoredEnergy(IWailaDataAccessor accessor){
		if (ConfigHandler.instance().getConfig("ic2.storage")){
			if (accessor.getNBTData().hasKey("energy"))
				return accessor.getNBTData().getDouble("energy");
			else if (accessor.getNBTData().hasKey("storage"))
				return accessor.getNBTData().getDouble("storage");
		}
		return -1.0;
	}
	
	public static IC2Upgrades getUpgrades(IWailaDataAccessor accessor){
		
		NBTTagCompound nbt   = accessor.getNBTData();
		IC2Upgrades upgrades = new IC2Upgrades();
		
		if (!nbt.hasKey("InvSlots")) return null;
		
		NBTTagCompound inventory = nbt.getCompoundTag("InvSlots");
		if (!inventory.hasKey("upgrade")) return null;
		
		NBTTagList nbtupgrades = inventory.getCompoundTag("upgrade").getTagList("Contents");
		for (Object subobj : nbtupgrades.tagList){
			NBTTagCompound subtag = (NBTTagCompound)subobj;
			int id    = subtag.getShort("id");
			int meta  = subtag.getShort("Damage");
			int count = subtag.getByte("Count");
			
			//ItemStack is = new ItemStack(id, 1, count);
			if (TransformerUpgradeStack.getItemDamage()   == meta && TransformerUpgradeStack.itemID == id)
				upgrades.transform += count;
			if (EnergyStorageUpgradeStack.getItemDamage() == meta && EnergyStorageUpgradeStack.itemID == id)
				upgrades.storage += count;
			if (OverclockerUpgradeStack.getItemDamage()   == meta && OverclockerUpgradeStack.itemID == id)
				upgrades.overclocker += count;			
		}
		
		return upgrades;
	}
	
	
	/*
	  public void setOverclockRates()
	  {
	    int extraProcessTime = 0;
	    double processTimeMultiplier = 1.0D;
	    int extraEnergyDemand = 0;
	    double energyDemandMultiplier = 1.0D;
	    int extraEnergyStorage = 0;
	    double energyStorageMultiplier = 1.0D;
	    int extraTier = 0;

	    for (int i = 0; i < this.upgradeSlot.size(); i++) {
	      ItemStack stack = this.upgradeSlot.get(i);

	      if ((stack != null) && ((stack.func_77973_b() instanceof IUpgradeItem)))
	      {
	        IUpgradeItem upgrade = (IUpgradeItem)stack.func_77973_b();

	        extraProcessTime += upgrade.getExtraProcessTime(stack, this) * stack.field_77994_a;
	        processTimeMultiplier *= Math.pow(upgrade.getProcessTimeMultiplier(stack, this), stack.field_77994_a);
	        extraEnergyDemand += upgrade.getExtraEnergyDemand(stack, this) * stack.field_77994_a;
	        energyDemandMultiplier *= Math.pow(upgrade.getEnergyDemandMultiplier(stack, this), stack.field_77994_a);
	        extraEnergyStorage += upgrade.getExtraEnergyStorage(stack, this) * stack.field_77994_a;
	        energyStorageMultiplier *= Math.pow(upgrade.getEnergyStorageMultiplier(stack, this), stack.field_77994_a);
	        extraTier += upgrade.getExtraTier(stack, this) * stack.field_77994_a;
	      }
	    }
	    double previousProgress = this.progress / this.operationLength;

	    double stackOpLen = (this.defaultOperationLength + extraProcessTime) * 64.0D * processTimeMultiplier;
	    this.operationsPerTick = ((int)Math.min(Math.ceil(64.0D / stackOpLen), 2147483647.0D));
	    this.operationLength = ((int)Math.round(stackOpLen * this.operationsPerTick / 64.0D));

	    this.energyConsume = applyModifier(this.defaultEnergyConsume, extraEnergyDemand, energyDemandMultiplier);
	    setTier(applyModifier(this.defaultTier, extraTier, 1.0D));
	    this.maxEnergy = applyModifier(this.defaultEnergyStorage, extraEnergyStorage + this.operationLength * this.energyConsume, energyStorageMultiplier);

	    if (this.operationLength < 1) this.operationLength = 1;

	    this.progress = ((short)(int)Math.floor(previousProgress * this.operationLength + 0.1D));
	  }	
	*/
	
}
