/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.ragandboneman;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.collections.KeyringCollection;
import net.runelite.client.plugins.microbot.questhelper.managers.QuestContainerManager;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestVarPlayer;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestVarbits;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemOnTileRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.KeyringRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.npc.NpcInteractingRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicType;
import net.runelite.client.plugins.microbot.questhelper.requirements.util.Operation;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarplayerRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.client.plugins.microbot.questhelper.tools.QuestTile;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.runelite.client.plugins.microbot.questhelper.requirements.util.LogicHelper.nor;

public class RagAndBoneManII extends BasicQuestHelper
{
	//Items Required
	ItemRequirement coins, pots, logs, tinderbox, lightSource, rope, dustyKey, mirrorShield, iceCooler,
	fishingExplosive, coinsOrVinegar;

	//Items Recommended
	ItemRequirement antifireShield, inoculationBracelet, digsitePendant, ectophial, ringOfDueling,
		gamesNecklace, varrockTeleport, lumbridgeTeleport, nardahTeleport, draynorTeleport,
		karamjaTeleport, taverleyTeleport, rellekkaTeleport, gnomeTeleport, feldipTeleport, dramenStaff,
		rellekkaNETeleport, rangedWeapon;

	ItemRequirement jugOfVinegar, jugOfVinegarNeeded, potOfVinegar, potOfVinegarNeeded, potNeeded, axe, jailKey,
		boneInVinegar;

	DetailedQuestStep finishP1;

	DetailedQuestStep killBat, killUndeadCow, killExperiment, killWerewolf, killGhoul, enterSewer, killZombie,
		killRat, killMossGiant, killCaveGoblin, killJackal, killLizard, killVulture, killSeagull,
		enterAsgarniaDungeon, killIceGiant, killMogre, killSnake, killJogre, enterBrimhavenDungeon, killFireGiant,
		enterTaverleyDungeon, killBabyBlueDragon, killTroll, killRabbit, enterFremmyDungeon, killBasilisk,
		travelToWaterbirth, enterWaterbirthDungeon, killDagannoth, killTerrorbird, killWolf, killOgre, killZogre,
		pickupBone;

	// Experiment
	DetailedQuestStep enterExperimentCave;

	// Mogre
	DetailedQuestStep throwExplosive;

	// Blue dragon
	DetailedQuestStep goThroughPipe, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley, pickUpJailKey;

	// Cave goblin
	DetailedQuestStep addRope, enterSwamp, leaveJunaRoom;

	DetailedQuestStep enterKeldagrimCave;

	DetailedQuestStep makePotOfVinegar, useBonesOnVinegar;

	DetailedQuestStep placeLogs, useBoneOnBoiler, lightLogs, waitForCooking, removePot, repeatSteps;

	DetailedQuestStep giveBones, talkToFinish;

	Requirement inSwamp, inJunaRoom, inExperimentCave, inVarrockSewer, inAsgarniaDungeon, inBrimhavenDungeon,
		inTaverleyDungeon, inTrollCave, inFremennikSlayerDungeon, onWaterbirth, inWaterbirthDungeon, addedRope,
		inDeepTaverleyDungeon, inJailCell, boneNearby, hadAllBones, jailKeyOnFloor, mogreNearby;

	Requirement hadVinegar, allBonesAtLeastAddedToVinegar, allBonesPolished;

	Requirement logAdded, boneAddedToBoiler, logLit, boneReady;

	Zone swamp, junaRoom, experimentCave, varrockSewer, asgarniaDungeon, brimhavenDungeon,
		taverleyDungeon, trollCave, fremennikSlayerDungeon, waterbirth, waterbirthDungeon, deepTaverleyDungeon1,
		deepTaverleyDungeon2, deepTaverleyDungeon3, deepTaverleyDungeon4, jailCell;

	ConditionalStep morySteps, varrockSteps, lumbridgeSteps, desertSteps, sarimSteps, karamjaSteps, taverleySteps,
	fremennikSteps, strongholdSteps, feldipSteps;

	ConditionalStep collectBonesSteps, preparingBonesSteps, cookingSteps;

	HashMap<RagBoneState, QuestStep> stepsForRagAndBoneManII = new LinkedHashMap<>();

	HashMap<RagBoneState, QuestStep> mapForMory = new LinkedHashMap<>();
	List<Requirement> moryReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForVarrock = new LinkedHashMap<>();
	List<Requirement> varrockReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForLumbridge = new LinkedHashMap<>();
	List<Requirement> lumbridgeReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForDesert = new LinkedHashMap<>();
	List<Requirement> desertReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForSarim = new LinkedHashMap<>();
	List<Requirement> sarimReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForKaramja = new LinkedHashMap<>();
	List<Requirement> karamjaReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForTaverley = new LinkedHashMap<>();
	List<Requirement> taverleyReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForFremennik = new LinkedHashMap<>();
	List<Requirement> fremennikReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForStronghold = new LinkedHashMap<>();
	List<Requirement> strongholdReqsList = new ArrayList<>();
	HashMap<RagBoneState, QuestStep> mapForFeldip = new LinkedHashMap<>();
	List<Requirement> feldipReqsList = new ArrayList<>();

	Conditions moryReqs, varrockReqs, lumbridgeReqs, desertReqs, sarimReqs, karamjaReqs, taverleyReqs, fremennikReqs,
		strongholdReqs, feldipReqs;

	ConditionalStep pickupBoneSteps;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		setupConditionalSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		preparingBonesSteps = new ConditionalStep(this, makePotOfVinegar);
		preparingBonesSteps.addStep(potOfVinegarNeeded, useBonesOnVinegar);
		preparingBonesSteps.setLockingCondition(allBonesAtLeastAddedToVinegar);

		cookingSteps = new ConditionalStep(this, placeLogs);
		cookingSteps.addStep(boneReady, removePot);
		cookingSteps.addStep(logLit, waitForCooking);
		cookingSteps.addStep(boneAddedToBoiler, lightLogs);
		cookingSteps.addStep(logAdded, useBoneOnBoiler);
		cookingSteps.setLockingCondition(allBonesPolished);

		collectBonesSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state general."));
		collectBonesSteps.addStep(boneNearby, pickupBoneSteps);
		collectBonesSteps.addStep(allBonesPolished, giveBones);
		collectBonesSteps.addStep(allBonesAtLeastAddedToVinegar, cookingSteps);
		collectBonesSteps.addStep(hadAllBones, preparingBonesSteps);
		collectBonesSteps.addStep(nor(moryReqs), morySteps);
		collectBonesSteps.addStep(nor(varrockReqs), varrockSteps);
		collectBonesSteps.addStep(nor(lumbridgeReqs), lumbridgeSteps);
		collectBonesSteps.addStep(nor(desertReqs), desertSteps);
		collectBonesSteps.addStep(nor(sarimReqs), sarimSteps);
		collectBonesSteps.addStep(nor(karamjaReqs), karamjaSteps);
		collectBonesSteps.addStep(nor(taverleyReqs), taverleySteps);
		collectBonesSteps.addStep(nor(fremennikReqs), fremennikSteps);
		collectBonesSteps.addStep(nor(strongholdReqs), strongholdSteps);
		collectBonesSteps.addStep(nor(feldipReqs), feldipSteps);
		collectBonesSteps.setLockingCondition(hadAllBones);

		steps.put(0, finishP1);
		steps.put(1, finishP1);
		steps.put(2, finishP1);
		steps.put(3, finishP1);
		steps.put(4, collectBonesSteps);

		steps.put(5, talkToFinish);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		// Required items
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		pots = new ItemRequirement("Pot", ItemID.POT_EMPTY).isNotConsumed();
		potNeeded = new ItemRequirement("Pot", ItemID.POT_EMPTY, 8).alsoCheckBank(questBank).highlighted().isNotConsumed();
		logs = new ItemRequirement("Logs", ItemID.LOGS);
		tinderbox = new ItemRequirement("Tinderbox", ItemID.TINDERBOX).isNotConsumed();
		lightSource = new ItemRequirement("Light source", ItemCollections.LIGHT_SOURCES).isNotConsumed();
		dustyKey = new KeyringRequirement("Dusty Key", configManager, KeyringCollection.DUSTY_KEY).isNotConsumed();
		dustyKey.canBeObtainedDuringQuest();
		mirrorShield = new ItemRequirement("Mirror shield", ItemID.SLAYER_MIRROR_SHIELD).isNotConsumed();
		mirrorShield.addAlternates(ItemID.VIKINGEXILE_V_SHIELD, ItemID.V_SHIELD);
		iceCooler = new ItemRequirement("Ice coolers", ItemID.SLAYER_ICY_WATER, 10);
		fishingExplosive = new ItemRequirement("Fishing explosive", ItemID.FISHING_EXPLOSIVE, 10);
		fishingExplosive.addAlternates(ItemID.SLAYERGUIDE_FISHING_EXPLOSIVE);
		axe = new ItemRequirement("Any axe", ItemCollections.AXES).isNotConsumed();

		// Optional items
		rope = new ItemRequirement("Rope", ItemID.ROPE);
		varrockTeleport = new ItemRequirement("Varrock teleport", ItemID.POH_TABLET_VARROCKTELEPORT);
		lumbridgeTeleport = new ItemRequirement("Lumbridge teleport", ItemID.POH_TABLET_LUMBRIDGETELEPORT);
		digsitePendant = new ItemRequirement("Digsite pendant", ItemCollections.DIGSITE_PENDANTS);
		draynorTeleport = new ItemRequirement("Draynor teleport", ItemCollections.AMULET_OF_GLORIES);
		draynorTeleport.addAlternates(ItemID.TELETAB_DRAYNOR);
		karamjaTeleport = new ItemRequirement("Karamja teleport", ItemCollections.AMULET_OF_GLORIES);
		karamjaTeleport.addAlternates(ItemID.NZONE_TELETAB_BRIMHAVEN, ItemID.TELEPORTSCROLL_TAIBWO);

		antifireShield = new ItemRequirement("Antifire shield", ItemCollections.ANTIFIRE_SHIELDS).isNotConsumed();
		inoculationBracelet = new ItemRequirement("Inoculation bracelet or a potion for Disease",
			ItemCollections.ANTIDISEASE);
		ectophial = new ItemRequirement("Ectophial", ItemID.ECTOPHIAL).isNotConsumed();
		ringOfDueling = new ItemRequirement("Ring of dueling", ItemCollections.RING_OF_DUELINGS);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		nardahTeleport = new ItemRequirement("Nardah teleport", ItemID.TELEPORTSCROLL_NARDAH);
		nardahTeleport.addAlternates(ItemCollections.PHAROAH_SCEPTRE);
		taverleyTeleport = new ItemRequirement("Taverley teleport", ItemID.NZONE_TELETAB_TAVERLEY);
		taverleyTeleport.addAlternates(ItemID.POH_TABLET_FALADORTELEPORT);
		rellekkaTeleport = new ItemRequirement("Rellekka teleport", ItemCollections.ENCHANTED_LYRE);
		rellekkaTeleport.addAlternates(ItemID.NZONE_TELETAB_RELLEKKA);
		gnomeTeleport = new ItemRequirement("Teleport to Gnome Stronghold (Spirit tree/Gnome Glider", -1, 1);
		feldipTeleport = new ItemRequirement("Teleport to Feldip Hills (Gnome Glider or Fairy Ring (AKS))", ItemID.TELEPORTSCROLL_FELDIP);

		dramenStaff = new ItemRequirement("Dramen staff", ItemID.DRAMEN_STAFF).isNotConsumed();
		dramenStaff.addAlternates(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF);
		rellekkaNETeleport = new ItemRequirement("Fairy Ring (DKS)", ItemCollections.FAIRY_STAFF).isNotConsumed();

		rangedWeapon = new ItemRequirement("Ranged weapon for killing vultures", ItemCollections.CROSSBOWS);
		rangedWeapon.setTooltip("Vultures can fly, making them unable to be attacked with melee");
		rangedWeapon.addAlternates(ItemCollections.BOWS);

		// Quest items
		jugOfVinegar = new ItemRequirement("Jar of vinegar", ItemID.RAG_VINEGAR);
		potOfVinegar = new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR);
		potOfVinegarNeeded =
			new ItemRequirement("Pot of vinegar", ItemID.RAG_POT_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
		jugOfVinegarNeeded =
			new ItemRequirement("Jug of vinegar", ItemID.RAG_VINEGAR, 8).alsoCheckBank(questBank).highlighted();
		coinsOrVinegar = new ItemRequirement("Pots/Jugs of vinegar, or coins to buy", ItemID.RAG_POT_VINEGAR);
		coinsOrVinegar.addAlternates(ItemID.RAG_VINEGAR, ItemID.COINS);

		List<Integer> bonesInVinegar = new ArrayList<>();
		for (int i = ItemID.RAG_POT_GOBLIN_BONE; i <= ItemID.RAG_POT_JACKAL_BONE; i+=3)
		{
			bonesInVinegar.add(i);
		}
		boneInVinegar = new ItemRequirement("Bone in vinegar", ItemID.RAG_POT_GOBLIN_BONE);
		boneInVinegar.addAlternates(bonesInVinegar);

		jailKey = new ItemRequirement("Jail key", ItemID.JAIL_KEY);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		AtomicInteger winesNeededQuantity = new AtomicInteger(27);

		stepsForRagAndBoneManII.forEach((RagBoneState state, QuestStep step) -> {
			if (state.hadBoneInVinegarItem(questBank).check(client))
			{
				winesNeededQuantity.getAndDecrement();
			}
		});

		potOfVinegarNeeded.setQuantity(winesNeededQuantity.get());

		int jugsNeeded = winesNeededQuantity.get();
		jugsNeeded -= potOfVinegar.checkTotalMatchesInContainers(QuestContainerManager.getEquippedData(), QuestContainerManager.getInventoryData(), QuestContainerManager.getBankData());
		potNeeded.setQuantity(jugsNeeded);
		jugOfVinegarNeeded.setQuantity(jugsNeeded);

		// Need to know how many pots of vinegar needed, and if missing some
	}

	@Override
	protected void setupZones()
	{
		swamp = new Zone(new WorldPoint(3138, 9536, 0), new WorldPoint(3261, 9601, 0));
		junaRoom = new Zone(new WorldPoint(3205, 9484, 0), new WorldPoint(3263, 9537, 2));

		experimentCave = new Zone(new WorldPoint(3466, 9921, 0), new WorldPoint(3582, 9982, 0));
		varrockSewer = new Zone(new WorldPoint(3086, 9821, 0), new WorldPoint(3290, 9919, 0));
		asgarniaDungeon = new Zone(new WorldPoint(2979, 9538, 0), new WorldPoint(3069, 9602, 0));
		brimhavenDungeon = new Zone(new WorldPoint(2560, 9411, 0), new WorldPoint(2752, 9599, 2));

		taverleyDungeon = new Zone(new WorldPoint(2816, 9668, 0), new WorldPoint(2973, 9855, 0));
		deepTaverleyDungeon1 = new Zone(new WorldPoint(2816, 9856, 0), new WorldPoint(2880, 9760, 0));
		deepTaverleyDungeon2 = new Zone(new WorldPoint(2880, 9760, 0), new WorldPoint(2907, 9793, 0));
		deepTaverleyDungeon3 = new Zone(new WorldPoint(2889, 9793, 0), new WorldPoint(2923, 9815, 0));
		deepTaverleyDungeon4 = new Zone(new WorldPoint(2907, 9772, 0), new WorldPoint(2928, 9793, 0));
		jailCell = new Zone(new WorldPoint(2928, 9683, 0), new WorldPoint(2934, 9689, 0));

		trollCave = new Zone(new WorldPoint(2762, 10123, 0), new WorldPoint(2804, 10164, 0));
		fremennikSlayerDungeon = new Zone(new WorldPoint(2688, 9984, 0), new WorldPoint(2811, 10047, 0));
		waterbirth = new Zone(new WorldPoint(2496, 3712, 0), new WorldPoint(2559, 3774, 0));
		waterbirthDungeon = new Zone(new WorldPoint(2432, 10113, 0), new WorldPoint(2559, 10175, 0));
	}

	private void setupConditions()
	{
		inSwamp = new ZoneRequirement(swamp);
		inJunaRoom = new ZoneRequirement(junaRoom);
		inExperimentCave = new ZoneRequirement(experimentCave);
		inVarrockSewer = new ZoneRequirement(varrockSewer);
		inAsgarniaDungeon = new ZoneRequirement(asgarniaDungeon);
		inBrimhavenDungeon = new ZoneRequirement(brimhavenDungeon);
		inTaverleyDungeon = new ZoneRequirement(taverleyDungeon);
		inDeepTaverleyDungeon = new ZoneRequirement(deepTaverleyDungeon1, deepTaverleyDungeon2, deepTaverleyDungeon3,
			deepTaverleyDungeon4);
		inJailCell = new ZoneRequirement(jailCell);

		inTrollCave = new ZoneRequirement(trollCave);
		inFremennikSlayerDungeon = new ZoneRequirement(fremennikSlayerDungeon);
		onWaterbirth = new ZoneRequirement(waterbirth);
		inWaterbirthDungeon = new ZoneRequirement(waterbirthDungeon);

		addedRope = new VarbitRequirement(279, 1);

		boneNearby = new Conditions(LogicType.OR,
			RagBoneGroups.getBonesOnFloor(RagBoneGroups.getBones(RagBoneGroups.getRagBoneIIStates())));

		logAdded = new VarbitRequirement(VarbitID.RAG_BOILER, 1, Operation.GREATER_EQUAL);
		boneAddedToBoiler = new VarbitRequirement(VarbitID.RAG_BOILER, 2, Operation.GREATER_EQUAL);
		logLit = new VarbitRequirement(VarbitID.RAG_BOILER, 3, Operation.GREATER_EQUAL);
		boneReady = new VarbitRequirement(2046, 4);

		jailKeyOnFloor = new ItemOnTileRequirement(jailKey);
		mogreNearby = new NpcInteractingRequirement(NpcID.MUDSKIPPER_OGRE);


		allBonesPolished = new Conditions(RagBoneGroups.allBonesPolished(RagBoneGroups.getRagBoneIIStates(),
			questBank));

		allBonesAtLeastAddedToVinegar =
			new Conditions(RagBoneGroups.allBonesAddedToVinegar(RagBoneGroups.getRagBoneIIStates(), questBank));

		hadAllBones = new Conditions(RagBoneGroups.allBonesObtained(RagBoneGroups.getRagBoneIIStates(), questBank));

		hadVinegar = new Conditions(jugOfVinegar.alsoCheckBank(questBank));

		// TODO: Setup check for marking bones 'done' if not on list
		// Widget 220, 7-15, check Text
	}

	public void setupSteps()
	{
		finishP1 = new DetailedQuestStep(this, "Finish Rag and Bone Man I.");
		addRope = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", rope.highlighted(), lightSource, tinderbox);
		addRope.addIcon(ItemID.ROPE);
		leaveJunaRoom = new ObjectStep(this, ObjectID.TOG_CAVE_UP, new WorldPoint(3219, 9534, 2),
			"Enter the Lumbridge Swamp caves.");
		enterSwamp = new ObjectStep(this, ObjectID.GOBLIN_CAVE_ENTRANCE, new WorldPoint(3169, 3172, 0),
			"Enter the hole to the Lumbridge Swamp caves.", lightSource, tinderbox);
		enterSwamp.addSubSteps(addRope, leaveJunaRoom);

		killBat = new NpcStep(this, NpcID.SMALL_BAT, new WorldPoint(3367, 3486, 0),
			"Kill bats south of the Odd Old Man.");

		killUndeadCow = new NpcStep(this, NpcID.AHOY_UNDEAD_COW, new WorldPoint(3617, 3526, 0),
			"Kill undead cows west of the Ectofuntus.", true);
		killUndeadCow.addTeleport(ectophial);

		enterExperimentCave = new ObjectStep(this, ObjectID.FENK_COFFIN, new WorldPoint(3578, 3528, 0),
			"Enter the Experiment Cave near Castle Fenkenstrain.");
		killExperiment = new NpcStep(this, NpcID.FENK_EXPERIMENT_2, new WorldPoint(3557, 9946, 0),
			"Kill Experiments.", true);
		((NpcStep) killExperiment).addAlternateNpcs(NpcID.FENK_EXPERIMENT_3);
		killWerewolf = new NpcStep(this, NpcID.CANAFIS_WEREWOLF_WOMAN11, new WorldPoint(3491, 3487, 0),
			"Kill the citizens/werewolves in Canifis. (Do not use Wolfbane)", true);
		List<Integer> werewolves = new ArrayList<>();
		for (int i = NpcID.CANAFIS_WEREWOLF_MAN2; i <= NpcID.CANAFIS_WOMAN12; i++)
		{
			werewolves.add(i);
		}
		((NpcStep) killWerewolf).addAlternateNpcs(werewolves);

		killGhoul = new NpcStep(this, NpcID.GHOUL, new WorldPoint(3434, 3461, 0),
			"Kill Ghouls west of Canifis.", true);
		enterSewer = new ObjectStep(this, ObjectID.MANHOLEOPEN, new WorldPoint(3237, 3458, 0),
			"Go down into Varrock Sewer via the Manhole south east of Varrock Castle.");
		((ObjectStep) enterSewer).addAlternateObjects(ObjectID.MANHOLECLOSED);
		killZombie = new NpcStep(this, NpcID.ZOMBIE_UNARMED_SEWER4, new WorldPoint(3244, 9892, 0),
			"Kill zombies in the sewer.", true);
		((NpcStep) killZombie).addAlternateNpcs(NpcID.ZOMBIE_UNARMED_SEWER2);
		killRat = new NpcStep(this, NpcID.PITRAT_SARIM_DEF, new WorldPoint(3244, 9892, 0),
			"Kill rats.", true);
		killMossGiant = new NpcStep(this, NpcID.MOSSGIANT, new WorldPoint(2654, 9565, 0),
			"Kill Moss Giants near the entrance.", true);
		((NpcStep) killMossGiant).addAlternateNpcs(NpcID.MOSSGIANT2, NpcID.MOSSGIANT3,
			NpcID.MOSSGIANT4);
		killCaveGoblin = new NpcStep(this, NpcID.DORGESH_MALE_1, new WorldPoint(3248, 9574, 0),
			"Kill Cave Goblins in the east of the Lumbridge Caves. Run between the marked tiles to avoid the Wall " +
				"Beasts.", true);
		((NpcStep) killCaveGoblin).addAlternateNpcs(NpcID.CAVE_GOBLIN2, NpcID.CAVE_GOBLIN3,
			NpcID.CAVE_GOBLIN4);
		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3161, 9573, 0), SpriteID.OPTIONS_RUNNING));
		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3163, 9573, 0), SpriteID.OPTIONS_RUNNING));

		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3163, 9555, 0), SpriteID.OPTIONS_RUNNING));
		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3165, 9555, 0), SpriteID.OPTIONS_RUNNING));

		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3197, 9553, 0), SpriteID.OPTIONS_RUNNING));
		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3199, 9553, 0), SpriteID.OPTIONS_RUNNING));

		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3214, 9559, 0), SpriteID.OPTIONS_RUNNING));
		killCaveGoblin.addTileMarker(new QuestTile(new WorldPoint(3216, 9559, 0), SpriteID.OPTIONS_RUNNING));

		killJackal = new NpcStep(this, NpcID.ICS_LITTLE_JACKAL, new WorldPoint(3400, 2997, 0),
			"Kill Jackals north of Nardah.", true);
		killSnake = new NpcStep(this, NpcID.FEUD_DESERT_SNAKE, new WorldPoint(3400, 3035, 0),
			"Kill desert snakes north of Nardah.", true);
		killLizard = new NpcStep(this, NpcID.SLAYER_LIZARD_MASSIVE, new WorldPoint(3439, 3036, 0),
			"Kill the giant lizards north of Nardah.", true, iceCooler);
		killVulture = new NpcStep(this, NpcID.RAG_VULTURE, new WorldPoint(3348, 2875, 0),
			"Kill vultures south west of Nardah.", true, rangedWeapon);
		((NpcStep) killVulture).addAlternateNpcs(NpcID.RAG_VULTURE_FLYING);
		killSeagull = new NpcStep(this, NpcID.SARIM_SEAGULL_PIER, new WorldPoint(3033, 3235, 0),
			"Kill seagulls on the Port Sarim docks.", true);
		((NpcStep) killSeagull).addAlternateNpcs(NpcID.SARIM_SEAGULL_PIER_BIG);
		enterAsgarniaDungeon = new ObjectStep(this, ObjectID.FAI_TRAPDOOR, new WorldPoint(3008, 3150, 0), "Enter the " +
			"Asgarnia Dungeon by Mudskipper Point.");
		killIceGiant = new NpcStep(this, NpcID.ICEGIANT2, new WorldPoint(3059, 9576, 0),
			"Kill Ice Giants at the end of the dungeon.", true);
		((NpcStep) killIceGiant).addAlternateNpcs(NpcID.ICEGIANT3, NpcID.ICEGIANT_LOW_WANDERRANGE,
			NpcID.ICEGIANT_LOW_WANDERRANGE2);

		throwExplosive = new ObjectStep(this, ObjectID.OMINOUS_FISHING_SPOT_A, new WorldPoint(2982, 3113, 0),
			"Throw a fishing explosive to attract a Mogre on Mudskipper Point.", fishingExplosive.highlighted());
		throwExplosive.addIcon(ItemID.SLAYERGUIDE_FISHING_EXPLOSIVE);
		killMogre = new NpcStep(this, NpcID.MUDSKIPPER_OGRE, new WorldPoint(2988, 3111, 0),
			"Kill the Mogre.");
		killJogre = new NpcStep(this, NpcID.JOGRE, new WorldPoint(2921, 3051, 0),
			"Kill Jogres on Karamja.", true);
		enterBrimhavenDungeon = new ObjectStep(this, ObjectID.DUNGEON_TREE_OPEN, new WorldPoint(2745, 3155, 0),
			"Enter Brimhaven Dungeon.", axe, coins.quantity(875));
		enterBrimhavenDungeon.addDialogStep("Yes");
		killFireGiant = new NpcStep(this, NpcID.FIREGIANT_BIG, new WorldPoint(2662, 9494, 0),
			"Kill fire giants deep in the dungeon.", true);
		((NpcStep) killFireGiant).addAlternateNpcs(NpcID.FIREGIANT_BIG2, NpcID.FIREGIANT_BIG3,
			NpcID.FIREGIANT, NpcID.FIREGIANT2, NpcID.FIREGIANT3, NpcID.FIREGIANT_STRONGHOLDCAVE_1,
			NpcID.FIREGIANT_STRONGHOLDCAVE_2, NpcID.FIREGIANT_STRONGHOLDCAVE_3, NpcID.FIREGIANT_STRONGHOLDCAVE_4);

		if (client.getRealSkillLevel(Skill.AGILITY) >= 70)
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. Bring an antifire shield if you can.");
		}
		else
		{
			enterTaverleyDungeon = new ObjectStep(this, ObjectID.LADDER_OUTSIDE_TO_UNDERGROUND, new WorldPoint(2884, 3397, 0),
				"Go to Taverley Dungeon. Bring a dusty key if you have one, otherwise you can get one in the dungeon." +
					" Bring an antifire shield if you can.", dustyKey);
		}
		enterTaverleyDungeon.addTeleport(taverleyTeleport);

		goThroughPipe = new ObjectStep(this, ObjectID.TAVERLY_DUNGEON_PIPE_SC, new WorldPoint(2888, 9799, 0),
			"Squeeze through the obstacle pipe.");
		killJailerForKey = new NpcStep(this, NpcID.JAILER, new WorldPoint(2930, 9692, 0),
			"Travel through Taverley Dungeon until you reach the Black Knights' Base. Kill the Jailer in the east side of the base for a jail key.");
		pickUpJailKey = new ItemStep(this, "Pick up the jail key.", jailKey);
		getDustyFromAdventurer = new NpcStep(this, NpcID.VELRAK_THE_EXPLORER, new WorldPoint(2930, 9685, 0),
			"Use the jail key on the south door and talk to Velrak for a dusty key.", jailKey);
		getDustyFromAdventurer.addDialogStep("So... do you know anywhere good to explore?");
		getDustyFromAdventurer.addDialogStep("Yes please!");
		enterDeeperTaverley = new ObjectStep(this, ObjectID.DEEPDUNGEONDOOR, new WorldPoint(2924, 9803, 0),
			"Enter the gate to the deeper Taverley dungeon.", dustyKey);
		enterTaverleyDungeon.addSubSteps(goThroughPipe, killJailerForKey, getDustyFromAdventurer, enterDeeperTaverley);

		killBabyBlueDragon = new NpcStep(this, NpcID.BABYBLUEDRAGON, new WorldPoint(2906, 9802, 0),
			"Kill baby blue dragons. South east near the lava is a good spot without any adult dragons.", true);
		((NpcStep) killBabyBlueDragon).addAlternateNpcs(NpcID.BABYBLUEDRAGON2, NpcID.BABYBLUEDRAGON3);

		enterKeldagrimCave = new ObjectStep(this, ObjectID.TROLLROMANCE_STRONGHOLD_EXIT_TUNNEL, new WorldPoint(2732,
			3713, 0), "Enter the cave north east of Rellekka.");
		enterKeldagrimCave.addTeleport(rellekkaNETeleport);
		killTroll = new NpcStep(this, NpcID.DEATH_TROLL_MELEE1, new WorldPoint(2830, 10107, 0), "Kill trolls.", true);
		((NpcStep) killTroll).addAlternateNpcs(NpcID.DEATH_TROLL_MELEE2, NpcID.DEATH_TROLL_MELEE3,
			NpcID.DEATH_TROLL_MELEE4, NpcID.DEATH_TROLL_MELEE5, NpcID.DEATH_TROLL_MELEE6, NpcID.DEATH_TROLL_MELEE7);
		killRabbit = new NpcStep(this, NpcID.VIKING_BUNNY_1, new WorldPoint(2738, 3637, 0),
			"Kill bunnies south east of Rellekka.", true);
		((NpcStep) killRabbit).addAlternateNpcs(NpcID.VIKING_BUNNY_2);
		enterFremmyDungeon = new ObjectStep(this, ObjectID.SLAYER_DUNGEON_ENTRANCE, new WorldPoint(2798, 3615, 0),
			"Enter the Fremennik Slayer Dungeon.", mirrorShield.equipped());
		killBasilisk = new NpcStep(this, NpcID.SLAYER_BASILISK, new WorldPoint(2743, 10010, 0),
			"Kill basilisks in the middle of the dungeon.", true, mirrorShield.equipped(),
			new SkillRequirement(Skill.SLAYER,
			40,	true));
		travelToWaterbirth = new NpcStep(this, NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_ISLAND, new WorldPoint(2620, 3685, 0), "Travel to Waterbirth " +
			"Island.");
		((NpcStep) travelToWaterbirth).addAlternateNpcs(NpcID.VIKING_DAGGANOTH_CAVE_FERRYMAN_1OP, NpcID.VIKING_DAGGANOTH_CAVE_WATERBIRTH_ISLAND);
		enterWaterbirthDungeon = new ObjectStep(this, ObjectID.DAGANNOTH_CAVEENTRANCE_ROCK, new WorldPoint(2521, 3740, 0),
			"Enter waterbirth dungeon.");
		killDagannoth = new NpcStep(this, NpcID.DUNGEON_DAGGANOTH_MAINROOM, new WorldPoint(2452, 10146, 0),
			"Kill dagannoths.", true);
		killTerrorbird = new NpcStep(this, NpcID.TERRORBIRD, new WorldPoint(2379, 3433, 0),
			"Kill Terrorbirds in the Tree Gnome Stronghold.", true);

		((NpcStep) killTerrorbird).addAlternateNpcs(NpcID.TERRORBIRD2, NpcID.TERRORBIRD3);
		killWolf = new NpcStep(this, NpcID.WOLF, new WorldPoint(2591, 2966, 0),
			"Kill wolves in Feldip Hills.", true);
		killWolf.addTeleport(feldipTeleport);
		killOgre = new NpcStep(this, NpcID.OGRE, new WorldPoint(2570, 2975, 0),
			"Kill ogres in Feldip Hills.", true);
		killOgre.addTeleport(feldipTeleport);
		killZogre = new NpcStep(this, NpcID.ZOGRE_1, new WorldPoint(2460, 3048, 0),
			"Kill Zogres in Jiggig.", true, inoculationBracelet);
		((NpcStep) killZogre).addAlternateNpcs(NpcID.ZOGRE_2, NpcID.ZOGRE_3, NpcID.ZOGRE_4, NpcID.ZOGRE_5,
			NpcID.ZOGRE_6, NpcID.ZOGRE_DANCE_1, NpcID.ZOGRE_DANCE_2, NpcID.ZOGRE_DANCE_3, NpcID.ZOGRE_DANCE_4);
		killZogre.addTeleport(feldipTeleport);

		pickupBone = new ItemStep(this, "Pickup the bone.");
		pickupBone.addItemRequirements(RagBoneGroups.pickupBones(RagBoneGroups.getRagBoneIIStates()));
		pickupBone.setShowInSidebar(false);

		makePotOfVinegar = new DetailedQuestStep(this, "Buy 27 jugs of vinegar from Fortunato in Draynor Village, " +
			"then use them on pots.",
			jugOfVinegarNeeded, potNeeded);

		useBonesOnVinegar = new DetailedQuestStep(this, "Use the bones on the pots of vinegar.", potOfVinegar.highlighted());
		useBonesOnVinegar.addItemRequirements(RagBoneGroups.bonesToAddToVinegar(RagBoneGroups.getRagBoneIIStates(), questBank));

		placeLogs = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Place logs under the pot-boiler near the Odd Old Man. If you've already polished all the bones, hand " +
				"them in to the Odd Old Man.", logs.highlighted());
		placeLogs.addIcon(ItemID.LOGS);

		useBoneOnBoiler = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Add a bone to the pot boiler.", boneInVinegar.highlighted());
		useBoneOnBoiler.addIcon(ItemID.RAG_POT_GOBLIN_BONE);
		lightLogs = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Light the logs under the pot-boiler.", tinderbox.highlighted());
		lightLogs.addIcon(ItemID.TINDERBOX);

		waitForCooking = new DetailedQuestStep(this, "Wait for the bones to be cleaned. You can hop worlds to make this happen instantly.");

		removePot = new ObjectStep(this, ObjectID.RAG_MULTI_POTBOILER, new WorldPoint(3360, 3505, 0),
			"Take the pot from the pot-boiler.");

		giveBones = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Give the Odd Old Man the bones.");
		giveBones.addItemRequirements(RagBoneGroups.cleanBonesNotHandedIn(RagBoneGroups.getRagBoneIIStates()));

		talkToFinish = new NpcStep(this, NpcID.RAG_ODD_OLD_MAN, new WorldPoint(3362, 3502, 0),
			"Talk to the Odd Old Man to finish.");
		giveBones.addSubSteps(talkToFinish);

		repeatSteps = new DetailedQuestStep(this, "Repeat the steps until all the bones are cleaned.");
	}

	public void setupConditionalSteps()
	{
		pickupBoneSteps = new ConditionalStep(this, new DetailedQuestStep(this, ""));

		// Morytania
		mapForMory.put(RagBoneState.BAT_WING, killBat);
		mapForMory.put(RagBoneState.UNDEAD_COW_RIBS, killUndeadCow);

		ConditionalStep goKillExperiment = new ConditionalStep(this, enterExperimentCave);
		goKillExperiment.addStep(inExperimentCave, killExperiment);
		mapForMory.put(RagBoneState.EXPERIMENT_BONE, goKillExperiment);
		mapForMory.put(RagBoneState.WEREWOLF_BONE, killWerewolf);
		mapForMory.put(RagBoneState.GHOUL_BONE, killGhoul);

		stepsForRagAndBoneManII.putAll(mapForMory);

		// Varrock Sewer
		ConditionalStep goGetZombieBone = new ConditionalStep(this, enterSewer);
		goGetZombieBone.addStep(inVarrockSewer, killZombie);
		mapForVarrock.put(RagBoneState.ZOMBIE_BONE, goGetZombieBone);

		ConditionalStep goGetRatBone = new ConditionalStep(this, enterSewer);
		goGetRatBone.addStep(inVarrockSewer, killRat);
		mapForVarrock.put(RagBoneState.RAT_BONE, goGetRatBone);

		stepsForRagAndBoneManII.putAll(mapForVarrock);

		// Lumbridge Swamp Caves
		ConditionalStep goGetCaveGoblinBone = new ConditionalStep(this, addRope);
		goGetCaveGoblinBone.addStep(inSwamp, killCaveGoblin);
		goGetCaveGoblinBone.addStep(inJunaRoom, leaveJunaRoom);
		goGetCaveGoblinBone.addStep(addedRope, enterSwamp);
		mapForLumbridge.put(RagBoneState.CAVE_GOBLIN_SKULL, goGetCaveGoblinBone);

		stepsForRagAndBoneManII.putAll(mapForLumbridge);

		// Desert
		mapForDesert.put(RagBoneState.JACKAL_BONE, killJackal);
		mapForDesert.put(RagBoneState.SNAKE_SPINE, killSnake);
		mapForDesert.put(RagBoneState.DESERT_LIZARD_BONE, killLizard);
		mapForDesert.put(RagBoneState.VULTURE_WING, killVulture);

		stepsForRagAndBoneManII.putAll(mapForDesert);

		// Port Sarim
		mapForSarim.put(RagBoneState.SEAGULL_WING, killSeagull);

		ConditionalStep goKillIceGiant = new ConditionalStep(this, enterAsgarniaDungeon);
		goKillIceGiant.addStep(inAsgarniaDungeon, killIceGiant);
		mapForSarim.put(RagBoneState.ICE_GIANT_RIBS, goKillIceGiant);

		ConditionalStep goKillMogre = new ConditionalStep(this, throwExplosive);
		goKillMogre.addStep(mogreNearby, killMogre);
		mapForSarim.put(RagBoneState.MOGRE_BONE, goKillMogre);

		stepsForRagAndBoneManII.putAll(mapForSarim);

		// Karamja
		mapForKaramja.put(RagBoneState.JOGRE_BONE, killJogre);

		ConditionalStep goKillMossGiant = new ConditionalStep(this, enterBrimhavenDungeon);
		goKillMossGiant.addStep(inBrimhavenDungeon, killMossGiant);
		mapForKaramja.put(RagBoneState.MOSS_GIANT_BONE, goKillMossGiant);

		ConditionalStep goKillFireGiant = new ConditionalStep(this, enterBrimhavenDungeon);
		goKillFireGiant.addStep(inBrimhavenDungeon, killFireGiant);
		mapForKaramja.put(RagBoneState.FIRE_GIANT_BONE, goKillFireGiant);

		stepsForRagAndBoneManII.putAll(mapForKaramja);

		// Taverley
		ConditionalStep goKillBabyBlue = new ConditionalStep(this, enterTaverleyDungeon);
		goKillBabyBlue.addStep(new Conditions(inDeepTaverleyDungeon), killBabyBlueDragon);
		goKillBabyBlue.addStep(new Conditions(inTaverleyDungeon,
			new SkillRequirement(Skill.AGILITY, 70, true)), goThroughPipe);
		goKillBabyBlue.addStep(new Conditions(inTaverleyDungeon, dustyKey), enterDeeperTaverley);
		goKillBabyBlue.addStep(new Conditions(inTaverleyDungeon, new Conditions(LogicType.OR, inJailCell, jailKey)),
			getDustyFromAdventurer);
		goKillBabyBlue.addStep(new Conditions(inTaverleyDungeon, jailKeyOnFloor), pickUpJailKey);
		goKillBabyBlue.addStep(new Conditions(inTaverleyDungeon), killJailerForKey);
		mapForTaverley.put(RagBoneState.BABY_DRAGON_BONE, goKillBabyBlue);

		stepsForRagAndBoneManII.putAll(mapForTaverley);

		// Fremennik
		ConditionalStep goKillTroll = new ConditionalStep(this, enterKeldagrimCave);
		goKillTroll.addStep(inTrollCave, killTroll);
		mapForFremennik.put(RagBoneState.TROLL_BONE, goKillTroll);

		mapForFremennik.put(RagBoneState.RABBIT_BONE, killRabbit);

		ConditionalStep goKillBasilisk = new ConditionalStep(this, enterFremmyDungeon);
		goKillBasilisk.addStep(inFremennikSlayerDungeon, killBasilisk);
		mapForFremennik.put(RagBoneState.BASILISK_BONE, goKillBasilisk);

		ConditionalStep goKillDagannoth = new ConditionalStep(this, travelToWaterbirth);
		goKillDagannoth.addStep(inWaterbirthDungeon, killDagannoth);
		goKillDagannoth.addStep(onWaterbirth, enterWaterbirthDungeon);
		mapForFremennik.put(RagBoneState.DAGANNOTH_RIBS, goKillDagannoth);

		stepsForRagAndBoneManII.putAll(mapForFremennik);

		// Gnome Stronghold
		mapForStronghold.put(RagBoneState.TERRORBIRD_WING, killTerrorbird);

		stepsForRagAndBoneManII.putAll(mapForStronghold);

		// Feldip Hills
		mapForFeldip.put(RagBoneState.WOLF_BONE, killWolf);
		mapForFeldip.put(RagBoneState.OGRE_RIBS, killOgre);
		mapForFeldip.put(RagBoneState.ZOGRE_BONE, killZogre);

		stepsForRagAndBoneManII.putAll(mapForFeldip);


		// Take all reqs and compile as locking step for mory
		morySteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Morytania"));
		mapForMory.forEach((RagBoneState state, QuestStep step) -> {
			moryReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			morySteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		moryReqs = new Conditions(moryReqsList);
		morySteps.setLockingCondition(moryReqs);

		varrockSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Varrock"));
		mapForVarrock.forEach((RagBoneState state, QuestStep step) -> {
			varrockReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			varrockSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		varrockReqs = new Conditions(varrockReqsList);
		varrockSteps.setLockingCondition(varrockReqs);

		lumbridgeSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Lumbridge"));
		mapForLumbridge.forEach((RagBoneState state, QuestStep step) -> {
			lumbridgeReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			lumbridgeSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		lumbridgeReqs = new Conditions(lumbridgeReqsList);
		lumbridgeSteps.setLockingCondition(lumbridgeReqs);

		desertSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Desert"));
		mapForDesert.forEach((RagBoneState state, QuestStep step) -> {
			desertReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			desertSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		desertReqs = new Conditions(desertReqsList);
		desertSteps.setLockingCondition(desertReqs);

		sarimSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Port Sarim"));
		mapForSarim.forEach((RagBoneState state, QuestStep step) -> {
			sarimReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			sarimSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		sarimReqs = new Conditions(sarimReqsList);
		sarimSteps.setLockingCondition(sarimReqs);

		karamjaSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Karamja"));
		mapForKaramja.forEach((RagBoneState state, QuestStep step) -> {
			karamjaReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			karamjaSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		karamjaReqs = new Conditions(karamjaReqsList);
		karamjaSteps.setLockingCondition(karamjaReqs);

		taverleySteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Taverley"));
		mapForTaverley.forEach((RagBoneState state, QuestStep step) -> {
			taverleyReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			taverleySteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		taverleyReqs = new Conditions(taverleyReqsList);
		taverleySteps.setLockingCondition(taverleyReqs);

		fremennikSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Fremennik"));
		mapForFremennik.forEach((RagBoneState state, QuestStep step) -> {
			fremennikReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			fremennikSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		fremennikReqs = new Conditions(fremennikReqsList);
		fremennikSteps.setLockingCondition(fremennikReqs);

		strongholdSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Gnome"));
		mapForStronghold.forEach((RagBoneState state, QuestStep step) -> {
			strongholdReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			if (step instanceof ConditionalStep)
			{
				((ConditionalStep) step).getSteps().forEach((QuestStep substep) -> substep.addSubSteps(pickupBoneStep));
			}
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			strongholdSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		strongholdReqs = new Conditions(strongholdReqsList);
		strongholdSteps.setLockingCondition(strongholdReqs);

		feldipSteps = new ConditionalStep(this, new DetailedQuestStep(this, "Unknown state Feldip"));
		mapForFeldip.forEach((RagBoneState state, QuestStep step) -> {
			feldipReqsList.add(state.hadBoneItem(questBank));
			ItemStep pickupBoneStep = new ItemStep(this, "Pickup the bone.", state.getBoneItem());
			step.addSubSteps(pickupBoneStep);
			pickupBoneSteps.addStep(new ItemOnTileRequirement(state.getBoneItem()), pickupBoneStep);
			feldipSteps.addStep(nor(state.hadBoneItem(questBank)), step);
		});
		feldipReqs = new Conditions(feldipReqsList);
		feldipSteps.setLockingCondition(feldipReqs);
		feldipSteps.setBlocker(true);
	}

	@Override
	public List<String> getCombatRequirements()
	{
		return Collections.singletonList("27 low to mid leveled monsters for their bones");
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> requirements = new ArrayList<>();
		requirements.add(new SkillRequirement(Skill.SLAYER, 40, true));
		requirements.add(new SkillRequirement(Skill.DEFENCE, 20));
		requirements.add(new QuestRequirement(QuestHelperQuest.RAG_AND_BONE_MAN_I, QuestState.FINISHED));
		requirements.add(new QuestRequirement(QuestHelperQuest.SKIPPY_AND_THE_MOGRES, QuestState.FINISHED));

		Conditions canAccessExperimentCave = new Conditions(LogicType.OR,
			new VarbitRequirement(192, 1),
			new VarplayerRequirement(QuestVarPlayer.QUEST_CREATURE_OF_FENKENSTRAIN.getId(), 2, Operation.GREATER_EQUAL)
		);
		canAccessExperimentCave.setText("Partial completion of Creature of Fenkenstrain");
		requirements.add(canAccessExperimentCave);
		requirements.add(new VarbitRequirement(QuestVarbits.QUEST_ZOGRE_FLESH_EATERS.getId(), Operation.GREATER_EQUAL,
			3, "Partial completion of Zogre Flesh Eaters"));
		return requirements;
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(coins.quantity(902), pots.quantity(27), logs.quantity(27), tinderbox,
			lightSource, rope.hideConditioned(addedRope), rangedWeapon,
			dustyKey.hideConditioned(new SkillRequirement(Skill.AGILITY, 70, true)), iceCooler, fishingExplosive,
			mirrorShield, axe);
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(antifireShield, inoculationBracelet, digsitePendant, ectophial, ringOfDueling,
			gamesNecklace, varrockTeleport, lumbridgeTeleport, nardahTeleport, draynorTeleport,
			karamjaTeleport, taverleyTeleport, rellekkaTeleport, gnomeTeleport, feldipTeleport, dramenStaff);
	}

	@Override
	public List<String> getNotes()
	{
		return Collections.singletonList("If you've handed in any bones to the Odd Old Man, open the quest journal to" +
			" sync up the helper's state");
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.PRAYER, 5000));
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("A Bonesack", ItemID.RAG_BONESACK, 1),
				new ItemReward("A Ram Skull Helm", ItemID.RAG_RAM_HELM, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		PanelDetails collectingMorytaniaPanel = new PanelDetails("Morytania bones", Arrays.asList(killBat,
			killUndeadCow, enterExperimentCave, killExperiment, killWerewolf, killGhoul), null, Collections.singletonList(ectophial));
		collectingMorytaniaPanel.setLockingStep(morySteps);
		allSteps.add(collectingMorytaniaPanel);

		PanelDetails collectingVarrockPanel = new PanelDetails("Varrock Sewer bones",
			Arrays.asList(enterSewer, killZombie, killRat));
		collectingVarrockPanel.setLockingStep(varrockSteps);
		allSteps.add(collectingVarrockPanel);

		PanelDetails collectingLumbridgePanel = new PanelDetails("Lumbridge Swamp bone",
			Arrays.asList(enterSwamp, killCaveGoblin), rope.hideConditioned(addedRope), lightSource, tinderbox);
		collectingLumbridgePanel.setLockingStep(lumbridgeSteps);
		allSteps.add(collectingLumbridgePanel);

		PanelDetails collectingDesertPanel = new PanelDetails("Desert bones",
			Arrays.asList(killJackal, killSnake, killLizard, killVulture), iceCooler, rangedWeapon);
		collectingDesertPanel.setLockingStep(desertSteps);
		allSteps.add(collectingDesertPanel);

		PanelDetails collectingSarimPanel = new PanelDetails("Port Sarim bones",
			Arrays.asList(killSeagull, enterAsgarniaDungeon, killIceGiant, throwExplosive,
				killMogre), fishingExplosive);
		collectingSarimPanel.setLockingStep(sarimSteps);
		allSteps.add(collectingSarimPanel);

		PanelDetails collectingKaramjaPanel = new PanelDetails("Karamja bones",
			Arrays.asList(killJogre, enterBrimhavenDungeon, killMossGiant, killFireGiant),
			coins.quantity(875).hideConditioned(new VarbitRequirement(8122, 1)), axe);
		collectingKaramjaPanel.setLockingStep(karamjaSteps);
		allSteps.add(collectingKaramjaPanel);
		// 8123 0->8 also when paid 1m for perm access

		PanelDetails collectingTaverleyPanel = new PanelDetails("Taverley Dungeon bone",
			Arrays.asList(enterTaverleyDungeon, killBabyBlueDragon),
			Collections.singletonList(dustyKey
					.hideConditioned(new SkillRequirement(Skill.AGILITY, 70, true))),
			Arrays.asList(taverleyTeleport, antifireShield)
		);
		collectingTaverleyPanel.setLockingStep(taverleySteps);
		allSteps.add(collectingTaverleyPanel);

		PanelDetails collectingFremennikPanel = new PanelDetails("Fremennik bones",
			Arrays.asList(enterKeldagrimCave, killTroll, killRabbit, enterFremmyDungeon, killBasilisk,
				travelToWaterbirth, enterWaterbirthDungeon, killDagannoth),
			Collections.singletonList(mirrorShield),
			Arrays.asList(rellekkaTeleport, rellekkaNETeleport));
		collectingFremennikPanel.setLockingStep(fremennikSteps);
		allSteps.add(collectingFremennikPanel);

		PanelDetails collectingStrongholdPanel = new PanelDetails("Gnome Stronghold bones",
			Collections.singletonList(killTerrorbird));
		collectingStrongholdPanel.setLockingStep(strongholdSteps);
		allSteps.add(collectingStrongholdPanel);

		PanelDetails collectingFeldipPanel = new PanelDetails("Feldip Hills bones",
			Arrays.asList(killWolf, killOgre, killZogre), null, Collections.singletonList(feldipTeleport));
		collectingFeldipPanel.setLockingStep(feldipSteps);
		allSteps.add(collectingFeldipPanel);

		List<Requirement> dirtyBones = new ArrayList<>(Arrays.asList(coinsOrVinegar.quantity(27), pots.quantity(27)));
		dirtyBones.addAll(RagBoneGroups.dirtyBonesNotHandedIn(RagBoneGroups.getRagBoneIIStates()));
		PanelDetails preparingPanel = new PanelDetails("Preparing the bones", Arrays.asList(makePotOfVinegar, useBonesOnVinegar),
			dirtyBones);
		preparingPanel.setLockingStep(preparingBonesSteps);
		allSteps.add(preparingPanel);

		List<Requirement> cleaningBones = new ArrayList<>(Arrays.asList(logs.quantity(27), tinderbox));
		PanelDetails cookingPanel = new PanelDetails("Cleaning the bones", Arrays.asList(placeLogs,
			useBoneOnBoiler, lightLogs, waitForCooking, removePot, repeatSteps), cleaningBones);
		cookingPanel.setLockingStep(cookingSteps);
		allSteps.add(cookingPanel);

		allSteps.add(new PanelDetails("Handing the bones in", Collections.singletonList(giveBones)));

		return allSteps;
	}
}
