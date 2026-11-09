package com.rewindmc.upsilonfixes.minefactory;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper")
@ConfigOptions("fixMfrAutoEnchanter")
public class AutoEnchantmentHelperTransformer extends UpsilonMiniTransformer {

	@Patch.Method("buildEnchantmentList(Ljava/util/Random;Lnet/minecraft/item/ItemStack;I)Ljava/util/List;")
	@Patch.Method.AffectsControlFlow
	public void patchBuildEnchantmentList(PatchContext ctx) {
		// Vanilla filters every candidate against all selected enchantments. MFR checks only one.
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			ILOAD(2),
			INVOKESTATIC("net/minecraft/enchantment/EnchantmentHelper", "buildEnchantmentList", "(Ljava/util/Random;Lnet/minecraft/item/ItemStack;I)Ljava/util/List;"),
			ARETURN()
		);
	}
}
