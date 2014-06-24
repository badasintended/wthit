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
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import net.minecraft.util.EnumChatFormatting;

public class Test {

	public static ArrayList<TrueTypeFont> testFonts = new ArrayList<TrueTypeFont>();
	public static float[] white = {1f,1f,1f,1f};
	public static boolean init = false;
	public static void initTest(){
		return;
		/*
		 GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		 Font[] fonts = e.getAllFonts();
		 {
			 int max = 9;
			 for(Font f : fonts){
				 boolean pass = true;
				 for(TrueTypeFont ttf : testFonts){
					 if(ttf.font.getFontName().startsWith(f.getFontName().substring(0, 1))){
						 pass = false;
						 continue;
					 }
				 }
				 if(pass){
					 max--;
					 if(max == 0){
						 break;
					 }
					 testFonts.add(FontLoader.loadSystemFont(f.getFontName(), 24f, false)); 
					 testFonts.add(FontLoader.loadSystemFont(f.getFontName(), 24f, true));
				 }
			 }
		 }
		 init = true;
		  
	}
	
	public static void doTest(){
		if(!init){
			initTest();
		}
		for(int i =0; i < testFonts.size(); i++){
			int y = 10+i*12;
			int x = 10;	
			FontHelper.drawString("He"+EnumChatFormatting.GOLD+"llo "+EnumChatFormatting.GREEN+"wor"+EnumChatFormatting.YELLOW+"ld", x, y, testFonts.get(i), 1f, 1f);
		}*/
	}
}
