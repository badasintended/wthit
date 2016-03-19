package mcp.mobius.waila.gui.truetyper;

import java.util.ArrayList;

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
