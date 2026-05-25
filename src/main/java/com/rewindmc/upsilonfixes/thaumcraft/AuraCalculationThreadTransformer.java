package com.rewindmc.upsilonfixes.thaumcraft;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.PatchContext.SearchResult;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("thaumcraft/common/AuraCalculationThread")
@ConfigOptions("fixThreadSafety")
public class AuraCalculationThreadTransformer extends UpsilonMiniTransformer {

	@Patch.Method("checkFlux(Lnet/minecraft/world/World;Lthaumcraft/api/AuraNode;)V")
	public void patchCheckFlux(PatchContext ctx) {
		SearchResult sr = ctx.search(
			ALOAD(1),
			GETFIELD("net/minecraft/world/World", "rand", "Ljava/util/Random;")
		);
		
		LabelNode Lafter = new LabelNode();
		
		sr.jumpBefore();
		ctx.add(GOTO(Lafter));
		
		sr.jumpAfter();
		ctx.add(
			Lafter,
			INVOKESTATIC(hooks(), "getSafeRandom", "()Ljava/util/Random;")
		);
	}
	
	public static class Hooks {
		public Random getSafeRandom() {
			return ThreadLocalRandom.current();
		}
	}
	
}
