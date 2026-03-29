package com.bankmodetext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class BankModeTextOverlay extends Overlay
{
	private static final Color LABEL_COLOR = new Color(0x6B, 0xB3, 0x6B);

	private final Client client;
	private final BankModeTextConfig config;

	@Inject
	BankModeTextOverlay(Client client, BankModeTextConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showActiveMode())
		{
			return null;
		}

		Widget buttonWidget = client.getWidget(InterfaceID.Bankmain.SWAP_INSERT);
		if (buttonWidget == null || buttonWidget.isHidden())
		{
			return null;
		}

		Rectangle bounds = buttonWidget.getBounds();
		if (bounds == null)
		{
			return null;
		}

		Point mousePos = client.getMouseCanvasPosition();
		if (!bounds.contains(mousePos.getX(), mousePos.getY()))
		{
			return null;
		}

		int mode = client.getVarbitValue(Varbits.BANK_REARRANGE_MODE);
		String modeText = mode == 1 ? "Insert" : "Swap";

		FontMetrics fm = graphics.getFontMetrics();
		int textWidth = fm.stringWidth(modeText);
		int x = bounds.x + (bounds.width - textWidth) / 2;
		int y = bounds.y + bounds.height + fm.getHeight();

		graphics.setColor(Color.BLACK);
		graphics.drawString(modeText, x + 1, y + 1);
		graphics.setColor(LABEL_COLOR);
		graphics.drawString(modeText, x, y);

		return null;
	}
}
