package com.bankmodetext;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BankModeTextPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BankModeTextPlugin.class);
		RuneLite.main(args);
	}
}
