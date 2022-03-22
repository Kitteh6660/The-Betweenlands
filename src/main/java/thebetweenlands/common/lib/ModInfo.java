package thebetweenlands.common.lib;

// Will be replaced by mod.toml.
public class ModInfo {
	public static final String ID = "thebetweenlands";
	public static final String NAME_PREFIX = ID + ".";
	public static final String ASSETS_PREFIX = ID + ":";
	public static final String NAME = "The Betweenlands";
	public static final String CHANNEL = ID;
	public static final String CLIENTPROXY_LOCATION = "thebetweenlands.client.proxy.ClientProxy";
	public static final String COMMONPROXY_LOCATION = "thebetweenlands.common.proxy.CommonProxy";
	public static final String API_NAME = "BetweenlandsAPI";

	public static final String DEPENDENCIES = "required-after:forge@[36.2.20,);after:tombmanygraves2api@[1.12-4.1.0,);after:tombmanygraves@[1.12-4.1.0,);after:jei@[4.12.0,)";
	public static final String MC_VERSIONS = "[1.16.5]";
	public static final String VERSION = "4.0.0";
	public static final String API_VERSION = "1.16.5";
	public static final String CONFIG_VERSION = "2.0.0"; //Increment and add updater if properties are moved/renamed or removed
	public static final String GALLERY_VERSION = "2.0.0"; //Protocol version of the online gallery. Change this when a breaking change is introduced to the gallery protocol or format. For backwards compatibility.
	
	//Values are replaced by build script
	//public static final boolean IDE = /*!ide*/true/*ide!*/;
	//public static final boolean SERVER_ONLY = /*!server_only*/false/*server_only!*/;
	
}
