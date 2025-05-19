package com.rewindmc.upsilonfixes.newjava;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.Type;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("logisticspipes.proxy.cc.CCProxy")
@ConfigOptions("modernJavaSupport")
public class CCProxyTransformer extends UpsilonMiniTransformer {

	@Patch.Method("<init>()V")
	@Patch.Method.AffectsControlFlow
	public void patchInit(PatchContext ctx) {
		LabelNode LafterBadReflection = new LabelNode();
		ctx.search(
			ALOAD(0),
			LDC(Type.getType("Ljava/lang/Thread;")),
			LDC("target")
		).jumpBefore();
		ctx.add(
			GOTO(LafterBadReflection)
		);
		
		
		ctx.search(
			ALOAD(0),
			ICONST_1(),
			PUTFIELD("logisticspipes/proxy/cc/CCProxy", "valid", "Z")
		).jumpBefore();
		ctx.add(
			LafterBadReflection
		);
	}
	
	@Patch.Method("isLuaThread(Ljava/lang/Thread;)Z")
	public void patchIsLuaThread(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(1),
			INVOKESTATIC(hooks(), "isLuaThread", "(Ljava/lang/Thread;)Z"),
			IRETURN()
		);
	}
	
	public static class Hooks {
		
		public static boolean isLuaThread(Thread t) {
			return t.getName().startsWith("Coroutine-");
		}
		
	}

}
