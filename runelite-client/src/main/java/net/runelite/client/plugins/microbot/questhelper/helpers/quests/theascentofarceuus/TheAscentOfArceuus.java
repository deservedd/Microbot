/*
 * Copyright (c) 2021, Zoinkwiz
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.theascentofarceuus;

import net.runelite.client.plugins.microbot.questhelper.bank.banktab.BankSlotIcons;
import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.npc.NpcHintArrowRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.InInstanceRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.microbot.questhelper.steps.NpcStep;
import net.runelite.client.plugins.microbot.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class TheAscentOfArceuus extends BasicQuestHelper
{
	// Recommended
	ItemRequirement combatGear, dramenStaff, battlefrontTeleports2, xericsTalisman, skillsNecklace;

	Requirement inTowerInstance, inTowerF1, inKaruulm, inCastle, foundTrack1, foundTrack2, foundTrack3,
		foundTrack4, foundTrack5, trappedSoulNearby;

	QuestStep talkToMori, goUpToAndrews, talkToAndrews, returnToMori, enterTowerOfMagic, killTormentedSouls,
		goUpstairsTowerOfMagic, talkToArceuus, enterKaruulm, talkToKaal, leaveKaal, inspectGrave, inspectTrack1,
		inspectTrack2, inspectTrack3, inspectTrack4, inspectTrack5, inspectTrack6, killTrappedSoul, enterKaruulmAgain,
		talkToKaalAgain, searchRocks, goUpstairsInTowerToFinish, talkToArceuusToFinish;

	Zone towerF0, towerF1, karuulm, castle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMori);

		ConditionalStep goTalkToAndrews = new ConditionalStep(this, goUpToAndrews);
		goTalkToAndrews.addStep(inCastle, talkToAndrews);
		steps.put(1, goTalkToAndrews);

		steps.put(2, returnToMori);

		ConditionalStep goDefeatSouls = new ConditionalStep(this, enterTowerOfMagic);
		goDefeatSouls.addStep(inTowerInstance, killTormentedSouls);
		steps.put(3, goDefeatSouls);
		steps.put(4, goDefeatSouls);

		ConditionalStep goTalkToArceuus = new ConditionalStep(this, goUpstairsTowerOfMagic);
		goTalkToArceuus.addStep(inTowerF1, talkToArceuus);
		steps.put(5, goTalkToArceuus);
		steps.put(6, goTalkToArceuus);

		ConditionalStep goTalkToKaal = new ConditionalStep(this, enterKaruulm);
		goTalkToKaal.addStep(inKaruulm, talkToKaal);
		steps.put(7, goTalkToKaal);

		ConditionalStep goInspectGrave = new ConditionalStep(this, inspectGrave);
		goInspectGrave.addStep(inKaruulm, leaveKaal);
		steps.put(8, goInspectGrave);

		ConditionalStep trackingSteps = new ConditionalStep(this, inspectTrack1);
		trackingSteps.addStep(foundTrack5, inspectTrack6);
		trackingSteps.addStep(foundTrack4, inspectTrack5);
		trackingSteps.addStep(foundTrack3, inspectTrack4);
		trackingSteps.addStep(foundTrack2, inspectTrack3);
		trackingSteps.addStep(foundTrack1, inspectTrack2);
		steps.put(9, trackingSteps);

		ConditionalStep goKillTrappedSoul = new ConditionalStep(this, inspectTrack6);
		goKillTrappedSoul.addStep(trappedSoulNearby, killTrappedSoul);
		steps.put(10, goKillTrappedSoul);

		ConditionalStep goReturnToKaal = new ConditionalStep(this, enterKaruulmAgain);
		goReturnToKaal.addStep(inKaruulm, talkToKaalAgain);
		steps.put(11, goReturnToKaal);

		steps.put(12, searchRocks);

		ConditionalStep goFinishQuest = new ConditionalStep(this, goUpstairsInTowerToFinish);
		goFinishQuest.addStep(inTowerF1, talkToArceuusToFinish);
		steps.put(13, goFinishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		dramenStaff = new ItemRequirement("Access to Fairy Rings", ItemID.DRAMEN_STAFF).isNotConsumed();
		dramenStaff.addAlternates(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		battlefrontTeleports2 = new ItemRequirement("Battlefront teleports", ItemID.TELETAB_BATTLEFRONT, 2);
		xericsTalisman = new ItemRequirement("Xeric's Talisman", ItemID.XERIC_TALISMAN).isNotConsumed();
		skillsNecklace = new ItemRequirement("Skills necklace", ItemCollections.SKILLS_NECKLACES).isNotConsumed();

		combatGear = new ItemRequirement("Combat gear", -1, -1).isNotConsumed();
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	@Override
	protected void setupZones()
	{
		towerF0 = new Zone(new WorldPoint(1563, 3802, 0), new WorldPoint(1595, 3836, 0));
		towerF1 = new Zone(new WorldPoint(1563, 3802, 1), new WorldPoint(1595, 3836, 1));
		castle = new Zone(new WorldPoint(1591, 3654, 1), new WorldPoint(1628, 3692, 1));
		karuulm = new Zone(new WorldPoint(1249, 10144, 0), new WorldPoint(1385, 10286, 0));
	}

	public void setupConditions()
	{
		inTowerInstance = new Conditions(new InInstanceRequirement(), new ZoneRequirement(towerF0));
		inTowerF1 = new ZoneRequirement(towerF1);
		inCastle = new ZoneRequirement(castle);
		inKaruulm = new ZoneRequirement(karuulm);

		foundTrack1 = new VarbitRequirement(7860, 1);
		foundTrack2 = new VarbitRequirement(7861, 1);
		foundTrack3 = new VarbitRequirement(7862, 1);
		foundTrack4 = new VarbitRequirement(7863, 1);
		foundTrack5 = new VarbitRequirement(7864, 1);

		trappedSoulNearby = new NpcHintArrowRequirement(NpcID.ARCQUEST_SOUL);
		// Inspected grave:
		// 7859 0->1
		// 7856 8->9
	}

	public void setupSteps()
	{
		talkToMori = new NpcStep(this, NpcID.ARCQUEST_MORI_VISIBLE, new WorldPoint(1698, 3742, 0), "Talk to Mori in Arceuus.");
		talkToMori.addDialogSteps("What can I do to help?", "We should let someone know about this.","Yes.",
			"Of course I'll help.");

		goUpToAndrews = new ObjectStep(this, ObjectID.FAI_VARROCK_STAIRS_TALLER_NEW_FIX, new WorldPoint(1616, 3681, 0),
			"Talk to Councillor Andrews in Kourend Castle.");
		talkToAndrews = new NpcStep(this, NpcID.ZEAH_HEAD_COUNCILLOR_VIS, new WorldPoint(1620, 3673, 1),
			"Talk to Councillor Andrews in Kourend Castle.");
		talkToAndrews.addDialogStep("There's been a death in Arceuus.");
		talkToAndrews.addSubSteps(goUpToAndrews);

		returnToMori = new NpcStep(this, NpcID.ARCQUEST_MORI_VISIBLE, new WorldPoint(1698, 3742, 0), "Return to Mori in Arceuus.");
		returnToMori.addDialogStep("What should we do now?");

		enterTowerOfMagic = new ObjectStep(this, ObjectID.ARCQUEST_TOWER_DOOR_RIGHT, new WorldPoint(1596, 3820, 0), "Enter the Tower" +
			" of Magic in Arceuus, ready to fight some level 16 Tormented Souls.", combatGear);
		enterTowerOfMagic.addDialogStep("Yes.");

		killTormentedSouls = new NpcStep(this, NpcID.ARCQUEST_GHOST1, new WorldPoint(1585, 3821, 0),
			"Defeat the tormented souls.", true, combatGear);
		((NpcStep) killTormentedSouls).addAlternateNpcs(NpcID.ARCQUEST_GHOST2);

		goUpstairsTowerOfMagic = new ObjectStep(this, ObjectID.ARCQUEST_STAIRS_LOWER_LEFT, new WorldPoint(1585, 3821, 0),
			"Go up the stairs in the Tower of Magic.");

		talkToArceuus = new NpcStep(this, NpcID.TROBIN_ARCEUUS_FOCUS_VISIBLE, new WorldPoint(1580, 3821, 1),
			"Talk to Lord Trobin Arceuus.");
		((NpcStep) talkToArceuus).addAlternateNpcs(NpcID.TROBIN_ARCEUUS_VISIBLE);

		enterKaruulm = new ObjectStep(this, ObjectID.BRIMSTONE_ELEVATOR_CENTRAL_TILE, new WorldPoint(1311, 3807, 0),
			"Go down the elevator on Mount Karuulm.");

		talkToKaal = new NpcStep(this, NpcID.TASAKAAL_KET, new WorldPoint(1312, 10211, 0), "Talk to Kaal-Ket-Jor.");


		leaveKaal = new ObjectStep(this, ObjectID.BRIMSTONE_DUNGEON_EXIT, new WorldPoint(1312, 10186, 0),
			"Inspect the ancient grave south of Mount Karuulm.");

		inspectGrave = new ObjectStep(this, ObjectID.ARCQUEST_GRAVE, new WorldPoint(1349, 3737, 0),
			"Inspect the ancient grave south of Mount Karuulm.", combatGear);
		inspectGrave.addSubSteps(leaveKaal);

		inspectTrack1 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_BUSH, new WorldPoint(1335, 3743, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack2 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_PLANT, new WorldPoint(1317, 3750, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack3 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_PLANT, new WorldPoint(1305, 3750, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack4 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_TREE_STUMP, new WorldPoint(1288, 3751, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack5 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_PLANT2, new WorldPoint(1286, 3738, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack1.addSubSteps(inspectTrack2, inspectTrack3, inspectTrack4, inspectTrack5);

		inspectTrack6 = new ObjectStep(this, ObjectID.ARCQUEST_HUNTING_END, new WorldPoint(1282, 3726, 0),
			"Inspect the final plant and kill the Trapped Soul (level 30) which appears.", combatGear);
		killTrappedSoul = new NpcStep(this, NpcID.ARCQUEST_SOUL, new WorldPoint(1281, 3724, 0),
			"Kill the Trapped Soul.");
		inspectTrack6.addSubSteps(killTrappedSoul);

		enterKaruulmAgain = new ObjectStep(this, ObjectID.BRIMSTONE_ELEVATOR_CENTRAL_TILE, new WorldPoint(1311, 3807, 0),
			"Return to Kaal-Ket-Jor.");
		talkToKaalAgain = new NpcStep(this, NpcID.TASAKAAL_KET, new WorldPoint(1312, 10211, 0),
			"Return to Kaal-Ket-Jor.");
		talkToKaalAgain.addSubSteps(enterKaruulmAgain);

		searchRocks = new ObjectStep(this, ObjectID.ARCQUEST_ROCKS_2_OP, new WorldPoint(1710, 3882, 0), "Inspect the rocks near the Arceuus Altar until you find a device.", true);
		((ObjectStep) searchRocks).addAlternateObjects(ObjectID.ARCQUEST_ROCKS_1_OP);

		goUpstairsInTowerToFinish = new ObjectStep(this, ObjectID.ARCQUEST_STAIRS_LOWER_LEFT, new WorldPoint(1585, 3821, 0),
			"Return to Lord Trobin Arceuus to finish the quest.");
		talkToArceuusToFinish = new NpcStep(this, NpcID.TROBIN_ARCEUUS_VISIBLE, new WorldPoint(1580, 3821, 1),
			"Talk to Lord Trobin Arceuus to finish the quest.");
		talkToArceuusToFinish.addSubSteps(goUpstairsInTowerToFinish);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(combatGear, dramenStaff, battlefrontTeleports2, xericsTalisman, skillsNecklace);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Arrays.asList("5 Tormented Souls (level 16)", "Trapped Soul (level 30)");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED));
		req.add(new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED));
		req.add(new SkillRequirement(Skill.HUNTER, 12));
		return req;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Arrays.asList(
				new ExperienceReward(Skill.HUNTER, 1500),
				new ExperienceReward(Skill.RUNECRAFT, 500));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("Coins", ItemID.COINS, 2000),
				new ItemReward("A Kharedst's Memoirs page", ItemID.VEOS_KHAREDSTS_MEMOIRS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			Arrays.asList(talkToMori, talkToAndrews, returnToMori, enterTowerOfMagic,
				killTormentedSouls, goUpstairsTowerOfMagic, talkToArceuus)));
		allSteps.add(new PanelDetails("Freeing a Soul",
			Arrays.asList(talkToKaal, inspectGrave, inspectTrack1, inspectTrack6, talkToKaalAgain),
			combatGear));
		allSteps.add(new PanelDetails("Saving Arceuus",
			Arrays.asList(searchRocks, talkToArceuusToFinish)));
		return allSteps;
	}
}
