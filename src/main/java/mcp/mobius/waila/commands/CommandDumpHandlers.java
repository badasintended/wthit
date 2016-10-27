package mcp.mobius.waila.commands;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.TagLocation;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

public class CommandDumpHandlers extends CommandBase {

    @Override
    public String getCommandName() {
        return "dumphandlers";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
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
            Waila.log.info("Printed handler dump to {}", dumpFile.getAbsolutePath());
        } catch (Exception e) {
            Waila.log.error("Error dumping handlers to file. Falling back to log.");
            Waila.log.error("\n" + getHandlerDump());
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    public static String getHandlerDump() {
        String toPrint = "# Waila Handler Dump\n\n";

        // Block/TileEntity handlers
        for (TagLocation location : TagLocation.values()) {
            if (location == TagLocation.OVERRIDE)
                continue;

            toPrint += "### " + location.name() + " BLOCK PROVIDERS\n";
            if (!ModuleRegistrar.instance().blockProviders.get(location).isEmpty()) {
                for (Map.Entry<Class, ArrayList<IWailaDataProvider>> data : ModuleRegistrar.instance().blockProviders.get(location).entrySet()) {
                    toPrint += String.format("* %s\n", data.getKey().getName());
                    for (IWailaDataProvider provider : data.getValue())
                        toPrint += String.format("\t* %s\n", provider.getClass().getName());
                    toPrint += "\n";
                }
            } else toPrint += "\n";
        }

        // Entity handlers
        for (TagLocation location : TagLocation.values()) {
            if (location == TagLocation.STACK)
                continue;

            toPrint += "### " + location.name() + " ENTITY PROVIDERS\n";
            if (!ModuleRegistrar.instance().entityProviders.get(location).isEmpty()) {
                for (Map.Entry<Class, ArrayList<IWailaEntityProvider>> data : ModuleRegistrar.instance().entityProviders.get(location).entrySet()) {
                    toPrint += String.format("* %s\n", data.getKey().getName());
                    for (IWailaEntityProvider provider : data.getValue())
                        toPrint += String.format("\t* %s\n", provider.getClass().getName());
                    toPrint += "\n";
                }
            } else toPrint += "\n";
        }

        return toPrint;
    }
}
