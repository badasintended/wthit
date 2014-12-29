package mcp.mobius.waila.overlay;

import static mcp.mobius.waila.api.SpecialChars.*;

import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;

public class DisplayUtil {
	public  static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

	
	 public static int getDisplayWidth(String s) {
		 if (s == null || s.equals(""))
			 return 0;
		 
		 int width = 0;
		 
		 Matcher renderMatcher = patternRender.matcher(s);
		 while (renderMatcher.find()){
			 IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(renderMatcher.group("name"));
			 if (renderer != null)
				 width += renderer.getSize(renderMatcher.group("args").split(","), DataAccessorBlock.instance).width;
		 }
		 
		 width += fontRenderer.getStringWidth(stripSymbols(s));
		 return width;
	 }
	 
	 public static Dimension displaySize() {
		 Minecraft mc = Minecraft.getMinecraft();
		 ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		 return new Dimension(res.getScaledWidth(), res.getScaledHeight());
	 }
	 
	 public static String stripSymbols(String s){
		 String result = patternRender.matcher(s).replaceAll("");
		 result = patternMinecraft.matcher(result).replaceAll("");
		 result = patternWaila.matcher(result).replaceAll("");
		 return result;
	 }
	 
	 public static String stripWailaSymbols(String s){
		 String result = patternRender.matcher(s).replaceAll("");
		 result = patternWaila.matcher(result).replaceAll("");
		 return result;
	 }	 
	 
	 /*
	 protected void drawGradientRect(int p_73733_1_, int p_73733_2_, int p_73733_3_, int p_73733_4_, int p_73733_5_, int p_73733_6_)
	 {
		 float f = (float)(p_73733_5_ >> 24 & 255) / 255.0F;
		 float f1 = (float)(p_73733_5_ >> 16 & 255) / 255.0F;
		 float f2 = (float)(p_73733_5_ >> 8 & 255) / 255.0F;
		 float f3 = (float)(p_73733_5_ & 255) / 255.0F;
		 float f4 = (float)(p_73733_6_ >> 24 & 255) / 255.0F;
		 float f5 = (float)(p_73733_6_ >> 16 & 255) / 255.0F;
		 float f6 = (float)(p_73733_6_ >> 8 & 255) / 255.0F;
		 float f7 = (float)(p_73733_6_ & 255) / 255.0F;
		 GL11.glDisable(GL11.GL_TEXTURE_2D);
		 GL11.glEnable(GL11.GL_BLEND);
		 GL11.glDisable(GL11.GL_ALPHA_TEST);
		 OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		 GL11.glShadeModel(GL11.GL_SMOOTH);
		 Tessellator tessellator = Tessellator.instance;
		 tessellator.startDrawingQuads();
		 tessellator.setColorRGBA_F(f1, f2, f3, f);
		 tessellator.addVertex((double)p_73733_3_, (double)p_73733_2_, (double)this.zLevel);
		 tessellator.addVertex((double)p_73733_1_, (double)p_73733_2_, (double)this.zLevel);
		 tessellator.setColorRGBA_F(f5, f6, f7, f4);
		 tessellator.addVertex((double)p_73733_1_, (double)p_73733_4_, (double)this.zLevel);
		 tessellator.addVertex((double)p_73733_3_, (double)p_73733_4_, (double)this.zLevel);
		 tessellator.draw();
		 GL11.glShadeModel(GL11.GL_FLAT);
		 GL11.glDisable(GL11.GL_BLEND);
		 GL11.glEnable(GL11.GL_ALPHA_TEST);
		 GL11.glEnable(GL11.GL_TEXTURE_2D);
	 }
	 */	 
}
