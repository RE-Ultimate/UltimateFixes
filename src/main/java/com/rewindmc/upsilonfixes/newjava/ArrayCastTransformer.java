package com.rewindmc.upsilonfixes.newjava;

import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import nilloader.api.lib.asm.Opcodes;
import nilloader.api.lib.asm.tree.AbstractInsnNode;
import nilloader.api.lib.asm.tree.ClassNode;
import nilloader.api.lib.asm.tree.MethodInsnNode;
import nilloader.api.lib.asm.tree.MethodNode;
import nilloader.api.lib.asm.tree.TypeInsnNode;

public abstract class ArrayCastTransformer extends UpsilonMiniTransformer {
	
	// how did this EVER work. what
	
	@Override
	protected boolean modifyClassStructure(ClassNode clazz) {
		for (MethodNode mn : clazz.methods) {
			AbstractInsnNode ain = mn.instructions.getFirst();
			while (ain != null) {
				if (ain.getOpcode() == Opcodes.CHECKCAST) {
					TypeInsnNode chk = (TypeInsnNode)ain;
					AbstractInsnNode prev = ain.getPrevious();
					if (prev instanceof MethodInsnNode) {
						MethodInsnNode min = (MethodInsnNode)prev;
						if ("toArray".equals(min.name) && "()[Ljava/lang/Object;".equals(min.desc)) {
							min.desc = "([Ljava/lang/Object;)[Ljava/lang/Object;";
							mn.instructions.insertBefore(min, build(
								ICONST_0(),
								ANEWARRAY(chk.desc.substring(2, chk.desc.length()-1))
							));
						}
					}
				}
				
				ain = ain.getNext();
			}
		}
		return false;
	}
	
}
