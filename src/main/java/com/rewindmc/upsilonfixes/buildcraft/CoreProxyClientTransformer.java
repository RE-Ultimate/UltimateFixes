package com.rewindmc.upsilonfixes.buildcraft;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("buildcraft/core/proxy/CoreProxyClient")
@ConfigOptions("fixThreadSafety")
public class CoreProxyClientTransformer extends UpsilonMiniTransformer {

	@Patch.Method("obsidianPipePickup(Lnet/minecraft/world/World;Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/tileentity/TileEntity;)V")
	@Patch.Method.AffectsControlFlow
	public void patchObsidianPipePickup(PatchContext ctx) {
		ctx.search(ALOAD(1)).jumpAfter();
		LabelNode Lcontinue = new LabelNode();
		ctx.add(
			POP(),
			INVOKESTATIC(hooks(), "getClientWorld", "()Lnet/minecraft/world/World;"),
			DUP(),
			IFNONNULL(Lcontinue),
			POP(),
			RETURN(),
			Lcontinue
		);
	}
	
	public static class Hooks {
		
		public static World getClientWorld() {
			return Minecraft.instance().world;
		}
		
	}
	
}
