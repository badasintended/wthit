package mcp.mobius.waila.utils;

import net.minecraft.nbt.*;

import java.util.Set;

public class NBTUtil {

    public static NBTBase getTag(String key, NBTTagCompound tag) {
        String[] path = key.split("\\.");

        NBTTagCompound deepTag = tag;
        for (String i : path) {
            if (deepTag.hasKey(i)) {
                if (deepTag.getTag(i) instanceof NBTTagCompound)
                    deepTag = deepTag.getCompoundTag(i);
                else {
                    return deepTag.getTag(i);
                }
            } else {
                return null;
            }
        }
        return deepTag;
    }

    public static NBTTagCompound setTag(String key, NBTTagCompound targetTag, NBTBase addedTag) {
        String[] path = key.split("\\.");

        NBTTagCompound deepTag = targetTag;
        for (int i = 0; i < path.length - 1; i++) {
            if (!deepTag.hasKey(path[i]))
                deepTag.setTag(path[i], new NBTTagCompound());

            deepTag = deepTag.getCompoundTag(path[i]);
        }

        deepTag.setTag(path[path.length - 1], addedTag);

        return targetTag;
    }

    public static NBTTagCompound createTag(NBTTagCompound inTag, Set<String> keys) {
        if (keys.contains("*")) return inTag;

        NBTTagCompound outTag = new NBTTagCompound();

        for (String key : keys) {
            NBTBase tagToAdd = getTag(key, inTag);
            if (tagToAdd != null)
                outTag = setTag(key, outTag, tagToAdd);
        }

        return outTag;
    }

    public static int getNBTInteger(NBTTagCompound tag, String keyname) {
        NBTBase subtag = tag.getTag(keyname);
        if (subtag instanceof NBTTagInt)
            return tag.getInteger(keyname);
        if (subtag instanceof NBTTagShort)
            return tag.getShort(keyname);
        if (subtag instanceof NBTTagByte)
            return tag.getByte(keyname);

        return 0;
    }
}
