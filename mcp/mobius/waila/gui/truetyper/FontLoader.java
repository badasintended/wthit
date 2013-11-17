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

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;



import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontLoader{
	
	public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias){
	     return loadSystemFont(name, defSize, antialias, Font.TRUETYPE_FONT);
	    
	}

	public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias, int type){
	     Font font;
	     TrueTypeFont out = null;
	     try {
	             font = new Font(name, type, (int)defSize);
	             font = font.deriveFont(defSize);
	             out = new TrueTypeFont(font, antialias);
	     } catch (Exception e) {
	             e.printStackTrace();
	     }
	     return out;
	}      

	public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias){
	     return createFont(res, defSize, antialias, Font.TRUETYPE_FONT);
	}

	public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias, int type){
	     Font font;
	     TrueTypeFont out = null;
	     try {
	             font = Font.createFont(type, Minecraft.getMinecraft().func_110442_L().func_110536_a(res).func_110527_b());
	             font = font.deriveFont(defSize);
	             out = new TrueTypeFont(font, antialias);
	     } catch (Exception e) {
	             e.printStackTrace();
	     }
	     return out;
	}
	
}