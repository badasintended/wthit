package mcp.mobius.waila.api;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class RenderableTextComponent extends TextComponentString {

    public RenderableTextComponent(ResourceLocation id, NBTTagCompound data) {
        super(getRenderString(id, data));
    }

    public RenderableTextComponent(RenderableTextComponent... components) {
        super(getRenderString(components));
    }

    public List<RenderContainer> getRenderers() {
        List<RenderContainer> renderers = Lists.newArrayList();
        NBTTagCompound data = getData();
        if (data.contains("renders")) {
            NBTTagList list = data.getList("renders", Constants.NBT.TAG_STRING);
            list.forEach(t -> {
                NBTTagString stringTag = (NBTTagString) t;
                try {
                    NBTTagCompound tag = JsonToNBT.getTagFromJson(stringTag.getString());
                    ResourceLocation id = new ResourceLocation(tag.getString("id"));
                    NBTTagCompound dataTag = tag.getCompound("data");
                    renderers.add(new RenderContainer(id, dataTag));
                } catch (CommandSyntaxException e) {
                    // no-op
                }
            });
        } else {
            ResourceLocation id = new ResourceLocation(data.getString("id"));
            NBTTagCompound dataTag = data.getCompound("data");
            renderers.add(new RenderContainer(id, dataTag));
        }

        return renderers;
    }

    private NBTTagCompound getData() {
        try {
            return JsonToNBT.getTagFromJson(getFormattedText());
        } catch (CommandSyntaxException e) {
            return new NBTTagCompound();
        }
    }

    private static String getRenderString(ResourceLocation id, NBTTagCompound data) {
        NBTTagCompound renderData = new NBTTagCompound();
        renderData.putString("id", id.toString());
        renderData.put("data", data);
        return renderData.toString();
    }

    private static String getRenderString(RenderableTextComponent... components) {
        NBTTagCompound container = new NBTTagCompound();
        NBTTagList renderData = new NBTTagList();
        for (RenderableTextComponent component : components)
            renderData.add(new NBTTagString(component.getFormattedText()));
        container.put("renders", renderData);
        return container.toString();
    }

    public static class RenderContainer {
        private final ResourceLocation id;
        private final NBTTagCompound data;
        private final ITooltipRenderer renderer;

        public RenderContainer(ResourceLocation id, NBTTagCompound data) {
            this.id = id;
            this.data = data;
            this.renderer = WailaRegistrar.INSTANCE.getTooltipRenderer(id);
        }

        public ResourceLocation getId() {
            return id;
        }

        public NBTTagCompound getData() {
            return data;
        }

        public ITooltipRenderer getRenderer() {
            return renderer;
        }
    }
}
