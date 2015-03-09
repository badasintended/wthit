package mcp.mobius.waila.overlay;
import static mcp.mobius.waila.api.SpecialChars.*;

import java.util.HashMap;

public enum IconUI {
HEART (52, 0, 9, 9, 52, 9, 9, 9, "a"),
HHEART(61, 0, 9, 9, 52, 9, 9, 9, "b"),
EHEART(52, 9, 9, 9, "c"),
BUBBLEEXP(25, 18, 9, 9, "x");

	private final static HashMap<String, IconUI> lk = new HashMap<String, IconUI>();
	static {
		for (IconUI icon : IconUI.values()){
			lk.put(icon.symbol, icon);
		}
	}

    public final int u, v, su, sv;
    public final int bu, bv, bsu, bsv;
    public final String symbol;
    
	private IconUI(int u, int v, int su, int sv, String symbol){
		this(u,v,su,sv,-1,-1,-1,-1,symbol);
	}

	private IconUI(int u, int v, int su, int sv, int bu, int bv, int bsu, int bsv, String symbol){
		this.u = u;
		this.v = v;
		this.su = su;
		this.sv = sv;
		this.bu  = bu;
		this.bv  = bv;
		this.bsu = bsu;
		this.bsv = bsv;
		this.symbol = WailaStyle + WailaIcon + symbol;
	}	
	
	public static IconUI bySymbol(String s){
		IconUI iconUI = lk.get(s);
		if (iconUI == null){
			return lk.get("x");
		}
		else {
			return lk.get(s);
		}
	}
	
}
