package com.rewindmc.upsilonfixes.vanilla.tattle;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.world.World;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net/minecraft/client/particle/EntityFX")
@ConfigOptions("tattleThreadSafety")
public class EntityFXTransformer extends UpsilonMiniTransformer {

	@Patch.Method("<init>(Lnet/minecraft/world/World;DDD)V")
	public void patchInit(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(1),
			INVOKESTATIC(hooks(), "check", "(Lnet/minecraft/world/World;)V")
		);
	}
	
	public static class Hooks {
		
		public static void check(World world) {
			if (!world.isRemote) {
				throw new IllegalArgumentException("Constructing EntityFX for a server world!");
			}
		}
		
	}
	
}
