/*
 * Copyright (c) 2022, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABI`LITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.requirements.player;

import net.runelite.client.plugins.microbot.questhelper.requirements.AbstractRequirement;
import net.runelite.client.plugins.microbot.questhelper.util.Utils;
import net.runelite.api.Client;

import javax.annotation.Nonnull;

public class IronmanRequirement extends AbstractRequirement
{
	final boolean shouldBeIronman;

	public IronmanRequirement(boolean shouldBeIronman)
	{
		this.shouldBeIronman = shouldBeIronman;
	}

	@Override
	public boolean check(Client client)
	{
		return client.getLocalPlayer() != null &&
			Utils.getAccountType(client).isAnyIronman() == shouldBeIronman;
	}

	@Nonnull
	@Override
	public String getDisplayText()
	{
		if (shouldBeIronman)
		{
			return "You need to be an ironman";
		}
		else
		{
			return "You need to not be an ironman";
		}
	}
}
