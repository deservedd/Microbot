/*
 * Copyright (c) 2021, Obasill <https://github.com/Obasill>
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
package net.runelite.client.plugins.microbot.questhelper.helpers.achievementdiaries.lumbridgeanddraynor;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.ComplexStateQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.ComplexRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirements;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarComparisonRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarType;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarplayerRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.client.plugins.microbot.questhelper.steps.emote.QuestEmote;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarPlayerID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LumbridgeElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement lockpick, crossbow, mithgrap, lightsource, axe, addyBar, hammer, essence, waterAccessOrAbyss, qcCape;

	// Items recommended
	ItemRequirement ringOfDueling, dorgSphere;

	Requirement notRichChest, notMovario, notChopMagic, notAddyPlatebody, notWaterRunes, notQCEmote, allQuests,
		deathToDorg, templeOfIkov;

	QuestStep claimReward, richChest, movario, chopMagic, addyPlatebody, waterRunes, qcEmote, moveToWater,
		dorgStairsChest, dorgStairsMovario, moveToOldman, moveToUndergroundChest,
		moveToUndergroundMovario, moveToDorgAgi;

	ObjectStep moveToDraySewer, moveToDorgChest, moveToDorgMovario;

	Zone underground, dorg1, dorg2, draySewer, oldman, waterAltar, dorgAgi;

	ZoneRequirement inUnderground, inDorg1, inDorg2, inDraySewer, inOldman, inWaterAltar, inDorgAgi;

	ConditionalStep richChestTask, movarioTask, chopMagicTask, addyPlatebodyTask, waterRunesTask, qcEmoteTask;

	@Override
	public QuestStep loadStep()
	{
		initializeRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);

		addyPlatebodyTask = new ConditionalStep(this, moveToDraySewer);
		addyPlatebodyTask.addStep(inDraySewer, addyPlatebody);
		doElite.addStep(notAddyPlatebody, addyPlatebodyTask);

		qcEmoteTask = new ConditionalStep(this, moveToOldman);
		qcEmoteTask.addStep(inOldman, qcEmote);
		doElite.addStep(notQCEmote, qcEmoteTask);

		richChestTask = new ConditionalStep(this, moveToUndergroundChest);
		richChestTask.addStep(inUnderground, moveToDorgChest);
		richChestTask.addStep(inDorg1, dorgStairsChest);
		richChestTask.addStep(inDorg2, richChest);
		doElite.addStep(notRichChest, richChestTask);

		movarioTask = new ConditionalStep(this, moveToUndergroundMovario);
		movarioTask.addStep(inUnderground, moveToDorgMovario);
		movarioTask.addStep(inDorg1, dorgStairsMovario);
		movarioTask.addStep(inDorg2, moveToDorgAgi);
		movarioTask.addStep(inDorgAgi, movario);
		doElite.addStep(notMovario, movarioTask);

		waterRunesTask = new ConditionalStep(this, moveToWater);
		waterRunesTask.addStep(inWaterAltar, waterRunes);
		doElite.addStep(notWaterRunes, waterRunesTask);

		chopMagicTask = new ConditionalStep(this, chopMagic);
		doElite.addStep(notChopMagic, chopMagicTask);

		return doElite;
	}

	@Override
	protected void setupRequirements()
	{
		notRichChest = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 4);
		notMovario = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 5);
		notChopMagic = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 6);
		notAddyPlatebody = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 7);
		notWaterRunes = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 8);
		notQCEmote = new VarplayerRequirement(VarPlayerID.LUMB_DRAY_ACHIEVEMENT_DIARY2, false, 9);

		allQuests = new VarComparisonRequirement(VarType.VARP, VarPlayer.QUEST_POINTS, VarType.VARBIT, 1782, Operation.EQUAL, "All quests completed");

		lockpick = new ItemRequirement("Lockpick", ItemID.LOCKPICK).showConditioned(notRichChest).isNotConsumed();
		crossbow = new ItemRequirement("Crossbow", ItemCollections.CROSSBOWS).showConditioned(notMovario).isNotConsumed();
		mithgrap = new ItemRequirement("Mith grapple", ItemID.XBOWS_GRAPPLE_TIP_BOLT_MITHRIL_ROPE).showConditioned(notMovario).isNotConsumed();
		lightsource = new ItemRequirement("A lightsource", ItemCollections.LIGHT_SOURCES).showConditioned(notMovario).isNotConsumed();
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).showConditioned(notChopMagic).isNotConsumed();
		addyBar = new ItemRequirement("Adamantite bar", ItemID.ADAMANTITE_BAR).showConditioned(notAddyPlatebody);
		hammer = new ItemRequirement("Hammer", ItemID.HAMMER).showConditioned(notAddyPlatebody).isNotConsumed();
		essence = new ItemRequirement("Essence", ItemCollections.ESSENCE_LOW).showConditioned(notWaterRunes);
		waterAccessOrAbyss = new ItemRequirement("Access to the Water Altar", ItemCollections.WATER_ALTAR_WEARABLE).showConditioned(notWaterRunes).isNotConsumed();
		waterAccessOrAbyss.setTooltip("Water Tiara, Elemental Tiara, RC-skill cape or via Abyss");
		qcCape = new ItemRequirement("Quest cape", ItemCollections.QUEST_CAPE).showConditioned(notQCEmote).isNotConsumed();
		dorgSphere = new ItemRequirement("Dorgesh-kaan Sphere", ItemID.DORGESH_TELEPORT_ARTIFACT)
			.showConditioned(new Conditions(notMovario, notRichChest));
		ringOfDueling = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS)
			.showConditioned(notChopMagic);

		inUnderground = new ZoneRequirement(underground);
		inDorg1 = new ZoneRequirement(dorg1);
		inDorg2 = new ZoneRequirement(dorg2);
		inDraySewer = new ZoneRequirement(draySewer);
		inWaterAltar = new ZoneRequirement(waterAltar);
		inOldman = new ZoneRequirement(oldman);
		inDorgAgi = new ZoneRequirement(dorgAgi);

		deathToDorg = new QuestRequirement(QuestHelperQuest.DEATH_TO_THE_DORGESHUUN, QuestState.FINISHED);
		templeOfIkov = new QuestRequirement(QuestHelperQuest.TEMPLE_OF_IKOV, QuestState.FINISHED);
	}

	@Override
	protected void setupZones()
	{
		waterAltar = new Zone(new WorldPoint(2688, 4863, 0), new WorldPoint(2751, 4800, 0));
		underground = new Zone(new WorldPoint(3137, 9706, 0), new WorldPoint(3332, 9465, 2));
		draySewer = new Zone(new WorldPoint(3077, 9699, 0), new WorldPoint(3132, 9641, 0));
		dorg1 = new Zone(new WorldPoint(2688, 5377, 0), new WorldPoint(2751, 5251, 0));
		dorg2 = new Zone(new WorldPoint(2688, 5377, 1), new WorldPoint(2751, 5251, 1));
		oldman = new Zone(new WorldPoint(3087, 3255, 0), new WorldPoint(3094, 3251, 0));
		dorgAgi = new Zone(new WorldPoint(2688, 5247, 0), new WorldPoint(2752, 5183, 3));
	}

	public void setupSteps()
	{
		moveToDraySewer = new ObjectStep(this, ObjectID.VAMPIRE_TRAP2, new WorldPoint(3118, 3244, 0),
			"Climb down into the Draynor Sewer.");
		moveToDraySewer.addAlternateObjects(ObjectID.VAMPIRE_TRAP1);
		addyPlatebody = new ObjectStep(this, ObjectID.ANVIL, new WorldPoint(3112, 9689, 0),
			"Smith a adamant platebody at the anvil in Draynor Sewer.", addyBar.quantity(5), hammer);

		moveToOldman = new TileStep(this, new WorldPoint(3088, 3253, 0),
			"Go to the Wise Old Man's house in Draynor Village.");
		qcEmote = new EmoteStep(this, QuestEmote.SKILL_CAPE, new WorldPoint(3088, 3253, 0),
			"Perform the skill cape emote with the quest cape equipped.", qcCape.equipped());

		moveToWater = new ObjectStep(this, 34815, new WorldPoint(3185, 3165, 0),
			"Enter the water altar.", waterAccessOrAbyss.highlighted(), essence.quantity(28));
		waterRunes = new ObjectStep(this, ObjectID.WATER_ALTAR, new WorldPoint(2716, 4836, 0),
			"Craft water runes.", essence.quantity(28));

		moveToUndergroundMovario = new ObjectStep(this, ObjectID.QIP_COOK_TRAPDOOR_OPEN, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", mithgrap, crossbow, lightsource);
		moveToUndergroundChest = new ObjectStep(this, ObjectID.QIP_COOK_TRAPDOOR_OPEN, new WorldPoint(3209, 3216, 0),
			"Climb down the trapdoor in the Lumbridge Castle.", lockpick, lightsource);

		moveToDorgChest = new ObjectStep(this, ObjectID.CAVE_GOBLIN_CITY_DOORR, new WorldPoint(3317, 9601, 0),
			"Go through the doors to Dorgesh-Kaan.", true, lockpick, lightsource);
		moveToDorgChest.addAlternateObjects(ObjectID.CAVE_GOBLIN_CITY_DOORL);
		moveToDorgMovario = new ObjectStep(this, ObjectID.CAVE_GOBLIN_CITY_DOORR, new WorldPoint(3317, 9601, 0),
			"Go through the doors to Dorgesh-Kaan.", true, mithgrap, crossbow, lightsource);
		moveToDorgMovario.addAlternateObjects(ObjectID.CAVE_GOBLIN_CITY_DOORL);

		dorgStairsMovario = new ObjectStep(this, ObjectID.DORGESH_1STAIRS_POSH, new WorldPoint(2721, 5360, 0),
			"Climb the stairs to the second level of Dorgesh-Kaan.", mithgrap, crossbow, lightsource);
		dorgStairsChest = new ObjectStep(this, ObjectID.DORGESH_1STAIRS_POSH, new WorldPoint(2721, 5360, 0),
			"Climb the stairs to the second level of Dorgesh-Kaan.", lockpick);

		richChest = new ObjectStep(this, ObjectID.DORGESH_RICH_CHEST_CLOSED, new WorldPoint(2703, 5348, 1),
			"Lockpick the chest.", lockpick);
		moveToDorgAgi = new ObjectStep(this, ObjectID.DORGESH_2STAIRS_POSH, new WorldPoint(2723, 5253, 1),
			"Climb the stairs to enter the Dorgesh-Kaan agility course.");
		movario = new NpcStep(this, NpcID.DORGESH_DARK_WIZARD_THERE, new WorldPoint(2706, 5237, 3),
			"Pickpocket Movario near the end of the agility course.");

		chopMagic = new ObjectStep(this, ObjectID.MAGICTREE, new WorldPoint(3357, 3312, 0),
			"Chop some magic logs near the Magic Training Arena.", axe);

		claimReward = new NpcStep(this, NpcID.HATIUS_LUMBRIDGE_DIARY, new WorldPoint(3235, 3213, 0),
			"Talk to Hatius Cosaintus in Lumbridge to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(qcCape, lockpick, mithgrap, hammer, waterAccessOrAbyss, axe, addyBar.quantity(5),
			essence.quantity(28), crossbow);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(ringOfDueling, dorgSphere);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new SkillRequirement(Skill.AGILITY, 70, true));
		reqs.add(new SkillRequirement(Skill.RANGED, 70, true));
		reqs.add(new ComplexRequirement(LogicType.OR, "76 Runecraft or 57 with Raiments of the Eye set",
			new SkillRequirement(Skill.RUNECRAFT, 76, true, "76 Runecraft"),
			new ItemRequirements("57 with Raiments of the Eye set",
				new ItemRequirement("Hat", ItemCollections.EYE_HAT).alsoCheckBank(questBank),
				new ItemRequirement("Top", ItemCollections.EYE_TOP).alsoCheckBank(questBank),
				new ItemRequirement("Bottom", ItemCollections.EYE_BOTTOM).alsoCheckBank(questBank),
				new ItemRequirement("Boot", ItemID.BOOTS_OF_THE_EYE)).alsoCheckBank(questBank)
		));
		reqs.add(new SkillRequirement(Skill.SMITHING, 88, true));
		reqs.add(new SkillRequirement(Skill.STRENGTH, 70, true));
		reqs.add(new SkillRequirement(Skill.THIEVING, 78, true));
		reqs.add(new SkillRequirement(Skill.WOODCUTTING, 75, true));

		reqs.add(allQuests);

		return reqs;
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
			new ItemReward("Explorer's ring 4", ItemID.LUMBRIDGE_RING_ELITE),
			new ItemReward("50,000 Exp. Lamp (Any skill over 70)", ItemID.THOSF_REWARD_LAMP)
		);
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Arrays.asList(
			new UnlockReward("100% run energy replenish 3 times a day from Explorer's ring"),
			new UnlockReward("30 casts of High Level Alchemy per day (does not provide experience) from Explorer's ring"),
			new UnlockReward("20% discount on items in the Culinaromancer's Chest"),
			new UnlockReward("Ability to use Fairy rings without the need of a Dramen or Lunar staff"),
			new UnlockReward("Unlocked the 6th slot for blocking Slayer tasks")
		);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails adamantitePlatebodySteps = new PanelDetails("Adamantite Platebody",
			Arrays.asList(moveToDraySewer, addyPlatebody), new SkillRequirement(Skill.SMITHING, 88, true),
			addyBar.quantity(5), hammer);
		adamantitePlatebodySteps.setDisplayCondition(notAddyPlatebody);
		adamantitePlatebodySteps.setLockingStep(addyPlatebodyTask);
		allSteps.add(adamantitePlatebodySteps);

		PanelDetails questCapeEmoteSteps = new PanelDetails("Quest Cape Emote", Arrays.asList(moveToOldman, qcEmote),
			allQuests, qcCape);
		questCapeEmoteSteps.setDisplayCondition(notQCEmote);
		questCapeEmoteSteps.setLockingStep(qcEmoteTask);
		allSteps.add(questCapeEmoteSteps);

		PanelDetails richChestSteps = new PanelDetails("Dorgesh-Kaan Rich Chest", Arrays.asList(moveToUndergroundChest,
			moveToDorgChest, dorgStairsChest, richChest), new SkillRequirement(Skill.THIEVING, 78, true), deathToDorg,
			lightsource, lockpick);
		richChestSteps.setDisplayCondition(notRichChest);
		richChestSteps.setLockingStep(richChestTask);
		allSteps.add(richChestSteps);

		PanelDetails movarioSteps = new PanelDetails("Movario", Arrays.asList(moveToUndergroundMovario, moveToDorgMovario,
			dorgStairsMovario, moveToDorgAgi, movario), new SkillRequirement(Skill.THIEVING, 42, true),
			new SkillRequirement(Skill.AGILITY, 70, true), new SkillRequirement(Skill.RANGED, 70, true),
			new SkillRequirement(Skill.STRENGTH, 70, true), deathToDorg, templeOfIkov, mithgrap, crossbow, lightsource);
		movarioSteps.setDisplayCondition(notMovario);
		movarioSteps.setLockingStep(movarioTask);
		allSteps.add(movarioSteps);

		PanelDetails waterRunesSteps = new PanelDetails("140 Water Runes", Arrays.asList(moveToWater, waterRunes),
			new SkillRequirement(Skill.RUNECRAFT, 76, true), essence.quantity(28), waterAccessOrAbyss);
		waterRunesSteps.setDisplayCondition(notWaterRunes);
		waterRunesSteps.setLockingStep(waterRunesTask);
		allSteps.add(waterRunesSteps);

		PanelDetails chopMagicsSteps = new PanelDetails("Chop Magics", Collections.singletonList(chopMagic),
			new SkillRequirement(Skill.WOODCUTTING, 75, true), axe);
		chopMagicsSteps.setDisplayCondition(notChopMagic);
		chopMagicsSteps.setLockingStep(chopMagicTask);
		allSteps.add(chopMagicsSteps);

		allSteps.add(new PanelDetails("Finishing off", Collections.singletonList(claimReward)));

		return allSteps;
	}
}
