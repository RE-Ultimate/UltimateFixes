package com.rewindmc.upsilonfixes.newjava;

import java.applet.Applet;
import java.awt.Container;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.FMLRelauncher;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.RelaunchClassLoader;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("cpw.mods.fml.relauncher.FMLRelauncher")
@ConfigOptions("modernJavaSupport")
public class FMLRelauncherTransformer extends UpsilonMiniTransformer {

	@Patch.Method("<init>()V")
	public void patchInit(PatchContext ctx) {
		ctx.search(INVOKESPECIAL("java/lang/Object", "<init>", "()V")).jumpAfter();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC(hooks(), "initRelauncher", "(Lcpw/mods/fml/relauncher/FMLRelauncher;)V"),
			RETURN()
		);
	}
	
	@Patch.Method("relaunchApplet(Ljava/applet/Applet;)V")
	public void patchRelaunchApplet(PatchContext ctx) {
		ctx.search(
			ALOAD(0),
			INVOKEVIRTUAL("java/lang/Object", "getClass", "()Ljava/lang/Class;"),
			INVOKEVIRTUAL("java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;"),
			ICONST_1(),
			ANEWARRAY("java/lang/String"),
			DUP(),
			ICONST_0(),
			LDC("java.applet.Applet")
		).jumpBefore();
		
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC(hooks()+"PostRelaunch", "relaunchAppletSecondBranchEnd", "(Lcpw/mods/fml/relauncher/FMLRelauncher;Ljava/applet/Applet;)V"),
			RETURN()
		);
	}
	
	public static class Hooks {
		
		// we have @Overwrite at home
		public static void initRelauncher(FMLRelauncher r) {
			List<URL> urls = new ArrayList<>();
			for (String s : System.getProperty("java.class.path").split(File.pathSeparator)) {
				try {
					urls.add(new File(s).toURI().toURL());
				} catch (MalformedURLException e) {
					UpsilonFixesPremain.log.warn("Failed to add {} to castless relauncher", s);
				}
			}
			RelaunchClassLoader rcl = new RelaunchClassLoader(urls.toArray(new URL[urls.size()]));
			rcl.addClassLoaderExclusion("java.applet");
			rcl.addClassLoaderExclusion("java.awt");
			r.classLoader = rcl;
		}
		
	}
	
	public static class HooksPostRelaunch {
		
		public static void relaunchAppletSecondBranchEnd(FMLRelauncher r, Applet minecraftApplet) {
			try {
				// UF - just use the fucking api instead of reflecting into everything
				r.newApplet = r.appletClass.newInstance();
				Container appletContainer = minecraftApplet.getParent();
				String launcherClassName = System.getProperty("minecraft.applet.WrapperClass", "net.minecraft.Launcher");
				Class<? super Object> launcherClass = ReflectionHelper.getClass(r.getClass().getClassLoader(), launcherClassName);
				if (!launcherClass.isInstance(appletContainer)) {
					FMLRelaunchLog.severe("Found unknown applet parent %s, unable to inject!\n", appletContainer.getClass().getName());
					throw new RuntimeException();
				}

				appletContainer.removeAll();
				ReflectionHelper.findMethod(launcherClass, appletContainer, new String[]{"replace"}, Applet.class)
					.invoke(appletContainer, r.newApplet);
			} catch (Exception var12) {
				throw new RuntimeException(var12);
			} finally {
				if (r.popupWindow != null) {
					r.popupWindow.setVisible(false);
					r.popupWindow.dispose();
				}
			}
		}
		
	}
	
}
