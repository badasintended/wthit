package mcp.mobius.waila.commands;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileWriter;

public class CommandDumpHandlers extends CommandBase {

    @Override
    public String getName() {
        return "dumphandlers";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_) {
        return "/dumphandlers";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            File dumpFile = new File("WailaHandlerDump.md");
            if (dumpFile.exists())
                dumpFile.delete();
            FileWriter writer = new FileWriter(dumpFile);
            writer.write(getHandlerDump());
            writer.close();
            Waila.LOGGER.info("Printed handler dump to {}", dumpFile.getAbsolutePath());
        } catch (Exception e) {
            Waila.LOGGER.error("Error dumping handlers to file. Falling back to log.");
            Waila.LOGGER.error("\n" + getHandlerDump());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    public static String getHandlerDump() {
        String toPrint = "# Waila Handler Dump\n\n";
        toPrint += "### HEAD BLOCK PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().headBlockProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaDataProvider provider : ModuleRegistrar.instance().headBlockProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### BODY BLOCK PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().bodyBlockProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaDataProvider provider : ModuleRegistrar.instance().bodyBlockProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### TAIL BLOCK PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().tailBlockProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaDataProvider provider : ModuleRegistrar.instance().tailBlockProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### STACK BLOCK PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().stackBlockProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaDataProvider provider : ModuleRegistrar.instance().stackBlockProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### NBT BLOCK PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().NBTDataProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaDataProvider provider : ModuleRegistrar.instance().NBTDataProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### HEAD ENTITY PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().headEntityProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaEntityProvider provider : ModuleRegistrar.instance().headEntityProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### BODY ENTITY PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().bodyEntityProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaEntityProvider provider : ModuleRegistrar.instance().bodyEntityProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### TAIL ENTITY PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().tailEntityProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaEntityProvider provider : ModuleRegistrar.instance().tailEntityProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### STACK ENTITY PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().overrideEntityProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaEntityProvider provider : ModuleRegistrar.instance().overrideEntityProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }

        toPrint += "\n\n### NBT ENTITY PROVIDERS\n";
        for (Class clazz : ModuleRegistrar.instance().NBTEntityProviders.keySet()) {
            toPrint += String.format("* %s\n", clazz.getName());
            for (IWailaEntityProvider provider : ModuleRegistrar.instance().NBTEntityProviders.get(clazz)) {
                toPrint += String.format("  * %s\n", provider.getClass().getName());
            }
            toPrint += "\n";
        }
        return toPrint;
    }
}
