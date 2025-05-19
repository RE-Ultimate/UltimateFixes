package com.rewindmc.upsilonfixes.newjava;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import com.rewindmc.upsilonfixes.newjava.support.ArrayUtils;
import com.rewindmc.upsilonfixes.newjava.support.IExtensibleEnum;

import cpw.mods.fml.common.FMLLog;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraftforge.common.EnumHelper")
@ConfigOptions("modernJavaSupport")
public class EnumHelperTransformer extends UpsilonMiniTransformer {

	@Patch.Method("addEnum(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Enum;")
	public void patchAddEnum(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			ALOAD(2),
			ALOAD(3),
			INVOKESTATIC(hooks(), "addEnum", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Enum;"),
			ARETURN()
		);
	}
	
	@Patch.Method("setup()V")
	public void patchSetup(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ICONST_1(),
			PUTSTATIC("net/minecraftforge/common/EnumHelper", "isSetup", "Z"),
			RETURN()
		);
	}
	
	public static class Hooks {

		private static final Map<Class<? extends Enum<?>>, Map<String, Enum<?>>> enumConstants = new HashMap<>();

		@SuppressWarnings("unchecked")
		public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes,
				Object[] paramValues) {
			if (!IExtensibleEnum.class.isAssignableFrom(enumType)) {
				throw new RuntimeException("Enum " + enumType.getName() + " was not made extensible!");
			}

			synchronized (enumConstants) {
				Map<String, Enum<?>> enumMap = enumConstants.computeIfAbsent(enumType, k -> new HashMap<>());
				if (enumMap.containsKey(enumName.toUpperCase())) {
					// Inside the addEnum method
					int suffix = 1;
					String newName;
					do {
						newName = enumName + "$" + suffix;
						suffix++;
					} while (enumMap.containsKey(newName.toUpperCase()));

					// Log Enum Name Change
					FMLLog.info(String.format("Duplicate Enum found for %s mapping to %s", enumName, newName));
					enumName = newName;
				}
				try {
					paramTypes = ArrayUtils.add(paramTypes, 0, String.class);
					paramValues = ArrayUtils.add(paramValues, 0, enumName);

					Method creatorHandle = enumType.getMethod("dynamicCreate", paramTypes);
					T newValue = (T) creatorHandle.invoke(null, paramValues);

					// Add Enum Name / Class to Map
					enumMap.put(enumName.toUpperCase(), newValue);
					return newValue;
				} catch (Exception e) {
					throw new RuntimeException("Failed to add enum constant: " + enumName, e);
				}
			}
		}
	}

}
