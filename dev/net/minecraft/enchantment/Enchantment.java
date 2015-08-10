package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class Enchantment
{
    public static final Enchantment[] enchantmentsList = new Enchantment[256];
    public static final Enchantment[] enchantmentsBookList;
    private static final Map locationEnchantments = Maps.newHashMap();
    public static final Enchantment protection = new EnchantmentProtection(0, new ResourceLocation("protection"), 10, 0);
    /** Protection against fire */
    public static final Enchantment fireProtection = new EnchantmentProtection(1, new ResourceLocation("fire_protection"), 5, 1);
    public static final Enchantment featherFalling = new EnchantmentProtection(2, new ResourceLocation("feather_falling"), 5, 2);
    /** Protection against explosions */
    public static final Enchantment blastProtection = new EnchantmentProtection(3, new ResourceLocation("blast_protection"), 2, 3);
    public static final Enchantment projectileProtection = new EnchantmentProtection(4, new ResourceLocation("projectile_protection"), 5, 4);
    public static final Enchantment respiration = new EnchantmentOxygen(5, new ResourceLocation("respiration"), 2);
    /** Increases underwater mining rate */
    public static final Enchantment aquaAffinity = new EnchantmentWaterWorker(6, new ResourceLocation("aqua_affinity"), 2);
    public static final Enchantment thorns = new EnchantmentThorns(7, new ResourceLocation("thorns"), 1);
    public static final Enchantment depthStrider = new EnchantmentWaterWalker(8, new ResourceLocation("depth_strider"), 2);
    public static final Enchantment sharpness = new EnchantmentDamage(16, new ResourceLocation("sharpness"), 10, 0);
    public static final Enchantment smite = new EnchantmentDamage(17, new ResourceLocation("smite"), 5, 1);
    public static final Enchantment baneOfArthropods = new EnchantmentDamage(18, new ResourceLocation("bane_of_arthropods"), 5, 2);
    public static final Enchantment knockback = new EnchantmentKnockback(19, new ResourceLocation("knockback"), 5);
    /** Lights mobs on fire */
    public static final Enchantment fireAspect = new EnchantmentFireAspect(20, new ResourceLocation("fire_aspect"), 2);
    /** Mobs have a chance to drop more loot */
    public static final Enchantment looting = new EnchantmentLootBonus(21, new ResourceLocation("looting"), 2, EnumEnchantmentType.WEAPON);
    /** Faster resource gathering while in use */
    public static final Enchantment efficiency = new EnchantmentDigging(32, new ResourceLocation("efficiency"), 10);
    /**
     * Blocks mined will drop themselves, even if it should drop something else (e.g. stone will drop stone, not
     * cobblestone)
     */
    public static final Enchantment silkTouch = new EnchantmentUntouching(33, new ResourceLocation("silk_touch"), 1);
    /** Sometimes, the tool's durability will not be spent when the tool is used */
    public static final Enchantment unbreaking = new EnchantmentDurability(34, new ResourceLocation("unbreaking"), 5);
    /** Can multiply the drop rate of items from blocks */
    public static final Enchantment fortune = new EnchantmentLootBonus(35, new ResourceLocation("fortune"), 2, EnumEnchantmentType.DIGGER);
    /** Power enchantment for bows, add's extra damage to arrows. */
    public static final Enchantment power = new EnchantmentArrowDamage(48, new ResourceLocation("power"), 10);
    /** Knockback enchantments for bows, the arrows will knockback the target when hit. */
    public static final Enchantment punch = new EnchantmentArrowKnockback(49, new ResourceLocation("punch"), 2);
    /** Flame enchantment for bows. Arrows fired by the bow will be on fire. Any target hit will also set on fire. */
    public static final Enchantment flame = new EnchantmentArrowFire(50, new ResourceLocation("flame"), 2);
    /**
     * Infinity enchantment for bows. The bow will not consume arrows anymore, but will still required at least one
     * arrow on inventory use the bow.
     */
    public static final Enchantment infinity = new EnchantmentArrowInfinite(51, new ResourceLocation("infinity"), 1);
    public static final Enchantment luckOfTheSea = new EnchantmentLootBonus(61, new ResourceLocation("luck_of_the_sea"), 2, EnumEnchantmentType.FISHING_ROD);
    public static final Enchantment lure = new EnchantmentFishingSpeed(62, new ResourceLocation("lure"), 2, EnumEnchantmentType.FISHING_ROD);
    public final int effectId;
    private final int weight;
    /** The EnumEnchantmentType given to this Enchantment. */
    public EnumEnchantmentType type;
    /** Used in localisation and stats. */
    protected String name;
    private static final String __OBFID = "CL_00000105";

    /**
     * Retrieves an Enchantment from the enchantmentsList
     *  
     * @param enchID The numeric ID, used to represent an enchantment.
     */
    public static Enchantment getEnchantmentById(int enchID)
    {
        return enchID >= 0 && enchID < enchantmentsList.length ? enchantmentsList[enchID] : null;
    }

    protected Enchantment(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType)
    {
        this.effectId = enchID;
        this.weight = enchWeight;
        this.type = enchType;

        if (enchantmentsList[enchID] != null)
        {
            throw new IllegalArgumentException("Duplicate enchantment id! " + this.getClass() + " and " + enchantmentsList[enchID].getClass() + " Enchantment ID:" + enchID);
        }
        else
        {
            enchantmentsList[enchID] = this;
            locationEnchantments.put(enchName, this);
        }
    }

    /**
     * Retrieves an enchantment by using its location name.
     */
    public static Enchantment getEnchantmentByLocation(String location)
    {
        return (Enchantment)locationEnchantments.get(new ResourceLocation(location));
    }

    /**
     * Generates an array of strings, which represent a list of all the enchantment ResourceLocation's.
     */
    public static String[] getLocationNames()
    {
        String[] astring = new String[locationEnchantments.size()];
        int i = 0;
        ResourceLocation resourcelocation;

        for (Iterator iterator = locationEnchantments.keySet().iterator(); iterator.hasNext(); astring[i++] = resourcelocation.toString())
        {
            resourcelocation = (ResourceLocation)iterator.next();
        }

        return astring;
    }

    /**
     * Retrieves the weight value of an Enchantment. This weight value is used within vanilla to determine how rare an
     * enchantment is.
     */
    public int getWeight()
    {
        return this.weight;
    }

    /**
     * Returns the minimum level that the enchantment can have.
     */
    public int getMinLevel()
    {
        return 1;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 1 + enchantmentLevel * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 5;
    }

    /**
     * Calculates the damage protection of the enchantment based on level and damage source passed.
     *  
     * @param level The level of this enchantment.
     * @param source The source of the damage.
     */
    public int calcModifierDamage(int level, DamageSource source)
    {
        return 0;
    }

    /**
     * Calculates the additional damage that will be dealt by an item with this enchantment. This alternative to
     * calcModifierDamage is sensitive to the targets EnumCreatureAttribute.
     *  
     * @param level The level of this specific enchantment.
     * @param creatureType The EnumCreatureAttribute which represents the target entity. This can be used to have an
     * effect only apply to a specific group of creatures such as Undead or Arthropods.
     */
    public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
        return 0.0F;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     *  
     * @param ench A possible enchantment that may be applied along side this enchantment, depending on the results.
     */
    public boolean canApplyTogether(Enchantment ench)
    {
        return this != ench;
    }

    /**
     * Sets the enchantment name
     *  
     * @param enchName The simple name for this enchantment.
     */
    public Enchantment setName(String enchName)
    {
        this.name = enchName;
        return this;
    }

    /**
     * Return the name of key in translation table of this enchantment.
     */
    public String getName()
    {
        return "enchantment." + this.name;
    }

    /**
     * Returns the correct traslated name of the enchantment and the level in roman numbers.
     *  
     * @param level The level of this enchantment, used to create a roman numeral representation of the enchantments
     * tier.
     */
    public String getTranslatedName(int level)
    {
        String s = StatCollector.translateToLocal(this.getName());
        return s + " " + StatCollector.translateToLocal("enchantment.level." + level);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     *  
     * @param stack The ItemStack that is attempting to become enchanted with with enchantment.
     */
    public boolean canApply(ItemStack stack)
    {
        return canApplyAtEnchantingTable(stack);
    }

    /**
     * Called whenever a mob is damaged with an item that has this enchantment on it.
     *  
     * @param user An instance of the entity which used the enchantment. This is normally an EntityPlayer.
     * @param target An instance of the damaged entity.
     * @param level The level of the enchantment used.
     */
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {}

    /**
     * Whenever an entity that has this enchantment on one of its associated items is damaged this method will be
     * called.
     *  
     * @param user An instance of the entity that is associated with this enchantment.
     * @param attacker An instance of the entity that has attacked the using entity.
     * @param level The level of the enchantment used.
     */
    public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {}

    /**
     * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     * @param stack
     * @return
     */
    public boolean canApplyAtEnchantingTable(ItemStack stack)
    {
        return this.type.canEnchantItem(stack.getItem());
    }

    private static final java.lang.reflect.Field bookSetter = Enchantment.class.getDeclaredFields()[1];
    /**
     * Add to the list of enchantments applicable by the anvil from a book
     *
     * @param enchantment
     */
    public static void addToBookList(Enchantment enchantment)
    {
        try
        {
            net.minecraftforge.common.util.EnumHelper.setFailsafeFieldValue(bookSetter, null,
                com.google.common.collect.ObjectArrays.concat(enchantmentsBookList, enchantment));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e); //Rethrow see what happens
        }
    }

    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
    public boolean isAllowedOnBooks()
    {
        return true;
    }

    static
    {
        ArrayList var0 = Lists.newArrayList();
        Enchantment[] var1 = enchantmentsList;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            Enchantment var4 = var1[var3];

            if (var4 != null)
            {
                var0.add(var4);
            }
        }

        enchantmentsBookList = (Enchantment[])var0.toArray(new Enchantment[var0.size()]);
    }
}