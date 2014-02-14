package mcp.mobius.waila.overlay;

public enum IconUI {
HEART(52, 0, 9, 9, 52, 9, 9, 9),
HHEART(61, 0, 9, 9, 52, 9, 9, 9);

    public final int u, v, su, sv;
    public final int bu, bv, bsu, bsv;
	private IconUI(int u, int v, int su, int sv){
		this.u = u;
		this.v = v;
		this.su = su;
		this.sv = sv;
		this.bu  = -1;
		this.bv  = -1;
		this.bsu = -1;
		this.bsv = -1;
	}

	private IconUI(int u, int v, int su, int sv, int bu, int bv, int bsu, int bsv){
		this.u = u;
		this.v = v;
		this.su = su;
		this.sv = sv;
		this.bu  = bu;
		this.bv  = bv;
		this.bsu = bsu;
		this.bsv = bsv;
	}	
	
}
