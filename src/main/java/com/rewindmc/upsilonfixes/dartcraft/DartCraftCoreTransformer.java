package com.rewindmc.upsilonfixes.dartcraft;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.tree.AbstractInsnNode;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("bluedart.core.DartCraftCore")
@ConfigOptions({"fixDartCraftForceDisablingGregTechTweaks", "fixDartCraftMobSpawnRegistration"})
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
}
