package com.rewindmc.upsilonfixes.lang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;
import nilloader.api.lib.nanojson.JsonObject;
import nilloader.api.lib.nanojson.JsonWriter;

@Patch.Class("cpw.mods.fml.common.registry.LanguageRegistry")
@ConfigOptions("dumpLang")
public class LanguageRegistryTransformer extends UpsilonMiniTransformer {

	@Patch.Method("addStringLocalization(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
	public void patchAddStringLocalization(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(1),
			ALOAD(2),
			ALOAD(3),
			INVOKESTATIC(hooks(), "addStringLocalization", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
		);
	}
	
	@Patch.Method("addStringLocalization(Ljava/util/Properties;Ljava/lang/String;)V")
	public void patchAddStringLocalizationBulk(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(1),
			ALOAD(2),
			INVOKESTATIC(hooks(), "addStringLocalization", "(Ljava/util/Properties;Ljava/lang/String;)V")
		);
	}
	
	public static class Hooks {
		
		public static final ThreadLocal<String> modidOverride = ThreadLocal.withInitial(() -> null);

		private static final Pattern WINDOWS_IS_A_SHITTY_LEGACY_OPERATING_SYSTEM_THAT_PEOPLE_NEED_TO_STOP_USING = Pattern.compile("[<>:\"\\\\|?*]");
		
		private static final Map<String, Map<String, JsonObject>> translations = new HashMap<>();
		private static final ScheduledExecutorService sched = Executors.newScheduledThreadPool(0);
		private static Future<?> future;

		public static void addStringLocalization(Properties props, String lang) {
			for (Map.Entry<Object, Object> en : props.entrySet()) {
				addStringLocalization(String.valueOf(en.getKey()), lang, String.valueOf(en.getValue()));
			}
		}
		
		public static void addStringLocalization(String key, String lang, String value) {
			ModContainer mod = Loader.instance().activeModContainer();
			String modid = modidOverride.get();
			if (modid == null) {
				if (mod == null) {
					modid = new Throwable("Stack trace").fillInStackTrace().getStackTrace()[3].getClassName();
				} else {
					modid = mod.getModId();
				}
			}
			Map<String, JsonObject> map = translations.get(modid);
			if (map == null) {
				map = new LinkedHashMap<>();
				translations.put(modid, map);
			}
			JsonObject obj = map.get(lang);
			if (obj == null) {
				obj = new JsonObject();
				map.put(lang, obj);
			}
			obj.put(key, value);
			if (future != null) future.cancel(false);
			future = sched.schedule(Hooks::save, 3, TimeUnit.SECONDS);
		}
		
		public static void save() {
			UpsilonFixesPremain.log.info("Saving lang dumps");
			File root = new File("langdumps");
			for (Map.Entry<String, Map<String, JsonObject>> en : translations.entrySet()) {
				String modid = en.getKey().replace("/", "_");
				modid = WINDOWS_IS_A_SHITTY_LEGACY_OPERATING_SYSTEM_THAT_PEOPLE_NEED_TO_STOP_USING.matcher(modid).replaceAll("_");
				File dir = new File(root, modid);
				dir.mkdirs();
				for (Map.Entry<String, JsonObject> en2 : en.getValue().entrySet()) {
					String lang = en2.getKey();
					JsonObject obj = en2.getValue();
					File f = new File(dir, lang+".json");
					try (FileOutputStream out = new FileOutputStream(f)) {
						JsonWriter.indent("\t").on(out).object(obj).done();
					} catch (IOException e) {
						UpsilonFixesPremain.log.warn("Failed to save {}", f, e);
					}
				}
			}
		}
		
	}
	
}
