package com.bankmodetext;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(BankModeTextConfig.GROUP)
public interface BankModeTextConfig extends Config
{
	String GROUP = "bankmodetext";

	@ConfigItem(
		keyName = "font",
		name = "Font",
		description = "The font used for the button labels",
		position = 0
	)
	default LabelFont font()
	{
		return LabelFont.PLAIN_12;
	}

	@ConfigItem(
		keyName = "textColor",
		name = "Text Color",
		description = "The color of the button labels, and of the active mode when showing both modes",
		position = 1
	)
	default Color textColor()
	{
		return new Color(0xFF981F);
	}

	@ConfigItem(
		keyName = "showBothModes",
		name = "Show Both Rearrange Modes",
		description = "Display both Swap and Insert on the button, highlighting the active mode",
		position = 2
	)
	default boolean showBothModes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "inactiveModeColor",
		name = "Inactive Mode Color",
		description = "The color of the inactive mode when showing both modes",
		position = 3
	)
	default Color inactiveModeColor()
	{
		return new Color(0x9F9F9F);
	}
}
