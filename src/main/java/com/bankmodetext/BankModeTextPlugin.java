package com.bankmodetext;

import com.google.inject.Provides;
import java.awt.Color;
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
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(
	name = "Bank Mode Text"
)
public class BankModeTextPlugin extends Plugin
{
	private static final Color HOVER_COLOR = Color.WHITE;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BankModeTextConfig config;

	@Override
	protected void startUp()
	{
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
	public void onConfigChanged(ConfigChanged event)
	{
		if (BankModeTextConfig.GROUP.equals(event.getGroup()))
		{
			clientThread.invokeLater(this::updateWidgets);
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		updateLabels();
	}

	private void updateWidgets()
	{
		Widget swapInsertGraphic = client.getWidget(InterfaceID.Bankmain.SWAP_INSERT_GRAPHIC);
		if (swapInsertGraphic != null)
		{
			applyStyle(swapInsertGraphic);
		}

		Widget noteGraphic = client.getWidget(InterfaceID.Bankmain.NOTE_GRAPHIC);
		if (noteGraphic != null)
		{
			applyStyle(noteGraphic);
		}

		updateLabels();
	}

	private void updateLabels()
	{
		Widget swapInsertGraphic = client.getWidget(InterfaceID.Bankmain.SWAP_INSERT_GRAPHIC);
		if (isStyled(swapInsertGraphic))
		{
			boolean hovered = isHovered(client.getWidget(InterfaceID.Bankmain.SWAP_INSERT));
			setLabel(swapInsertGraphic, getSwapInsertText(hovered), hovered);
		}

		Widget noteGraphic = client.getWidget(InterfaceID.Bankmain.NOTE_GRAPHIC);
		if (isStyled(noteGraphic))
		{
			boolean hovered = isHovered(client.getWidget(InterfaceID.Bankmain.NOTE));
			setLabel(noteGraphic, "Note", hovered);
		}
	}

	private String getSwapInsertText(boolean hovered)
	{
		int mode = client.getVarbitValue(Varbits.BANK_REARRANGE_MODE);

		if (!config.showBothModes())
		{
			return mode == 0 ? "Insert" : "Swap";
		}

		// Both modes are shown with color tags, which override the widget's text color
		Color activeColor = hovered ? HOVER_COLOR : config.textColor();
		Color inactiveColor = config.inactiveModeColor();
		boolean insertActive = mode == 1;

		return ColorUtil.wrapWithColorTag("Swap", insertActive ? inactiveColor : activeColor)
			+ "<br>"
			+ ColorUtil.wrapWithColorTag("Insert", insertActive ? activeColor : inactiveColor);
	}

	private void applyStyle(Widget widget)
	{
		widget.setHidden(false);
		widget.setType(WidgetType.TEXT);
		widget.setFontId(config.font().getFontId());
		widget.setTextShadowed(true);
		widget.setXTextAlignment(1);
		widget.setYTextAlignment(1);
		widget.revalidate();
	}

	private void setLabel(Widget widget, String text, boolean hovered)
	{
		if (!text.equals(widget.getText()))
		{
			widget.setText(text);
		}

		int color = (hovered ? HOVER_COLOR : config.textColor()).getRGB() & 0xFFFFFF;
		if (widget.getTextColor() != color)
		{
			widget.setTextColor(color);
		}
	}

	private static boolean isStyled(Widget widget)
	{
		return widget != null && !widget.isHidden() && widget.getType() == WidgetType.TEXT;
	}

	private boolean isHovered(Widget containerWidget)
	{
		if (containerWidget == null)
		{
			return false;
		}

		Rectangle bounds = containerWidget.getBounds();
		if (bounds == null)
		{
			return false;
		}

		Point mousePos = client.getMouseCanvasPosition();
		return bounds.contains(mousePos.getX(), mousePos.getY());
	}

	@Provides
	BankModeTextConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankModeTextConfig.class);
	}
}
