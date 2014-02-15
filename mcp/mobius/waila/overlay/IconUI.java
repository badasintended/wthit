package mcp.mobius.waila.overlay;
import static mcp.mobius.waila.SpecialChars.*;

import java.util.HashMap;

public enum IconUI {
HEART (52, 0, 9, 9, 52, 9, 9, 9, "a"),
HHEART(61, 0, 9, 9, 52, 9, 9, 9, "b"),
EHEART(52, 9, 9, 9, "c");

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
		this.symbol = WailaStyle + symbol + WailaStyleEnd;
	}	
	
	public static IconUI bySymbol(String s){
		return lk.get(s);
	}
	
}
