package com.rewindmc.upsilonfixes.thaumcraft;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("thaumcraft/api/ObjectTags")
@ConfigOptions("fixThaumcraftTagNpe")
public class ObjectTagsTransformer extends UpsilonMiniTransformer {

	@Patch.Method("reduceAmount(Lthaumcraft/api/EnumTag;I)Z")
	@Patch.Method.AffectsControlFlow
	public void patchAddNullcheckBoolean(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode Lcontinue = new LabelNode();
		ctx.add(
			ALOAD(1),
			IFNONNULL(Lcontinue),
			ICONST_0(),
			IRETURN(),
			Lcontinue
		);
	}

	@Patch.Method("remove(Lthaumcraft/api/EnumTag;I)Lthaumcraft/api/ObjectTags;")
	@Patch.Method("add(Lthaumcraft/api/EnumTag;I)Lthaumcraft/api/ObjectTags;")
	@Patch.Method("merge(Lthaumcraft/api/EnumTag;I)Lthaumcraft/api/ObjectTags;")
	@Patch.Method.AffectsControlFlow
	public void patchAddNullcheckThis(PatchContext ctx) {
		ctx.jumpToStart();
		LabelNode Lcontinue = new LabelNode();
		ctx.add(
			ALOAD(1),
			IFNONNULL(Lcontinue),
			ALOAD(0),
			ARETURN(),
			Lcontinue
		);
	}
	
}
