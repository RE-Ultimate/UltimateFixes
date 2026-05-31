package com.rewindmc.upsilonfixes.ic2;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("ic2.core.network.NetworkManager")
@ConfigOptions("fixIC2NetworkCrash")
public class NetworkManagerTransformer extends UpsilonMiniTransformer {

	@Patch.Method("updateTileEntityField(Lnet/minecraft/tileentity/TileEntity;Ljava/lang/String;)V")
	@Patch.Method.AffectsControlFlow
	public void patchUpdateTileEntityField(PatchContext ctx) {
		LabelNode Lcontinue = new LabelNode();
		ctx.search(
			ALOAD(0),
			ALOAD(1),
			GETFIELD("net/minecraft/tileentity/TileEntity", "worldObj", "Lnet/minecraft/world/World;"),
			INVOKESPECIAL("ic2/core/network/NetworkManager", "sendUpdatePacket", "(Lnet/minecraft/world/World;)V")
		).jumpBefore();
		ctx.add(
			ALOAD(1),
			GETFIELD("net/minecraft/tileentity/TileEntity", "worldObj", "Lnet/minecraft/world/World;"),
			GETFIELD("net/minecraft/world/World", "isRemote", "Z"),
			IFEQ(Lcontinue),
			RETURN(),
			Lcontinue
		);
	}

}
