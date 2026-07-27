package com.rewindmc.upsilonfixes.dartcraft;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import bluedart.api.EngineLiquid;
import bluedart.api.ForceEngineLiquids;
import bluedart.core.Config;
import bluedart.item.DartItem;
import net.minecraft.block.Block;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import nilloader.api.lib.asm.tree.AbstractInsnNode;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("bluedart.core.DartCraftCore")
@ConfigOptions({"fixDartCraftForceDisablingGregTechTweaks", "fixDartCraftMobSpawnRegistration", "nerfDartCraftForceEngine"})
public class DartCraftCoreTransformer extends UpsilonMiniTransformer {
	private final AbstractInsnNode[] loadMonsterSpawnsCall = {
			ALOAD(0),
			INVOKESPECIAL("bluedart/core/DartCraftCore", "loadMonsterSpawns", "()V")
	};
	private final AbstractInsnNode[] mobSpawnRegistrationCall = {
			LDC("enderTot"),
			ICONST_3(),
			ICONST_1(),
			ICONST_2(),
			GETSTATIC("net/minecraft/entity/EnumCreatureType", "monster", "Lnet/minecraft/entity/EnumCreatureType;"),
			ICONST_1(),
			ANEWARRAY("net/minecraft/world/biome/Biome"),
			DUP(),
			ICONST_0(),
			ALOAD(4),
			AASTORE(),
			INVOKESTATIC("cpw/mods/fml/common/registry/EntityRegistry", "addSpawn", "(Ljava/lang/String;IIILnet/minecraft/entity/EnumCreatureType;[Lnet/minecraft/world/biome/Biome;)V")
	};

	@Patch.Method("init(Ljava/lang/Object;)V")
	public void patchInit(PatchContext ctx) {
		if (UpsilonFixesConfig.fixDartCraftMobSpawnRegistration) {
			ctx.search(
					ALOAD(0),
					INVOKESPECIAL("bluedart/core/DartCraftCore", "loadEntities", "()V")
			).jumpAfter();
			ctx.add(loadMonsterSpawnsCall);
		}
	}

	@Patch.Method("postInit()V")
	public void patchPostInit(PatchContext ctx) {
		if (UpsilonFixesConfig.fixDartCraftForceDisablingGregTechTweaks) {
			ctx.search(
					LDC("gregtechmod.GT_Mod")
			).jumpAfter();
			ctx.add(
					POP(),
					LDC("thisis.aclass.that.will.not.exist.AndIfItDoes$ThenWellCongratulations")
			);
		}

		if (UpsilonFixesConfig.fixDartCraftMobSpawnRegistration) {
			ctx.jumpToStart();
			ctx.search(loadMonsterSpawnsCall).erase();
		}
	}

	@Patch.Method("loadMonsterSpawns()V")
	public void patchLoadMonsterSpawns(PatchContext ctx) {
		if (!UpsilonFixesConfig.fixDartCraftMobSpawnRegistration) return;
		LabelNode Lcontinue = new LabelNode();
		ctx.search(mobSpawnRegistrationCall).jumpBefore();
		ctx.add(
				ALOAD(4),
				GETSTATIC("net/minecraft/world/biome/Biome", "MUSHROOM_ISLAND", "Lnet/minecraft/world/biome/Biome;"),
				IF_ACMPEQ(Lcontinue),
				ALOAD(4),
				GETSTATIC("net/minecraft/world/biome/Biome", "MUSHROOM_ISLAND_SHORE", "Lnet/minecraft/world/biome/Biome;"),
				IF_ACMPEQ(Lcontinue)
		);
		ctx.search(mobSpawnRegistrationCall).jumpAfter();
		ctx.add(Lcontinue);
	}

	@Patch.Method("loadEngineLiquids()V")
	public void patchLoadEngineLiquids(PatchContext ctx) {
		if (UpsilonFixesConfig.nerfDartCraftForceEngine) {
			ctx.jumpToStart();
			ctx.add(
					INVOKESTATIC(hooks(), "addNerfedLiquids", "()V"),
					RETURN()
			);
		}
	}
	
	public static class Hooks {
		
		public static void addNerfedLiquids() {
			LiquidStack crushedIce = LiquidDictionary.getLiquid("ice", 1000);
			LiquidStack milk = LiquidDictionary.getLiquid("milk", 1000);
			LiquidStack oil = LiquidDictionary.getLiquid("Oil", 1000);
			LiquidStack fuel = LiquidDictionary.getLiquid("Fuel", 1000);
			LiquidStack biofuel = LiquidDictionary.getLiquid("biofuel", 1000);
			
			int forceValue = 4000;
			
			addFuel(DartItem.liquidForce.itemID, forceValue, 4);
			if (Config.burnLava)
				addFuel(Block.lavaStill.blockID, 5000, 0.5f);
			if (Config.burnMilk && milk != null)
				addFuel(milk, 1000, 2.5f);
			if (Config.burnOil && oil != null)
				addFuel(oil, 2500, 1.5f);
			if (Config.burnFuel && fuel != null)
				addFuel(fuel, 25000, 3);
			if (Config.burnBiofuel && biofuel != null)
				addFuel(biofuel, 10000, 2.5f);
			
			addThrottle(Block.waterStill.blockID, forceValue/100, 2);
			if (crushedIce != null) addThrottle(crushedIce, forceValue/3, 4);
		}
		

		public static void addAdditionalThrottles() {
			int forceValue = UpsilonFixesConfig.nerfDartCraftForceEngine ? 4000 : 60000;
			LiquidStack milk = LiquidDictionary.getLiquid("milk", 1000);
			LiquidStack essence = LiquidDictionary.getLiquid("mobEssence", 1000);
			LiquidStack liquidNitrogen = LiquidDictionary.getLiquid("liquidNitrogen", 1000);
			LiquidStack steam = LiquidDictionary.getLiquid("Steam", 1000);
			
			if (milk != null) addThrottle(milk, forceValue/5, 2.5f);
			if (essence != null) addThrottle(essence, forceValue*2, 3);
			if (liquidNitrogen != null) addThrottle(liquidNitrogen, forceValue/3, 4);
			if (steam != null) addThrottle(steam, forceValue*4, 0.25f);
		}

		private static void addFuel(int itemID, int value, float modifier) {
			addFuel(new LiquidStack(itemID, 1000, 0), value, modifier);
		}
		
		private static void addFuel(LiquidStack stack, int value, float modifier) {
			ForceEngineLiquids.addLiquid(new EngineLiquid(stack, EngineLiquid.TYPE_FUEL, value, modifier));
		}

		private static void addThrottle(int itemID, int value, float modifier) {
			addThrottle(new LiquidStack(itemID, 1000, 0), value, modifier);
		}
		
		private static void addThrottle(LiquidStack stack, int value, float modifier) {
			ForceEngineLiquids.addLiquid(new EngineLiquid(stack, EngineLiquid.TYPE_THROTTLE, value, modifier));
		}
		
	}
	
}
