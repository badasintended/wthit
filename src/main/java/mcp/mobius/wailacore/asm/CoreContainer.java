package mcp.mobius.wailacore.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class CoreContainer extends DummyModContainer {

	public CoreContainer()
	{
		super(new ModMetadata());
		
		ModMetadata md = getMetadata();
		md.modId   = "WailaCore";
		md.name    = "WailaCore";
		md.version = "1.0.0";
		md.credits = "ProfMobius";
		md.authorList = Arrays.asList("ProfMobius");
		md.description = "Coremod associated with Waila to force backward compatibility with the API";
		md.url     = "profmobius.blogspot.com";
	}

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	//bus.register(this);
    	// this needs to return true, otherwise the mod will be deactivated by FML
        return true;
    }		
	
}
