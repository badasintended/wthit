package mcp.mobius.waila.utils;

import java.util.HashSet;
import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTUtil {

	public static NBTBase getTag(String key, NBTTagCompound tag){
		String[] path = key.split("\\.");
		
		NBTTagCompound deepTag = tag;
		for (String i : path){
			if (deepTag.hasKey(i)){
				if (deepTag.getTag(i) instanceof NBTTagCompound)
					deepTag = deepTag.getCompoundTag(i);
				else{
					return deepTag.getTag(i);
				}
			} else {
				Waila.log.log(Level.WARNING, "Leaf " + key + " not found.");
				return null;
			}
		}
		return deepTag;
	}
	
	public static NBTTagCompound setTag(String key, NBTTagCompound targetTag, NBTBase addedTag){
		String[] path = key.split("\\.");
		
		NBTTagCompound deepTag = targetTag;
		for (int i = 0; i < path.length - 1; i++){
			if (!deepTag.hasKey(path[i]))
				deepTag.setCompoundTag(path[i], new NBTTagCompound());
			
			deepTag = deepTag.getCompoundTag(path[i]);
		}
		
		deepTag.setTag(path[path.length - 1], addedTag);		
		
		return targetTag;
	}
	
	public static NBTTagCompound createTag(NBTTagCompound inTag, HashSet<String> keys){
		if (keys.contains("*")) return inTag;
		
		NBTTagCompound outTag = new NBTTagCompound();
		
		for (String key : keys){
			NBTBase tagToAdd = getTag(key, inTag);
			//System.out.printf("%s\n", tagToAdd);
			if (tagToAdd != null)
				outTag = setTag(key, outTag, tagToAdd);
		}
		
		return outTag;
	}
	
}
