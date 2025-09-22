package mcp.mobius.waila.mixin;

import java.util.List;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccess {

    @Accessor("children")
    <T> List<T> wthit_children();

    @Invoker("repositionEntries")
    void wthit_repositionEntries();

}
