package com.bankmodetext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.FontID;

@Getter
@RequiredArgsConstructor
public enum LabelFont
{
	PLAIN_11("Plain 11", FontID.PLAIN_11),
	PLAIN_12("Plain 12", FontID.PLAIN_12),
	BOLD_12("Bold 12", FontID.BOLD_12);

	private final String name;
	private final int fontId;

	@Override
	public String toString()
	{
		return name;
	}
}
