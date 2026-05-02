package com.rewindmc.upsilonfixes.redesign;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenLanguage;
import net.minecraft.client.gui.ScreenMainMenu;
import net.minecraft.client.gui.ScreenMultiplayer;
import net.minecraft.client.gui.ScreenOptions;
import net.minecraft.client.gui.ScreenSelectWorld;
import net.minecraft.client.gui.ScreenTexturePacks;
import net.minecraft.client.gui.WidgetButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Translate;
import paulscode.sound.SoundSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;

import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.layering.ScreenTexturePacksWithLayers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import cpw.mods.fml.client.GuiModList;

public class ScreenMainMenuUpsilon extends ScreenMainMenu {
	
	public static final ScreenMainMenu panoramaDummy = new ScreenMainMenu();
	private static long lastPanoramaTick = System.nanoTime();
	private static float lastTickDelta = 0;
	
	public static void drawPanorama(Screen screen) {
		if (screen.width != panoramaDummy.width || screen.height != panoramaDummy.height) {
			panoramaDummy.setWorldAndResolution(screen.mc, screen.width, screen.height);
		}
		float tickDelta = screen.mc.timer.renderPartialTicks;
		if (System.nanoTime()-lastPanoramaTick > 50_000_000 || tickDelta < lastTickDelta) {
			panoramaDummy.updateScreen();
			lastPanoramaTick = System.nanoTime();
		}
		lastTickDelta = tickDelta;
		panoramaDummy.renderSkybox(0, 0, tickDelta);
		screen.drawGradientRect(0, 0, screen.width, screen.height, 0x80FFFFFF, 0x00FFFFFF);
		screen.drawGradientRect(0, 0, screen.width, screen.height, 0x00000000, 0x80000000);
	}
	
	private static int logoTexId = -1;

	public static final SoundPool menuMusic = new SoundPool();
	private static SoundPoolEntry lastEntry = null;
	
	static {
		File dir = new File("resources/menumusic");
		if (dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				menuMusic.addSound(f.getName(), f);
			}
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		controlList.clear();
		int x = 20;
		int y = height-40;
		int incr = 16;
		
		Runnable tex;
		if (UpsilonFixesConfig.layeredTexturePacks) {
			tex = () -> mc.displayScreen(new ScreenTexturePacksWithLayers(this));
		} else {
			tex = () -> mc.displayScreen(new ScreenTexturePacks(this));
		}
		
		controlList.add(WidgetVoxButton.left(x, y, Translate.format("menu.quit"), () -> mc.shutdown()));
		y -= incr;
		y -= 6;
		controlList.add(WidgetVoxButton.left(x, y, Translate.format("menu.options"), () -> mc.displayScreen(new ScreenOptions(this, mc.options))));
		y -= incr;
		controlList.add(WidgetVoxButton.left(x, y, Translate.format("menu.mods"), tex));
		y -= incr;
		y -= 6;
		controlList.add(WidgetVoxButton.left(x, y, Translate.format("menu.multiplayer"), () -> mc.displayScreen(new ScreenMultiplayer(this))));
		y -= incr;
		controlList.add(WidgetVoxButton.left(x, y, Translate.format("menu.singleplayer"), () -> mc.displayScreen(new ScreenSelectWorld(this))));
		y -= incr;
		
		x = width-100-20;
		y = height-40;
		controlList.add(WidgetVoxButton.right(x, y, Translate.format("options.language"), () -> mc.displayScreen(new ScreenLanguage(this, mc.options))));
		y -= incr;
		controlList.add(WidgetVoxButton.right(x, y, "Mods", () -> mc.displayScreen(new GuiModList(this))));
		if (UpsilonFixesConfig.upsilonBranding) {
			y -= 6;
			y -= incr;
			controlList.add(WidgetVoxButton.right(x, y, "Forum →", () -> openURL("https://forum.sleeping.town/t/rewind-upsilon")));
			y -= incr;
			controlList.add(WidgetVoxButton.right(x, y, "Website →", () -> openURL("https://rewindmc.com")));
			y -= incr;
			controlList.add(WidgetVoxButton.right(x, y, "Issues →", () -> openURL("https://git.sleeping.town/Rewind/Upsilon/issues")));
		}
	}
	
	private void openURL(String url) {
		try {
			Desktop.getDesktop().browse(URI.create(url));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to browse to "+url);
		}
	}
	
	@Override
	public void keyTyped(char arg0, int arg1) {
		if (arg1 == Keyboard.KEY_INSERT) {
			mc.displayScreen(new ScreenMainMenuUpsilon());
			return;
		}
		super.keyTyped(arg0, arg1);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float tickDelta) {
		drawPanorama(this);
		
		if (UpsilonFixesConfig.upsilonBranding && logoTexId == -1) {
			logoTexId = glGenTextures();
			try {
				glBindTexture(GL_TEXTURE_2D, logoTexId);
				BufferedImage img = ImageIO.read(ScreenMainMenuUpsilon.class.getResource("/upsilon-logo.png"));
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				ByteBuffer buf = BufferUtils.createByteBuffer(img.getWidth()*img.getHeight()*4);
				int[] arr = new int[img.getWidth()*img.getHeight()];
				img.getRGB(0, 0, img.getWidth(), img.getHeight(), arr, 0, img.getWidth());
				buf.asIntBuffer().put(arr);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, buf);
			} catch (IOException e) {
				UpsilonFixesPremain.log.error("Failed to load logo texture", e);
			}
		}
		
		Tessellator tess = Tessellator.instance;
		int mcW = 274;
		int mcH = 44;
		int r = 155;
		int mcX = (width-mcW)/2;
		int mcY = 30;
		glBindTexture(GL_TEXTURE_2D, mc.renderEngine.getTexture("/title/mclogo.png"));
		glColor4f(1, 1, 1, 1);
		drawTexturedModalRect(mcX + 0, mcY + 0, 0,  0, r, mcH);
		drawTexturedModalRect(mcX + r, mcY + 0, 0, 45, r, mcH);

		if (UpsilonFixesConfig.upsilonBranding) {
			drawCenteredString(fontRenderer, "§lRewind Upsilon is still in beta!", width/2, 5, 0xFFFF5588);
			drawCenteredString(fontRenderer, "Take regular backups!", width/2, 17, -1);
		}
		
		int uW = 160;
		int uH = 23;
		int uX = (width-uW)/2;
		int uY = 60;

		if (UpsilonFixesConfig.upsilonBranding) {
			glBindTexture(GL_TEXTURE_2D, logoTexId);
			tess.startDrawingQuads();
			//                   x      y      z    u  v
			tess.addVertexWithUV(uX   , uY+uH, 0,   0, 1);
			tess.addVertexWithUV(uX+uW, uY+uH, 0,   1, 1);
			tess.addVertexWithUV(uX+uW, uY   , 0,   1, 0);
			tess.addVertexWithUV(uX   , uY   , 0,   0, 0);
			tess.draw();
		}


		tess.setColorOpaque_I(-1);
		glPushMatrix();
		int offsetX = UpsilonFixesConfig.upsilonBranding ? 110 : 90;
		int offsetY = UpsilonFixesConfig.upsilonBranding ? 75 : 70;
		glTranslatef(width / 2 + offsetX, offsetY, 0.0F);
		glRotatef("Vertical!".equals(splashText) ? 70 : -20, 0, 0, 1);
		float s = 1.8f - MathHelper.abs(MathHelper.sin(Minecraft.getSystemTime() % 1000L / 1000f * 3.1415927f * 2f) * 0.1f);
		s = s * 100f / (fontRenderer.getStringWidth(splashText) + 32);
		glScalef(s, s, s);
		drawCenteredString(fontRenderer, splashText, 0, -8, 0xFFFF00);
		glPopMatrix();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		drawString(fontRenderer, "Minecraft 1.4.7", 2, height - 10, 0xBBFFFFFF);
		String copyright = "Copyright Mojang AB. Do not distribute!";
		drawString(fontRenderer, copyright, width - fontRenderer.getStringWidth(copyright) - 2, height - 10, 0xBBFFFFFF);
		glDisable(GL_BLEND);
		
		for (WidgetButton btn : (List<WidgetButton>)this.controlList) {
			btn.drawButton(mc, mouseX, mouseY);
		}
	}
	
	private boolean firstTime = true;
	private int musicTime = 0;
	
	@Override
	public void updateScreen() {
		musicTime++;
		SoundSystem sys = SoundManager.sndSystem;
		if (sys != null && musicTime > 20 && mc.options.musicVolume > 0 && SoundSystem.initialized && !menuMusic.allSoundPoolEntries.isEmpty() && !sys.playing("BgMusic")) {
			SoundPoolEntry en = null;
			for (int i = 0; i < 3; i++) {
				en = menuMusic.getRandomSound();
				if (en != lastEntry) break;
			}
			if (en != null) {
				lastEntry = en;
				sys.setVolume("BgMusic", mc.options.musicVolume);
				sys.backgroundMusic("BgMusic", en.soundUrl, en.soundName, false);
				if (firstTime) {
					// don't jumpscare new users :P
					sys.fadeOutIn("BgMusic", en.soundUrl, en.soundName, 0, 5000);
					firstTime = false;
				} else {
					sys.play("BgMusic");
				}
				// set it again for fade-in jank
				sys.setVolume("BgMusic", mc.options.musicVolume);
				musicTime = 0;
			}
		}
		super.updateScreen();
	}
	
}
