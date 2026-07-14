package com.rewindmc.upsilonfixes.vanilla.decorationqueue;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.world.WorldServer")
@ConfigOptions("decoratorQueue")
public class WorldServerTransformer extends UpsilonMiniTransformer {
	@Patch.Method("tick()V")
	public void patchTick(PatchContext ctx) {
		ctx.jumpToLastReturn();

		ctx.add(
			INVOKESTATIC(hooks(), "tick", "()V")
		);
	}

	public static class Hooks {
		private static int ticks = 0;

		public static void tick() {
			if (ticks < 20) {
				ticks++;
			} else {
				ticks = 0;

				if (BiomeDecoratorStorage.INSTANCE.isReady()) {
					BiomeDecoratorStorage.INSTANCE.decorate();
				}
			}
		}
	}
}
