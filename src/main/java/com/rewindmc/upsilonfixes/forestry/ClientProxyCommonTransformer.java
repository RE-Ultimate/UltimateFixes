package com.rewindmc.upsilonfixes.forestry;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("forestry/core/proxy/ClientProxyCommon")
@ConfigOptions("fixThreadSafety")
public class ClientProxyCommonTransformer extends UpsilonMiniTransformer {

	@Patch.Method("addEntityExplodeFX(Lnet/minecraft/world/World;DDDFFF)V")
	public void patchAddEntityExplodeFX(PatchContext ctx) {
		ctx.search(ALOAD(1)).jumpAfter();
		ctx.add(
			POP(),
			INVOKESTATIC(hooks(), "getClientWorld", "()Lnet/minecraft/world/World;")
		);
	}
	
	public static class Hooks {
		
		public static World getClientWorld() {
			return Minecraft.instance().world;
		}
		
	}
	
}
