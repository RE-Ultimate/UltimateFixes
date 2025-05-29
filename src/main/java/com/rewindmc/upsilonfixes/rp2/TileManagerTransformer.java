package com.rewindmc.upsilonfixes.rp2;

import com.eloraam.redpower.machine.TileManager;
import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("com.eloraam.redpower.machine.TileManager")
@ConfigOptions("fixRedPowerManagerPermanentBackstuff")
public class TileManagerTransformer extends UpsilonMiniTransformer {

	@Patch.Method("handleTubeItem(Lcom/eloraam/redpower/core/TubeItem;)Z")
	public void patchHandleTubeItem(PatchContext ctx) {
		ctx.search(GETFIELD("com/eloraam/redpower/machine/TileManager", "priority", "I")).jumpAfter();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC(hooks(), "modifyPriority", "(ILcom/eloraam/redpower/machine/TileManager;)I")
		);
	}
	
	public static class Hooks {
		
		public static int modifyPriority(int prio, TileManager mgr) {
			if (mgr.buffer.plugged) {
				return Integer.MAX_VALUE;
			}
			return prio;
		}
		
	}
	
}
