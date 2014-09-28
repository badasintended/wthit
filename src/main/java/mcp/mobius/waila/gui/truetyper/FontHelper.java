package mcp.mobius.waila.gui.truetyper;

/**
*	TrueTyper: Open Source TTF implementation for Minecraft.
*	Copyright (C) 2013 - Mr_okushama
* 
*    	 This program is free software: you can redistribute it and/or modify
*    	 it under the terms of the GNU General Public License as published by
*    	 the Free Software Foundation, either version 3 of the License, or
*    	 (at your option) any later version.
* 
*    	 This program is distributed in the hope that it will be useful,
*    	 but WITHOUT ANY WARRANTY; without even the implied warranty of
*    	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    	 GNU General Public License for more details.
* 
*    	 You should have received a copy of the GNU General Public License
*     	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class FontHelper {
	
	private static String formatEscape = "\u00A7";


	public static void drawString(String s, float x, float y, TrueTypeFont font, float scaleX, float scaleY, float... rgba){
		drawString(s,x,y,font,scaleX,scaleY,0,rgba);

	}

	public static void drawString(String s, float x, float y, TrueTypeFont font, float scaleX, float scaleY, float rotationZ, float... rgba){
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		if(mc.gameSettings.hideGUI){
			return;
		}
		int amt = 1;
		if(sr.getScaleFactor() == 1){
			amt = 2;
		}
		
		FloatBuffer matrixData = BufferUtils.createFloatBuffer(16);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, matrixData);
		FontHelper.set2DMode(matrixData);
		GL11.glPushMatrix();
		y = mc.displayHeight-(y*sr.getScaleFactor())-(((font.getLineHeight()/amt)));
		float tx = (x*sr.getScaleFactor())+(font.getWidth(s)/2);
		float tranx = tx+2;
		float trany = y+(font.getLineHeight()/2);
		GL11.glTranslatef(tranx,trany,0);
		GL11.glRotatef(rotationZ, 0f, 0f, 1f);
		GL11.glTranslatef(-tranx,-trany,0);

	
		GL11.glEnable(GL11.GL_BLEND);
		if(s.contains(formatEscape)){
			String[] pars = s.split(formatEscape);
			float totalOffset = 0;
			for(int i = 0; i < pars.length; i++){
				String par = pars[i];
					float[] c = rgba;
					if(i > 0){
						c = Formatter.getFormatted(par.charAt(0));
						par = par.substring(1, par.length());
					}
					font.drawString((x*sr.getScaleFactor()+totalOffset), y, par,  scaleX/amt, scaleY/amt, c);
					totalOffset += font.getWidth(par);
			}
		}else{
			font.drawString((x*sr.getScaleFactor()), y, s, scaleX/amt, scaleY/amt, rgba);	
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		FontHelper.set3DMode();
	}

	private static void set2DMode(FloatBuffer matrixData) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		mc.entityRenderer.setupOverlayRendering();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glPushMatrix(); 
		//GL11.glLoadMatrix(matrixData);

	    GL11.glLoadIdentity();
	    GL11.glOrtho(0, mc.displayWidth, 0, mc.displayHeight, -1, 1);
	   	GL11.glMatrixMode(GL11.GL_MODELVIEW); 
	    GL11.glPushMatrix();
	    GL11.glLoadIdentity();
	    
	  	Matrix4f matrix = new Matrix4f();
		matrix.load(matrixData);
		GL11.glTranslatef(matrix.m30*sr.getScaleFactor(),-matrix.m31*sr.getScaleFactor(), 0f);

	}

	private static void set3DMode() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW); 
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix(); 
		Minecraft mc = Minecraft.getMinecraft();
		mc.entityRenderer.setupOverlayRendering();
	}

}
