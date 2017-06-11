package mcp.mobius.waila.utils;

import mcp.mobius.waila.Waila;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.lang.reflect.Field;

public class AccessHelper {
    public static Field getDeclaredField(String classname, String fieldname) {

        try {
            Class class_ = Class.forName(classname);
            Field field_ = class_.getDeclaredField(fieldname);
            field_.setAccessible(true);
            //mod_Waila.log.fine(String.format("++ Found field %s %s\n", classname, fieldname));
            return field_;
        } catch (NoSuchFieldException e) {
            Waila.LOGGER.warn(String.format("== Field %s %s not found !\n", classname, fieldname));
            return null;
        } catch (SecurityException e) {
            Waila.LOGGER.warn(String.format("== Field %s %s security exception !\n", classname, fieldname));
            return null;
        } catch (ClassNotFoundException e) {
            Waila.LOGGER.warn(String.format("== Class %s not found !\n", classname));
            return null;
        }
    }

    public static Object getField(String classname, String fieldname, Object instance) {

        try {
            Class class_ = Class.forName(classname);
            Field field_ = class_.getDeclaredField(fieldname);
            field_.setAccessible(true);

            //mod_Waila.log.fine(String.format("++ Found field %s %s\n", classname, fieldname));
            return field_.get(instance);
        } catch (NoSuchFieldException e) {
            Waila.LOGGER.warn(String.format("== Field %s %s not found !\n", classname, fieldname));
            return null;
        } catch (SecurityException e) {
            Waila.LOGGER.warn(String.format("== Field %s %s security exception !\n", classname, fieldname));
            return null;
        } catch (ClassNotFoundException e) {
            Waila.LOGGER.warn(String.format("== Class %s not found !\n", classname));
            return null;
        } catch (IllegalArgumentException e) {
            Waila.LOGGER.warn(String.format("== %s\n", e));
            return null;
        } catch (IllegalAccessException e) {
            Waila.LOGGER.warn(String.format("== %s\n", e));
            return null;
        }
    }


    public static Object getFieldExcept(String classname, String fieldname, Object instance) throws Exception {
        Class class_ = Class.forName(classname);
        Field field_ = class_.getDeclaredField(fieldname);
        field_.setAccessible(true);

        //mod_Waila.log.fine(String.format("++ Found field %s %s\n", classname, fieldname));
        return field_.get(instance);
    }


    public static Block getBlock(String classname, String fieldname) {
        Field field_ = getDeclaredField(classname, fieldname);
        try {
            return (Block) field_.get(Block.class);
        } catch (Exception e) {
            System.out.printf("%s\n", e);
            Waila.LOGGER.warn(String.format("== ERROR GETTING BLOCK %s %s\n", classname, fieldname));
            return null;
        }
    }

    public static Item getItem(String classname, String fieldname) {
        Field field_ = getDeclaredField(classname, fieldname);
        try {
            return (Item) field_.get(Item.class);
        } catch (Exception e) {
            System.out.printf("%s\n", e);
            Waila.LOGGER.warn(String.format("== ERROR GETTING ITEM %s %s\n", classname, fieldname));
            return null;
        }
    }
}
