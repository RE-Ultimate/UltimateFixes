package com.rewindmc.upsilonfixes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.lwjgl.LWJGLUtil;

import com.rewindmc.upsilonfixes.UpsilonFixesConfig.Trilean;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig.Default;

import nilloader.api.ClassTransformer;
import nilloader.api.NilLogger;
import nilloader.api.NilModList;
import nilloader.api.lib.mini.MiniTransformer;

public class UpsilonFixesPremain implements Runnable {
	
	public static final NilLogger log = NilLogger.get("UpsilonFixes");
	
	public static final List<String> transformerTargets = new ArrayList<>();
	
	@Override
	public void run() {
		if (UpsilonFixesConfig.disableXrandr) {
			System.setProperty("LWJGL_DISABLE_XRANDR", "true");
		}
		
		boolean macOS = false;
		try {
			macOS = LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX;
		} catch (NoClassDefFoundError e) {}
		boolean newJava = !System.getProperty("java.version").startsWith("1.");
		
		try (ZipFile zip = new ZipFile(NilModList.getById("upsilonfixes").get().source)) {
			for (ZipEntry en : asIterable(zip::entries)) {
				String name = en.getName();
				if (name.endsWith("Transformer.class")) {
					name = name.substring(0, name.length()-6).replace('/', '.');
					Class<?> clazz = Class.forName(name);
					if (ClassTransformer.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
						ConfigOptions options = clazz.getAnnotation(ConfigOptions.class);
						boolean enabled = true;
						if (options != null) {
							enabled = false;
							for (String o : options.value()) {
								Field f = UpsilonFixesConfig.class.getField(o);
								boolean v;
								if (f.getType() == Trilean.class) {
									Default defAnn = f.getAnnotation(Default.class);
									boolean def = false;
									switch (defAnn.value()) {
										case MACOS:
											def = macOS;
											break;
										case NEW_JAVA:
											def = newJava;
											break;
										case OLD_JAVA:
											def = !newJava;
											break;
									}
									v = ((Trilean)f.get(null)).resolve(def);
								} else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
									v = (Boolean)f.get(null);
								} else {
									throw new ClassCastException(f.getType()+" is not boolean-convertible while looking up option "+o+" for "+name);
								}
								if (v) {
									log.debug("{} is eligible because of config option {}", name, o);
									enabled = true;
									break;
								}
							}
						}
						if (enabled) {
							ClassTransformer ct = (ClassTransformer)clazz.newInstance();
							if (ct instanceof MiniTransformer) {
								transformerTargets.add(((MiniTransformer)ct).getClassTargetName());
							}
							ClassTransformer.register(ct);
						} else {
							log.debug("{} is not eligible", name);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to discover transformers", e);
		}
	}
	
	private static <T> Iterable<T> asIterable(Supplier<Enumeration<T>> sup) {
		return () -> {
			Enumeration<T> e = sup.get();
			return new Iterator<T>() {
				@Override
				public boolean hasNext() { return e.hasMoreElements(); }
				@Override
				public T next() { return e.nextElement(); }
			};
		};
	}
	
}
