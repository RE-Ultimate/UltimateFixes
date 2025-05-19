package com.rewindmc.upsilonfixes.newjava;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import com.rewindmc.upsilonfixes.newjava.support.ArrayUtils;
import com.rewindmc.upsilonfixes.newjava.support.IExtensibleEnum;

import nilloader.api.lib.asm.Label;
import nilloader.api.lib.asm.Opcodes;
import nilloader.api.lib.asm.Type;
import nilloader.api.lib.asm.commons.InstructionAdapter;
import nilloader.api.lib.asm.tree.ClassNode;
import nilloader.api.lib.asm.tree.FieldNode;
import nilloader.api.lib.asm.tree.MethodNode;

// based on https://github.com/GTNewHorizons/lwjgl3ify/blob/8d5b556d79181c0fe6eea6b8b9260122bb0dd095/src/main/java/me/eigenraven/lwjgl3ify/rfb/transformers/ExtensibleEnumTransformer.java
public abstract class ExtensibleEnumTransformer extends UpsilonMiniTransformer {

	private final Type STRING = Type.getType(String.class);
	private final Type ENUM = Type.getType(Enum.class);
	public final Type MARKER_IFACE = Type.getType(IExtensibleEnum.class);
	private final Type ARRAY_UTILS = Type.getType(ArrayUtils.class);
	private final String ADD_DESC = Type.getMethodDescriptor(Type.getType(Object[].class), Type.getType(Object[].class), Type.getType(Object.class));
	private final Type UNSAFE_HACKS = Type.getType("Lcom/rewindmc/upsilonfixes/newjava/support/UnsafeHacks;"); // Again, not direct reference to prevent class loading.
	private final String CLEAN_DESC = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Class.class));
	private final String NAME_DESC = Type.getMethodDescriptor(STRING);
	private final String EQUALS_DESC = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, STRING);
	public static final String CREATE_METHOD_NAME = "dynamicCreate";

	@Override
	protected boolean modifyClassStructure(ClassNode clazz) {
		final Type classType = Type.getObjectType(clazz.name.replace('.', '/'));

		if (clazz.interfaces == null) {
			clazz.interfaces = new ArrayList<>(1);
		}
		clazz.interfaces.add(MARKER_IFACE.getInternalName());

		Type array = Type.getType("[" + classType.getDescriptor());
		int flags = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC;

		FieldNode values = clazz.fields.stream()
				.filter(f -> f.desc.contentEquals(array.getDescriptor()) && ((f.access & flags) == flags))
				.findFirst()
				.orElse(
						clazz.fields.stream()
						.filter(f -> f.desc.contentEquals(array.getDescriptor()) && f.name.equals("$VALUES"))
						.findFirst()
						.orElse(null));

		List<MethodNode> constructors = clazz.methods.stream()
				.filter(m -> m.name.equals("<init>"))
				.collect(Collectors.toList());

		// Static methods named "create" with first argument as a string
		List<MethodNode> candidates = constructors.stream()
				.map(ctor -> {
					String[] exceptions = ctor.exceptions == null ? null : ctor.exceptions.toArray(new String[0]);
					Type ctorDesc = Type.getMethodType(ctor.desc);
					Type creatorDesc = Type.getMethodType(classType, ArrayUtils.remove(ctorDesc.getArgumentTypes(), 1));
					MethodNode creator = new MethodNode(
							ctor.access,
							CREATE_METHOD_NAME,
							creatorDesc.getDescriptor(),
							null,
							exceptions);
					creator.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
					return creator;
				})
				.collect(Collectors.toList());

		if (candidates.isEmpty()) {
			throw new IllegalStateException(
					"IExtensibleEnum has no candidate factory methods: " + classType.getClassName());
		}

		clazz.methods.addAll(candidates);

		candidates.forEach(mtd -> {
			Type[] args = Type.getArgumentTypes(mtd.desc);
			if (args.length == 0 || !args[0].equals(STRING)) {
				UpsilonFixesPremain.log.error(
					"Enum has create method without String as first parameter:\n"
							+ "  Enum: {}\n"
							+ "  Target: {}{}",
						classType.getDescriptor(), mtd.name, mtd.desc);
				throw new IllegalStateException(
						"Enum has create method without String as first parameter: " + mtd.name + mtd.desc);
			}

			Type ret = Type.getReturnType(mtd.desc);
			if (!ret.equals(classType)) {
				UpsilonFixesPremain.log.error(
					"Enum has create method with incorrect return type:\n"
						+ "  Enum: {}\n"
						+ "  Target: {}{}\n"
						+ "  Found: {}, Expected: {}",
					classType.getDescriptor(), mtd.name, mtd.desc, ret.getClassName(), classType.getClassName());
				throw new IllegalStateException(
						"Enum has create method with incorrect return type: " + mtd.name + mtd.desc);
			}

			Type[] ctrArgs = new Type[args.length + 1];
			ctrArgs[0] = STRING;
			ctrArgs[1] = Type.INT_TYPE;
			for (int x = 1; x < args.length; x++) ctrArgs[1 + x] = args[x];

			String desc = Type.getMethodDescriptor(Type.VOID_TYPE, ctrArgs);

			MethodNode ctr = clazz.methods.stream()
					.filter(m -> m.name.equals("<init>") && m.desc.equals(desc))
					.findFirst()
					.orElse(null);
			if (ctr == null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Enum has create method with no matching constructor:\n");
				sb.append("  Enum: ").append(classType.getDescriptor()).append("\n");
				sb.append("  Candidate: ").append(mtd.desc).append("\n");
				sb.append("  Target: ").append(desc).append("\n");
				clazz.methods.stream()
					.filter(m -> m.name.equals("<init>"))
					.forEach(m -> sb.append("        : ")
						.append(m.desc)
						.append("\n"));
				UpsilonFixesPremain.log.error(sb.toString());
				throw new IllegalStateException("Enum has create method with no matching constructor: " + desc);
			}

			if (values == null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Enum has create method but we could not find $VALUES. Found:\n");
				clazz.fields.stream()
					.filter(f -> (f.access & Opcodes.ACC_STATIC) != 0)
					.forEach(m -> sb.append("  ")
						.append(m.name)
						.append(" ")
						.append(m.desc)
						.append("\n"));
				UpsilonFixesPremain.log.error(sb.toString());
				throw new IllegalStateException("Enum has create method but we could not find $VALUES");
			}

			values.access &= values.access & ~Opcodes.ACC_FINAL; // Strip the final so JITer doesn't inline things.

			mtd.access |= Opcodes.ACC_SYNCHRONIZED;
			mtd.instructions.clear();
			mtd.localVariables.clear();
			if (mtd.tryCatchBlocks != null) {
				mtd.tryCatchBlocks.clear();
			}
			if (mtd.visibleLocalVariableAnnotations != null) {
				mtd.visibleLocalVariableAnnotations.clear();
			}
			if (mtd.invisibleLocalVariableAnnotations != null) {
				mtd.invisibleLocalVariableAnnotations.clear();
			}
			InstructionAdapter ins = new InstructionAdapter(mtd);

			int vars = 0;
			for (Type arg : args) vars += arg.getSize();

			{
				vars += 1; // int x
				Label for_start = new Label();
				Label for_condition = new Label();
				Label for_inc = new Label();

				ins.iconst(0);
				ins.store(vars, Type.INT_TYPE);
				ins.goTo(for_condition);
				// if (!VALUES[x].name().equalsIgnoreCase(name)) goto for_inc
				ins.mark(for_start);
				ins.getstatic(classType.getInternalName(), values.name, values.desc);
				ins.load(vars, Type.INT_TYPE);
				ins.aload(array);
				ins.invokevirtual(ENUM.getInternalName(), "name", NAME_DESC, false);
				ins.load(0, STRING);
				ins.invokevirtual(STRING.getInternalName(), "equalsIgnoreCase", EQUALS_DESC, false);
				ins.ifeq(for_inc);
				// return VALUES[x];
				ins.getstatic(classType.getInternalName(), values.name, values.desc);
				ins.load(vars, Type.INT_TYPE);
				ins.aload(array);
				ins.areturn(classType);
				// x++
				ins.mark(for_inc);
				ins.iinc(vars, 1);
				// if (x < VALUES.length) goto for_start
				ins.mark(for_condition);
				ins.load(vars, Type.INT_TYPE);
				ins.getstatic(classType.getInternalName(), values.name, values.desc);
				ins.arraylength();
				ins.ificmplt(for_start);
			}

			{
				vars += 1; // enum ret;
				// ret = new ThisType(name, VALUES.length, args..)
				ins.anew(classType);
				ins.dup();
				ins.load(0, STRING);
				ins.getstatic(classType.getInternalName(), values.name, values.desc);
				ins.arraylength();
				int idx = 1;
				for (int x = 1; x < args.length; x++) {
					ins.load(idx, args[x]);
					idx += args[x].getSize();
				}
				ins.invokespecial(classType.getInternalName(), "<init>", desc, false);
				ins.store(vars, classType);
				// VALUES = ArrayUtils.add(VALUES, ret)
				ins.getstatic(classType.getInternalName(), values.name, values.desc);
				ins.load(vars, classType);
				ins.invokestatic(ARRAY_UTILS.getInternalName(), "add", ADD_DESC, false);
				ins.checkcast(array);
				ins.putstatic(classType.getInternalName(), values.name, values.desc);
				// EnumHelper.cleanEnumCache(ThisType.class)
				ins.visitLdcInsn(classType);
				ins.invokestatic(UNSAFE_HACKS.getInternalName(), "cleanEnumCache", CLEAN_DESC, false);
				// init ret
				ins.load(vars, classType);
				ins.invokeinterface(MARKER_IFACE.getInternalName(), "init", "()V");
				// return ret
				ins.load(vars, classType);
				ins.areturn(classType);
			}
		});
		
		return true; // compute frames
	}

}
