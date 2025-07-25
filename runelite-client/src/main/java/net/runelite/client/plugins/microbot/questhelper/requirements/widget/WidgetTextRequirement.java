/*
 *
 *  * Copyright (c) 2021, Zoinkwiz
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice, this
 *  *    list of conditions and the following disclaimer.
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.runelite.client.plugins.microbot.questhelper.requirements.widget;

import net.runelite.client.plugins.microbot.questhelper.requirements.SimpleRequirement;
import net.runelite.client.plugins.microbot.questhelper.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.annotations.Component;
import net.runelite.api.widgets.Widget;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class WidgetTextRequirement extends SimpleRequirement
{
	@Setter
	@Getter
	protected boolean hasPassed;

	@Getter
	private final int groupId;

	private final int childId;

	private List<String> text;

	private int childChildId = -1;
	private boolean checkChildren;

	// Used to restrict the considered set of children
	private int min = -1;
	private int max = -1;

	@Setter
	private String displayText = "";

	/**
	 * Use this if you need to update a widget requirement dynamically.
	 * <p>
	 * Updating the text will reset the `hasPassed` variable.
	 *
	 * @param text list of valid strings for this widget check
	 */
	public void setText(@Nonnull List<String> text) {
		this.setHasPassed(false);

		this.text = text;
	}

	/**
	 * Use this if you need to update a widget requirement dynamically.
	 * <p>
	 * Updating the text will reset the `hasPassed` variable.
	 *
	 * @param text valid string for this widget check
	 */
	public void setText(@Nonnull String text) {
		this.setText(List.of(text));
	}

	public WidgetTextRequirement(@Component int componentId, String... text)
	{
		var pair = Utils.unpackWidget(componentId);
		this.groupId = pair.getLeft();
		this.childId = pair.getRight();
		this.text = Arrays.asList(text);
	}

	public WidgetTextRequirement(@Component int componentId, boolean checkChildren, String... text)
	{
		var pair = Utils.unpackWidget(componentId);
		this.groupId = pair.getLeft();
		this.childId = pair.getRight();
		this.checkChildren = checkChildren;
		this.text = Arrays.asList(text);
	}

	public WidgetTextRequirement(int groupId, int childId, boolean checkChildren, String... text)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.checkChildren = checkChildren;
		this.text = Arrays.asList(text);
	}

	public WidgetTextRequirement(int groupId, int childId, String... text)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.text = Arrays.asList(text);
	}

	public WidgetTextRequirement(int groupId, int childId, int childChildId, String... text)
	{
		this.groupId = groupId;
		this.childId = childId;
		this.childChildId = childChildId;
		this.text = Arrays.asList(text);
	}

	public void addRange(int min, int max)
	{
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean check(Client client)
	{
		return checkWidget(client);
	}

	public boolean checkWidget(Client client)
	{
		Widget widget = client.getWidget(groupId, childId);
		if (widget != null)
		{
			if (childChildId != -1)
			{
				widget = widget.getChild(childChildId);
			}
			if (widget != null)
			{
				for (String textOption : text)
				{
					if (checkChildren)
					{
						if (getChildren(widget, textOption) && !widget.isHidden())
						{
							return true;
						}
					}

					if (widget.getText().contains(textOption) && !widget.isHidden())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean getChildren(Widget parentWidget, String textOption)
	{
		Widget[] children = parentWidget.getStaticChildren();
		if (children.length == 0)
		{
			return false;
		}

		int currentMin;
		int currentMax;
		if (max == -1 || min == -1)
		{
			currentMin = 0;
			currentMax = children.length;
		}
		else
		{
			currentMax = Math.min(children.length, max);
			currentMin = Math.max(0, min);
		}

		for (int i = currentMin; i < currentMax; i++)
		{
			Widget currentWidget = parentWidget.getStaticChildren()[i];
			if (currentWidget.getNestedChildren() != null)
			{
				if (currentWidget.getText().contains(textOption))
				{
					return true;
				}
			}
			else
			{
				if (getChildren(currentWidget, textOption))
				{
					return true;
				}
			}
		}
		return false;
	}

	public void checkWidgetText(Client client)
	{
		hasPassed = hasPassed || checkWidget(client);
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		return displayText;
	}
}
