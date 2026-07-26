package com.rewindmc.upsilonfixes.codechicken;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("codechicken.core.ClientUtils")
@ConfigOptions("fixCodeChickenCoreNetworkClosedExceptionCrash")
public class ClientUtilsTransformer extends UpsilonMiniTransformer {

	@Patch.Method("getServerIP()Ljava/lang/String;")
	@Patch.Method.AffectsControlFlow
	public void patchGetServerIP(PatchContext ctx) {
		LabelNode Lcontinue = new LabelNode();
		ctx.jumpToStart();
		ctx.search(
			INVOKESTATIC("cpw/mods/fml/common/FMLCommonHandler", "instance", "()Lcpw/mods/fml/common/FMLCommonHandler;"),
			ALOAD(0),
			LDC("Code Chicken Core"),
			ICONST_1(),
			INVOKEVIRTUAL("cpw/mods/fml/common/FMLCommonHandler", "raiseException", "(Ljava/lang/Throwable;Ljava/lang/String;Z)V")
		).jumpBefore();
		ctx.add(
			ALOAD(0),
			INSTANCEOF("codechicken/core/NetworkClosedException"),
			IFEQ(Lcontinue),
			ALOAD(0),
			ATHROW(),
			Lcontinue
		);
	}

}
