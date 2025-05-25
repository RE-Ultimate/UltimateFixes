package com.rewindmc.upsilonfixes.newjava;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.Opcodes;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("cpw.mods.fml.common.discovery.asm.ModClassVisitor")
@ConfigOptions("modernJavaSupport")
public class ModClassVisitorTransformer extends UpsilonMiniTransformer {
	
	@Patch.Method("<init>(Lcpw/mods/fml/common/discovery/asm/ASMModParser;)V")
	public void patchInit(PatchContext ctx) {
		ctx.search(LDC(Opcodes.ASM4)).jumpAfter();
		ctx.add(
			POP(),
			LDC(Opcodes.ASM5)
		);
	}

}
