package com.rewindmc.upsilonfixes.entrypoints;

import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("cpw.mods.fml.common.event.FMLLoadCompleteEvent")
public class FMLLoadCompleteEventTransformer extends UpsilonMiniTransformer {
	
	@Patch.Method("<init>([Ljava/lang/Object;)V")
	public void patchConstructor(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.addFireEntrypoint("forge-loadcomplete");
	}
	
}
