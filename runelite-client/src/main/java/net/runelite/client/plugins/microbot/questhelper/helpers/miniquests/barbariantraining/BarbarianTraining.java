/*
 * Copyright (c) 2024, Zoinkwiz <https://github.com/Zoinkwiz>
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
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.miniquests.barbariantraining;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.config.ConfigKeys;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.ChatMessageRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.MesBoxRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.MultiChatMessageRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemOnTileRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.npc.DialogRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.runelite.RuneliteRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.widget.WidgetTextRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.*;

public class BarbarianTraining extends BasicQuestHelper
{
	// Items Required
	ItemRequirement barbFishingRod, tinderbox, bow, knife, fish, combatGear, antifireShield, chewedBones, bronzeBar, logs, hammer,
		roe, attackPotion, sapling, seed, spade, oakLogs, axe, feathers, barbarianAttackPotion;

	// Items recommended
	ItemRequirement gamesNecklace, catherbyTeleport;

	Requirement fishing48, agility15, strength15, fishing55, strength35, firemaking35, crafting11, farming15, smithing5;

	QuestRequirement druidicRitual, taiBwoWannaiTrio;

	Requirement taskedWithFishing, taskedWithHarpooning, taskedWithFarming, taskedWithBowFiremaking, taskedWithPyre, taskedWithPotSmashing,
		taskedWithSpears, taskedWithHastae, taskedWithHerblore;

	Requirement chewedBonesNearby;

	Requirement plantedSeed, smashedPot, litFireWithBow, sacrificedRemains, caughtBarbarianFish,
		caughtFishWithoutHarpoon, madePotion, madeSpear, madeHasta;

	DetailedQuestStep talkToOttoAboutFishing, searchBed, catchFish, talkToOttoAfterFish;

	DetailedQuestStep talkToOttoAboutBarehanded, fishHarpoon, talkToOttoAfterHarpoon;

	DetailedQuestStep talkToOttoAboutBow, lightLogWithBow, talkToOttoAfterBow;

	DetailedQuestStep talkToOttoAboutPyre, enterWhirlpool, goDownToBrutalGreen, goUpToMithrilDragons, killMithrilDragons,
		pickupChewedBones, useLogOnPyre, talkToOttoAfterPyre;

	DetailedQuestStep talkToOttoAboutFarming, plantSeed, talkToOttoAfterPlantingSeed;

	DetailedQuestStep talkToOttoAboutPots, plantSapling, talkToOttoAfterSmashingPot;

	DetailedQuestStep talkToOttoAboutSpears, makeBronzeSpear, talkToOttoAfterBronzeSpear, talkToOttoAboutHastae, makeBronzeHasta, talkToOttoAfterMakingHasta;

	DetailedQuestStep talkToOttoAboutHerblore, getBarbRodForHerblore, fishForHerblore, dissectFish, useRoeOnAttackPotion, talkToOttoAfterPotion;

	ConditionalStep fishingSteps, harpoonSteps, seedSteps, potSmashingSteps, firemakingSteps, pyreSteps, spearSteps,
		spearAndHastaeSteps, herbloreSteps;

	Requirement finishedFishing, finishedHarpoon, finishedSeedPlanting, finishedPotSmashing, finishedFiremaking, finishedPyre, finishedSpear, finishedHasta, finishedHerblore;

	Zone ancientCavernF0, ancientCavernF1, ancientCavernArrivalRoom;

	ZoneRequirement inAncientCavernF0, inAncientCavernF1, inAncientCavernArrivalRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		// TODO: Finish with an account which has fishing req
		// Fishing
		fishingSteps = new ConditionalStep(this, talkToOttoAboutFishing);
		fishingSteps.addStep(caughtBarbarianFish, talkToOttoAfterFish);
		fishingSteps.addStep(and(taskedWithFishing, barbFishingRod.alsoCheckBank(questBank)), catchFish);
		fishingSteps.addStep(taskedWithFishing, searchBed);
		fishingSteps.setLockingCondition(finishedFishing);

		// Herblore
		herbloreSteps = new ConditionalStep(this, talkToOttoAboutHerblore);
		herbloreSteps.addStep(madePotion, talkToOttoAfterPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, roe), useRoeOnAttackPotion);
		herbloreSteps.addStep(and(taskedWithHerblore, fish), dissectFish);
		herbloreSteps.addStep(and(taskedWithHerblore, barbFishingRod.alsoCheckBank(questBank)), fishForHerblore);
		herbloreSteps.addStep(taskedWithHerblore, getBarbRodForHerblore);
		herbloreSteps.setLockingCondition(finishedHerblore);

		// Harpoon
		harpoonSteps = new ConditionalStep(this, talkToOttoAboutBarehanded);
		harpoonSteps.addStep(caughtFishWithoutHarpoon, talkToOttoAfterHarpoon);
		harpoonSteps.addStep(taskedWithHarpooning, fishHarpoon);
		harpoonSteps.setLockingCondition(finishedHarpoon);

		// Farming
		potSmashingSteps = new ConditionalStep(this, talkToOttoAboutPots);
		potSmashingSteps.addStep(smashedPot, talkToOttoAfterSmashingPot);
		potSmashingSteps.addStep(taskedWithPotSmashing, plantSapling);
		potSmashingSteps.setLockingCondition(finishedPotSmashing);
		// Completed pot smashing, 9610 2->3


		seedSteps = new ConditionalStep(this, talkToOttoAboutFarming);
		seedSteps.addStep(plantedSeed, talkToOttoAfterPlantingSeed);
		seedSteps.addStep(taskedWithFarming, plantSeed);
		seedSteps.setLockingCondition(finishedSeedPlanting);

		// Firemaking
		firemakingSteps = new ConditionalStep(this, talkToOttoAboutBow);
		firemakingSteps.addStep(litFireWithBow, talkToOttoAfterBow);
		firemakingSteps.addStep(taskedWithBowFiremaking, lightLogWithBow);
		firemakingSteps.setLockingCondition(finishedFiremaking);

		pyreSteps = new ConditionalStep(this, talkToOttoAboutPyre);
		pyreSteps.addStep(LogicHelper.and(sacrificedRemains), talkToOttoAfterPyre);
		pyreSteps.addStep(and(taskedWithPyre, chewedBones.alsoCheckBank(questBank)), useLogOnPyre);
		pyreSteps.addStep(and(taskedWithPyre, chewedBonesNearby), pickupChewedBones);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernArrivalRoom), enterWhirlpool);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernF0), goUpToMithrilDragons);
		pyreSteps.addStep(and(taskedWithPyre, inAncientCavernF1), killMithrilDragons);
		pyreSteps.addStep(taskedWithPyre, enterWhirlpool);
		pyreSteps.setLockingCondition(finishedPyre);

		// Smithing
		spearSteps = new ConditionalStep(this, talkToOttoAboutSpears);
		spearSteps.addStep(madeSpear, talkToOttoAfterBronzeSpear);
		spearSteps.addStep(taskedWithSpears, makeBronzeSpear);
		spearSteps.setLockingCondition(finishedSpear);

		spearAndHastaeSteps = new ConditionalStep(this, spearSteps);
		spearAndHastaeSteps.addStep(madeHasta, talkToOttoAfterMakingHasta);
		spearAndHastaeSteps.addStep(taskedWithHastae, makeBronzeHasta);
		spearAndHastaeSteps.addStep(finishedSpear, talkToOttoAboutHastae);
		spearAndHastaeSteps.setLockingCondition(finishedHasta);

		ConditionalStep allSteps = new ConditionalStep(this, fishingSteps);
		allSteps.addStep(LogicHelper.nor(finishedFishing), fishingSteps);
		allSteps.addStep(LogicHelper.nor(finishedHerblore), herbloreSteps);
		allSteps.addStep(LogicHelper.nor(finishedHarpoon), harpoonSteps);
		allSteps.addStep(LogicHelper.nor(finishedSeedPlanting), seedSteps);
		allSteps.addStep(LogicHelper.nor(finishedPotSmashing), potSmashingSteps);
		allSteps.addStep(LogicHelper.nor(finishedFiremaking), firemakingSteps);
		allSteps.addStep(nand(finishedSpear, finishedHasta), spearAndHastaeSteps);
		allSteps.addStep(LogicHelper.nor(finishedPyre), pyreSteps);
		allSteps.addDialogSteps("Let's talk about my training.", "I seek more knowledge.");
		allSteps.setCheckAllChildStepsOnListenerCall(true);


		// Started, 9613
		steps.put(0, allSteps);
		steps.put(1, allSteps);
		// Increments after doing a task
		steps.put(2, allSteps);
		// 9610, 0->1 at some point??? Probably for pot smashing, or for whirlpool?
		steps.put(3, allSteps);
		steps.put(4, allSteps);
		steps.put(5, allSteps);
		steps.put(6, allSteps);
		steps.put(7, allSteps);
		steps.put(8, allSteps);
		steps.put(9, allSteps);
		steps.put(10, allSteps);

		return steps;
	}

	@Override
	public void setupRequirements()
	{
		barbFishingRod = new ItemRequirement("Barbarian rod", ItemID.BRUT_FISHING_ROD);
		barbFishingRod.addAlternates(ItemID.FISHINGROD_PEARL_BRUT);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX);
		bow = new ItemRequirement("Any bow", ItemCollections.BOWS);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		fish = new ItemRequirement("Leaping trout/salmon/sturgeon", ItemID.BRUT_STURGEON);
		fish.addAlternates(ItemID.BRUT_SPAWNING_SALMON, ItemID.BRUT_SPAWNING_TROUT);
		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
		antifireShield = new ItemRequirement("Anti-dragon shield or DFS", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		chewedBones = new ItemRequirement("Chewed bones", ItemID.BRUT_BARBARIAN_BONES);
		bronzeBar = new ItemRequirement("Bronze bar", ItemID.BRONZE_BAR);
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		hammer = new ItemRequirement("Hammer", ItemCollections.HAMMER);
		roe = new ItemRequirement("Roe", ItemID.BRUT_ROE);
		attackPotion = new ItemRequirement("Attack potion (2)", ItemID._2DOSE1ATTACK);
		sapling = new ItemRequirement("Any sapling you can plant", ItemCollections.TREE_SAPLINGS);
		sapling.addAlternates(ItemCollections.FRUIT_TREE_SAPLINGS);
		seed = new ItemRequirement("Any seed that can be planted directly", ItemCollections.ALLOTMENT_SEEDS);
		seed.addAlternates(ItemCollections.HERB_SEEDS);
		seed.addAlternates(ItemCollections.FLOWER_SEEDS);
		seed.addAlternates(ItemCollections.BUSH_SEEDS);
		seed.addAlternates(ItemCollections.HOPS_SEEDS);
		spade = new ItemRequirement("Spade", ItemID.SPADE);
		oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES);
		feathers = new ItemRequirement("Feathers", ItemID.FEATHER);

		// Recommended
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		gamesNecklace.setChargedItem(true);
		catherbyTeleport = new ItemRequirement("Catherby teleport for fishing", ItemID.LUNAR_TABLET_CATHERBY_TELEPORT);
		catherbyTeleport.addAlternates(ItemID.POH_TABLET_CAMELOTTELEPORT);

		// Quest items
		barbarianAttackPotion = new ItemRequirement("Attack mix (2)", ItemID.BRUTAL_2DOSE1ATTACK);

		druidicRitual = new QuestRequirement(QuestHelperQuest.DRUIDIC_RITUAL, QuestState.FINISHED);
		taiBwoWannaiTrio = new QuestRequirement(QuestHelperQuest.TAI_BWO_WANNAI_TRIO, QuestState.FINISHED);
		fishing55 = new SkillRequirement(Skill.FISHING, 55, true);
		fishing48 = new SkillRequirement(Skill.FISHING, 48, true);
		agility15 = new SkillRequirement(Skill.AGILITY, 15);
		strength15 = new SkillRequirement(Skill.STRENGTH, 15);
		strength35 = new SkillRequirement(Skill.STRENGTH, 35);
		firemaking35 = new SkillRequirement(Skill.FIREMAKING, 35);
		crafting11 = new SkillRequirement(Skill.CRAFTING, 11);
		farming15 = new SkillRequirement(Skill.FARMING, 15, true);
		smithing5 = new SkillRequirement(Skill.SMITHING, 5, true);
	}

	public void setupConditions()
	{
		// Started tasks

		taskedWithFishing = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_FISHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Certainly. Take the rod from under my bed and fish in the lake. When you have caught a few fish, I am sure you will be ready to talk more with me."),
				new DialogRequirement("Alas, I do not sense that you have been successful in your fishing yet. The look in your eyes is not that of the osprey."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "fish with a new")
			)
		);

		taskedWithHarpooning = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_HARPOON.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("... and I thought fishing was a safe way to pass the time."),
				new DialogRequirement("I see you need encouragement in learning the ways of fishing without a harpoon."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "fish with my")
			)
		);

		taskedWithFarming = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Remember to be calm, and good luck."),
				new DialogRequirement("I see you have yet to be successful in planting a seed with your fists."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "plant a seed with")
			)
		);

		taskedWithPotSmashing = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("May the spirits guide you into success."),
				new DialogRequirement("You have not yet attempted to plant a tree. Why not?"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smash pots after")
			)
		);

		taskedWithBowFiremaking = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The spirits will aid you. The power they supply will guide your hands. Go and benefit from their guidance upon oak logs."),
				new DialogRequirement("By now you know my response."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "light a fire with")
			)
		);

		taskedWithPyre = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Dive into the whirlpool in the lake to the east. The spirits will use their abilities to ensure you arrive in the correct location. Be warned, their influence fades, so you must find y"),
				new DialogRequirement("I will repeat myself fully, since this is quite complex. Listen well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>create pyre ships")
			)
		);

		taskedWithHerblore = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_HERBLORE.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Have I become so predictable? But yes, I do indeed require a potion. It is of the highest importance that you bring me a lesser attack potion combined with fish roe."),
				new DialogRequirement("Do you have my potion?"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to make a <col=800000>new type")
			)
		);

		taskedWithSpears = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Note well that you will require wood for the spear shafts. The quality of wood must be similar to that of the metal involved."),
				new DialogRequirement("You do not exude the presence of one who has poured his soul into manufacturing spears."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "Otto<col=000080> has tasked me with learning how to <col=800000>smith spears")
			)
		);

		taskedWithHastae = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_STARTED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Indeed. You may use our special anvil for this spear type too. The ways of black and dragon hastae are beyond our knowledge, however."),
				new DialogRequirement("Take some wood and metal and make a spear upon the<br>nearby anvil, then you may return to me. As an<br>example, you may use bronze bars with normal logs or<br>iron bars with oak logs."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, " has tasked me with learning how to <col=800000>smith a hasta")
			)
		);

		// Finished tasks
		finishedFishing = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_FISHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Patience young one. These are fish which are fat with eggs rather than fat of flesh. It is these eggs that are the thing to make use of."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to catch a fish with the new rod!")
			),
			"Finished Barbarian Fishing"
		);

		finishedHarpoon = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_HARPOON.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I mean that when you eventually die and find peace, at least the spirits you encounter will be your friends. Alas for you adventurous sort, the natural ways of passing are close to imp"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to fish with my hands!")
			),
			"Finished Barbarian Harpooning"
		);

		finishedSeedPlanting = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_SEED_PLANTING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("No child, but we all have potential to improve our strength."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I managed to plant a seed with my fists!")
			),
			"Finished Barbarian Seed Planting"
		);

		finishedPotSmashing = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_POT_SMASHING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("It will become more natural with practice."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "<str>I managed to smash a plant pot without littering!")
			),
			"Finished Barbarian Pot Smashing"
		);

		finishedFiremaking = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_FIREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("Fine news indeed!"),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to light a fire with a bow!")
			),
			"Finished Barbarian Firemaking"
		);

		finishedPyre = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_PYREMAKING.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("On this great day you have my eternal thanks. May you find riches while rescuing my spiritual ancestors in the caverns for many moons to come."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a pyre ship!")
			),
			"Finished Barbarian Pyremaking"
		);

		finishedSpear = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("The manufacture of spears is now yours as a speciality. Use your skill well."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to smith a spear!")
			),
			"Finished Barbarian Spear Smithing"
		);

		finishedHasta = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("To live life to it's fullest of course - that you may be a peaceful spirit when your time ends."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a hasta!")
			),
			"Finished Barbarian Hasta Smithing"
		);

		finishedHerblore = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_FINISHED_HERBLORE.getKey(),
			new Conditions(true, LogicType.OR,
				new DialogRequirement("I will take that off your hands now. I will say no more than that I am eternally grateful."),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I managed to create a new potion!")
			),
			"Finished Barbarian Herblore"
		);

		// Mid-conditions
		plantedSeed = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_PLANTED_SEED.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>plant a seed with my fists<col=000080>!")
			)
		);

		smashedPot = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_SMASHED_POT.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You plant "),
					new ChatMessageRequirement(" sapling"),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smash a pot without littering<col=000080>!")
			)
		);

		litFireWithBow = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_BOW_FIRE.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The fire catches and the logs begin to burn."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>light a fire with a bow<col=000080>!")
			)
		);

		sacrificedRemains = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_PYRE_MADE.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("The ancient barbarian is laid to rest."),
					new ChatMessageRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>create a pyre ship<col=000080>! I should let")
			)
		);

		caughtBarbarianFish = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_BARBFISHED.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You catch a leaping trout.", "You catch a leaping salmon.", "You catch a leaping sturgeon."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to catch a <col=800000>fish with the new rod<col=000080>! I should let")
			)
		);

		caughtFishWithoutHarpoon = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_HARPOONED_FISH.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You catch a raw tuna.", "You catch a swordfish.", "You catch a shark.", "You catch a shark!"),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>fish with my hands<col=000080>! I should let <col=800000>Otto <col=000080>know")
			)
		);

		madePotion = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_MADE_POTION.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You combine your potion with the fish eggs."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to make a <col=800000>new type of potion<col=000080>! I should let")
			)
		);

		madeSpear = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_MADE_SPEAR.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You make a "),
					new ChatMessageRequirement(" spear."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smith a spear<col=000080>!")
			)
		);

		madeHasta = new RuneliteRequirement(
			getConfigManager(), ConfigKeys.BARBARIAN_TRAINING_MADE_HASTA.getKey(),
			new Conditions(true, LogicType.OR,
				new MultiChatMessageRequirement(
					new ChatMessageRequirement("You make a "),
					new ChatMessageRequirement(" hasta."),
					new MesBoxRequirement("You feel you have learned more of barbarian ways. Otto might wish to talk to you more.")
				),
				new WidgetTextRequirement(InterfaceID.Questjournal.TEXTLAYER, true, "I've managed to <col=800000>smith a hasta<col=000080>!")
			)
		);

		// For harpooning,
		// You catch a tuna.

		ancientCavernArrivalRoom = new Zone(new WorldPoint(1762, 5364, 1), new WorldPoint(1769, 5359, 1));
		ancientCavernF0 = new Zone(new WorldPoint(1734, 5318, 0), new WorldPoint(1800, 5400, 0));
		ancientCavernF1 = new Zone(new WorldPoint(1734, 5318, 1), new WorldPoint(1800, 5400, 1));
		inAncientCavernArrivalRoom = new ZoneRequirement(ancientCavernArrivalRoom);
		inAncientCavernF0 = new ZoneRequirement(ancientCavernF0);
		inAncientCavernF1 = new ZoneRequirement(ancientCavernF1);

		chewedBonesNearby = new ItemOnTileRequirement(chewedBones);
	}

	public void setupSteps()
	{
		// Barbarian Fishing
		talkToOttoAboutFishing = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about fishing.");
		talkToOttoAboutFishing.addDialogSteps("You think so?", "What can I learn about the use of a fishing rod?");

		searchBed = new ObjectStep(this, ObjectID.BRUT_BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		catchFish = new NpcStep(this, NpcID._0_39_54_BRUT_FISHING_SPOT, new WorldPoint(2500, 3510, 0),
			"Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod, feathers);

		talkToOttoAfterFish = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about your success.");
		talkToOttoAfterFish.addDialogStep("I've fished with a barbarian rod!");

		// Barbarian Harpooning
		talkToOttoAboutBarehanded = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about barehand fishing.");
		talkToOttoAboutBarehanded.addDialogSteps("You think so?", "Please teach me of your cunning with harpoons.");
		// TODO: Update ID and WorldPoint
		fishHarpoon = new NpcStep(this, NpcID._0_44_53_RAREFISH, new WorldPoint(2845, 3429, 0), "Fish in a fishing spot which requires a harpoon WITHOUT a harpoon.", true, fishing55);
		talkToOttoAfterHarpoon = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterHarpoon.addDialogSteps("Barbarian Outpost.", "I've fished with my hands!");
		talkToOttoAfterHarpoon.addTeleport(gamesNecklace);

		// Barbarian Firemaking
		talkToOttoAboutBow = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about firemaking.");
		talkToOttoAboutBow.addDialogSteps("You think so?", "I'm ready for your firemaking wisdom. Please instruct me.");
		lightLogWithBow = new DetailedQuestStep(this, "Use a bow on some oak logs to light it.", bow.highlighted(), oakLogs.highlighted());
		talkToOttoAfterBow = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterBow.addDialogSteps("I've set fire to logs with a bow!");

		talkToOttoAboutPyre = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced firemaking.");
		talkToOttoAboutPyre.addDialogStep("I have completed firemaking with a bow. What follows this?");
		enterWhirlpool = new ObjectStep(this, ObjectID.BRUT_WHIRLPOOL, new WorldPoint(2513, 3509, 0),
			"Jump into the whirlpool north of Otto. Be prepared to fight mithril dragons.", combatGear, antifireShield);
		goDownToBrutalGreen = new ObjectStep(this, ObjectID.BRUT_STAIR_LRG_TOP, new WorldPoint(1769, 5365, 1),
			"Climb down the stairs. There are brutal green dragons there, so have antifire protection and Protect from Magic on!");
		goUpToMithrilDragons = new ObjectStep(this, ObjectID.BRUT_CAVE_STAIRS_LOW, new WorldPoint(1778, 5344, 0),
			"Climb up the stairs to the south-east. Be ready to fight the mithril dragons!");
		killMithrilDragons = new NpcStep(this, NpcID.BRUT_MITHRIL_DRAGON, new WorldPoint(1773, 5348, 1),
			"Kill mithril dragons until you get some chewed bones.");
		pickupChewedBones = new ItemStep(this, "Pick up the chewed bones.", chewedBones);
		useLogOnPyre = new ObjectStep(this, ObjectID.BRUT_BURNED_GROUND, new WorldPoint(2506, 3518, 0),
			"Construct a pyre on the lake near Otto.", logs, chewedBones, tinderbox, axe);
		useLogOnPyre.addDialogStep("I've created a pyre ship!");
		talkToOttoAfterPyre = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto and tell him about the succesful pyre-making.");

		// Barbarian Farming
		talkToOttoAboutFarming = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about farming.");
		talkToOttoAboutFarming.addDialogSteps("You think so?", "How can I use my strength in the ways of agriculture?");
		plantSeed = new DetailedQuestStep(this, "Plant a seed in a patch WITHOUT a dibber. This can fail, so bring multiple and preferably use cheap seeds.",
			seed.quantity(9));
		talkToOttoAfterPlantingSeed = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterPlantingSeed.addDialogStep("I've planted seeds with my fists!");

		// Farming P2
		talkToOttoAboutPots = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about advanced farming.");
		talkToOttoAboutPots.addDialogStep("Are there any other fist related farming activities I can learn?");
		plantSapling = new DetailedQuestStep(this, "Plant a sapling in a tree or fruit tree patch.", sapling, spade);
		talkToOttoAfterSmashingPot = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Return to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterSmashingPot.addDialogStep("I've smashed a pot whilst farming!");

		// Barbarian Smithing, REQUIRES barbarian firemaking P1
		talkToOttoAboutSpears = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about spears.");
		talkToOttoAboutSpears.addDialogSteps("Tell me more about the use of spears.");
		makeBronzeSpear = new ObjectStep(this, ObjectID.BRUT_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze spear on the anvil south of Otto.", bronzeBar, logs, hammer);
		talkToOttoAfterBronzeSpear = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterBronzeSpear.addDialogStep("I've created a spear!");

		talkToOttoAboutHastae = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about hastae.");
		talkToOttoAboutHastae.addDialogStep("Tell me more about the use of spears.");
		makeBronzeHasta = new ObjectStep(this, ObjectID.BRUT_ANVIL, new WorldPoint(2502, 3485, 0),
			"Make a bronze hasta on the anvil south of Otto.", bronzeBar, logs, hammer);
		makeBronzeHasta.addWidgetHighlightWithItemIdRequirement(270, 15, 11421, true);
		talkToOttoAfterMakingHasta = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.");
		talkToOttoAfterMakingHasta.addDialogStep("I've created a hasta!");

		// Barbarian Herblore, REQUIRES Barbarian Fishing
		talkToOttoAboutHerblore = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls about herblore.");
		getBarbRodForHerblore = new ObjectStep(this, ObjectID.BRUT_BARBARIAN_BED, new WorldPoint(2500, 3490, 0), "Search the bed in Otto's hut.");
		fishForHerblore = new NpcStep(this, NpcID._0_39_54_BRUT_FISHING_SPOT, new WorldPoint(2500, 3510, 0), "Fish from one of the fishing spots near Otto's hut.", true, barbFishingRod);
		fishForHerblore.addSubSteps(getBarbRodForHerblore);
		dissectFish = new DetailedQuestStep(this, "Dissect the fish with a knife until you get some roe.", knife.highlighted(), fish.highlighted());
		talkToOttoAboutHerblore.addDialogSteps("What was that secret knowledge of herblore we talked of?");
		useRoeOnAttackPotion = new DetailedQuestStep(this, "Use roe on an attack potion (2).", roe.highlighted(), attackPotion.highlighted());
		talkToOttoAfterPotion = new NpcStep(this, NpcID.BRUT_OTTO, new WorldPoint(2500, 3488, 0),
			"Talk to Otto in his hut north-west of Baxtorian Falls.", barbarianAttackPotion);
		talkToOttoAfterPotion.addDialogStep("I've made a barbarian potion!");
		// LOOKED IN LOG, varplayer 3679 -1->3451
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		return new ArrayList<>(
			Arrays.asList(taiBwoWannaiTrio, fishing55, firemaking35, strength15, agility15, farming15, crafting11, smithing5)
		);
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(seed, sapling, spade,
			bow, oakLogs, tinderbox, axe,
			feathers, knife,
			hammer, bronzeBar.quantity(2), logs.quantity(3),
			attackPotion, roe);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNecklace.quantity(5), catherbyTeleport);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If the helper is out of sync, open up the Quest Journal to re-sync it.");
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails barbFishing = new PanelDetails("Barbarian Fishing", Arrays.asList(talkToOttoAboutFishing, searchBed, catchFish, talkToOttoAfterFish),
			Arrays.asList(fishing48, agility15, strength15, feathers), Arrays.asList(gamesNecklace, catherbyTeleport));
		barbFishing.setLockingStep(fishingSteps);
		allSteps.add(barbFishing);

		PanelDetails barbHerblore = new PanelDetails("Barbarian Herblore", Arrays.asList(talkToOttoAboutHerblore,
			fishForHerblore, dissectFish, useRoeOnAttackPotion, talkToOttoAfterPotion),
			druidicRitual, finishedFishing, attackPotion, knife, barbFishingRod, feathers);
		barbHerblore.setLockingStep(herbloreSteps);
		allSteps.add(barbHerblore);

		PanelDetails barbHarpooning = new PanelDetails("Barbarian Harpooning", Arrays.asList(talkToOttoAboutBarehanded, fishHarpoon, talkToOttoAfterHarpoon), fishing55, agility15, strength35);
		barbHarpooning.setLockingStep(harpoonSteps);
		allSteps.add(barbHarpooning);

		PanelDetails barbFarming = new PanelDetails("Barbarian Planting",
			Arrays.asList(talkToOttoAboutFarming, plantSeed, talkToOttoAfterPlantingSeed), seed);
		barbFarming.setLockingStep(seedSteps);
		allSteps.add(barbFarming);

		PanelDetails barbSmashing = new PanelDetails("Barb Plant Pot Smashing",
			Arrays.asList(talkToOttoAboutPots, plantSapling, talkToOttoAfterSmashingPot),
			finishedSeedPlanting, farming15, sapling, spade);
		barbSmashing.setLockingStep(potSmashingSteps);
		allSteps.add(barbSmashing);

		PanelDetails barbFiremaking = new PanelDetails("Barbarian Firemaking",
			Arrays.asList(talkToOttoAboutBow, lightLogWithBow, talkToOttoAfterBow),
			firemaking35, crafting11, bow, oakLogs);
		barbFiremaking.setLockingStep(firemakingSteps);
		allSteps.add(barbFiremaking);

		PanelDetails barbSmithing = new PanelDetails("Barbarian Smithing", Arrays.asList(talkToOttoAboutSpears, makeBronzeSpear, talkToOttoAfterBronzeSpear,
			talkToOttoAboutHastae, makeBronzeHasta, talkToOttoAfterMakingHasta), taiBwoWannaiTrio,
			finishedHarpoon, smithing5, hammer, bronzeBar.quantity(2), logs.quantity(2));
		barbSmithing.setLockingStep(spearAndHastaeSteps);
		allSteps.add(barbSmithing);

		PanelDetails barbPyremaking = new PanelDetails("Barbarian Pyremaking",
			Arrays.asList(talkToOttoAboutPyre, enterWhirlpool, goDownToBrutalGreen, goUpToMithrilDragons, killMithrilDragons,
				pickupChewedBones, useLogOnPyre, talkToOttoAfterPyre),
			finishedFiremaking, logs, tinderbox, axe, combatGear, antifireShield);
		barbPyremaking.setLockingStep(pyreSteps);
		allSteps.add(barbPyremaking);

		return allSteps;
	}
}
