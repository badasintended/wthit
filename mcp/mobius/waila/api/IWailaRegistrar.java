package mcp.mobius.waila.api;

public interface IWailaRegistrar {
	/* Add a config option in the section modname with displayed text configtext and access key keyname */
	public void addConfig(String modname, String keyname, String configtext);
	
	/* Register a IWailaDataProvider for the given blockID, either for the Head section or the Body section */
	public void registerHeadProvider (IWailaDataProvider dataProvider, int blockID);
	public void registerBodyProvider (IWailaDataProvider dataProvider, int blockID);
	
	/* Register a stack overrider for the given blockID */
	public void registerStackProvider(IWailaDataProvider dataProvider, int blockID);
}
