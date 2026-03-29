package com.bankmodetext;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bankmodetext")
public interface BankModeTextConfig extends Config
{
	@ConfigItem(
		keyName = "showActiveMode",
		name = "Show Active Rearrange Mode",
		description = "Display the currently active rearrange mode below the button"
	)
	default boolean showActiveMode()
	{
		return false;
	}
}
