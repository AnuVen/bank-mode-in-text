package com.bankmodetext;

import com.google.inject.Provides;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.ScriptID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Bank Mode Text"
)
public class BankModeTextPlugin extends Plugin
{
	private static final int TEXT_COLOR = 0xFF981F;
	private static final int HOVER_COLOR = 0xFFFFFF;
	private static final int FONT_ID = 494;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BankModeTextOverlay overlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		clientThread.invokeLater(this::updateWidgets);
	}

	@Override
	protected void shutDown()
	{
		clientThread.invokeLater(() ->
		{
			Widget swapInsertGraphic = client.getWidget(InterfaceID.Bankmain.SWAP_INSERT_GRAPHIC);
			if (swapInsertGraphic != null)
			{
				swapInsertGraphic.setType(WidgetType.GRAPHIC);
				swapInsertGraphic.setText("");
				swapInsertGraphic.revalidate();
			}

			Widget noteGraphic = client.getWidget(InterfaceID.Bankmain.NOTE_GRAPHIC);
			if (noteGraphic != null)
			{
				noteGraphic.setType(WidgetType.GRAPHIC);
				noteGraphic.setText("");
				noteGraphic.revalidate();
			}

		});
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		if (event.getScriptId() == ScriptID.BANKMAIN_BUILD
			|| event.getScriptId() == ScriptID.BANKMAIN_FINISHBUILDING)
		{
			updateWidgets();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == Varbits.BANK_REARRANGE_MODE)
		{
			updateWidgets();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		updateHoverState(
			client.getWidget(InterfaceID.Bankmain.SWAP_INSERT_GRAPHIC),
			client.getWidget(InterfaceID.Bankmain.SWAP_INSERT)
		);
		updateHoverState(
			client.getWidget(InterfaceID.Bankmain.NOTE_GRAPHIC),
			client.getWidget(InterfaceID.Bankmain.NOTE)
		);
	}

	private void updateWidgets()
	{
		updateSwapInsertText();
		updateNoteText();
	}

	private void updateSwapInsertText()
	{
		Widget graphicWidget = client.getWidget(InterfaceID.Bankmain.SWAP_INSERT_GRAPHIC);

		if (graphicWidget == null)
		{
			return;
		}

		int mode = client.getVarbitValue(Varbits.BANK_REARRANGE_MODE);
		String text = mode == 0 ? "Insert" : "Swap";

		applyText(graphicWidget, text);
	}

	private void updateNoteText()
	{
		Widget graphicWidget = client.getWidget(InterfaceID.Bankmain.NOTE_GRAPHIC);

		if (graphicWidget == null)
		{
			return;
		}

		applyText(graphicWidget, "Note");
	}

	private void applyText(Widget widget, String text)
	{
		widget.setHidden(false);
		widget.setType(WidgetType.TEXT);
		widget.setText(text);
		widget.setFontId(FONT_ID);
		widget.setTextColor(TEXT_COLOR);
		widget.setTextShadowed(true);
		widget.setXTextAlignment(1);
		widget.setYTextAlignment(1);
		widget.revalidate();
	}

	private void updateHoverState(Widget textWidget, Widget containerWidget)
	{
		if (textWidget == null || textWidget.isHidden() || containerWidget == null)
		{
			return;
		}

		Rectangle bounds = containerWidget.getBounds();
		if (bounds == null)
		{
			return;
		}

		Point mousePos = client.getMouseCanvasPosition();
		int color = bounds.contains(mousePos.getX(), mousePos.getY()) ? HOVER_COLOR : TEXT_COLOR;

		if (textWidget.getTextColor() != color)
		{
			textWidget.setTextColor(color);
		}
	}

	@Provides
	BankModeTextConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankModeTextConfig.class);
	}
}
