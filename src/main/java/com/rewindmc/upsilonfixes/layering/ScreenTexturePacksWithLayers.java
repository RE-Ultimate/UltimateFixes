package com.rewindmc.upsilonfixes.layering;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScrollPane;
import net.minecraft.client.gui.WidgetButton;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.util.Translate;
import net.minecraft.client.renderer.Tessellator;

public class ScreenTexturePacksWithLayers extends Screen {
	protected Screen parent;
	private int refreshTimer = -1;
	private String fileLocation = "";
	private TexturePackScrollPane texturePacks;
	private LayerPackScrollPane layers;

	public ScreenTexturePacksWithLayers(Screen parent) {
		this.parent = parent;
	}
	
	@Override
	public void initGui() {
		controlList.add(new WidgetButton(5, width / 2 - 154, height - 48, 150, 20, Translate.format("texturePack.openFolder")));
		controlList.add(new WidgetButton(6, width / 2 + 4, height - 48, 150, 20, Translate.format("gui.done")));
		mc.texturePacks.updateAvaliableTexturePacks();
		fileLocation = new File(Minecraft.getMinecraftDir(), "texturepacks").getAbsolutePath();
		texturePacks = new TexturePackScrollPane();
		texturePacks.registerScrollButtons(controlList, 7, 8);
		layers = new LayerPackScrollPane();
		layers.registerScrollButtons(controlList, 9, 10);
	}

	@Override
	public void actionPerformed(WidgetButton btn) {
		if (btn.enabled) {
			if (btn.id == 5) {
				try {
					Desktop.getDesktop().open(new File(Minecraft.getMinecraftDir(), "texturepacks"));
				} catch (Throwable e) {
					e.printStackTrace();
					System.out.println("Opening via system class!");
					Sys.openURL("file://" + fileLocation);
				}
			} else if (btn.id == 6) {
				mc.renderEngine.refreshTextures();
				mc.displayScreen(parent);
			} else {
				texturePacks.actionPerformed(btn);
				layers.actionPerformed(btn);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float tickDelta) {
		drawDefaultBackground();
		int tpMouseY = -9000;
		int lpMouseY = -9000;
		
		if (mouseX >= texturePacks.left && mouseX <= texturePacks.right) {
			tpMouseY = mouseY;
		}
		if (mouseX >= layers.left && mouseX <= layers.right) {
			lpMouseY = mouseY;
		}
		
		texturePacks.drawScreen(texturePacks.width/2, tpMouseY, tickDelta);
		layers.drawScreen(layers.width/2, lpMouseY, tickDelta);
		if (refreshTimer <= 0) {
			mc.texturePacks.updateAvaliableTexturePacks();
			refreshTimer += 20;
		}

		drawCenteredString(fontRenderer, Translate.format("texturePack.title"), width / 3, 16, 0xFFFFFF);
		drawCenteredString(fontRenderer, Translate.format("texturePack.folderInfo"), width / 2 - 77, height - 26, 0x808080);

		drawCenteredString(fontRenderer, "Layers", width * 5 / 6, 16, 0xEB6E32);
		
		super.drawScreen(mouseX, mouseY, tickDelta);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		refreshTimer--;
	}

	public abstract class AbstractTexturePackScrollPane extends ScrollPane {

		protected final Minecraft mc;
		
		public AbstractTexturePackScrollPane(Minecraft mc, int width, int height, int top, int bottom, int cellSize) {
			super(mc, width, height, top, bottom, cellSize);
			this.mc = mc;
			showSelectionBox = false;
		}
		
		@Override
		public int getSize() {
			int i = 0;
			for (ITexturePack pack : (List<ITexturePack>)mc.texturePacks.availableTexturePacks()) {
				if (Layering.isLayerPack(pack) == wantLayerPacks()) {
					i++;
				}
			}
			return i;
		}
		
		@Override
		public void elementClicked(int var1, boolean var2) {
			
		}
		
		@Override
		public boolean isSelected(int var1) {
			return false;
		}
		
		@Override
		public int getContentHeight() {
			return 0;
		}
		
		@Override
		public void drawBackground() {
			
		}

		@Override
		public void drawSlot(int i, int x, int y, int cellSize, Tessellator tess) {
			x += left;
			x += getXOffset();
			
			if (this.isSelected(i)) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(3553);
				tess.startDrawingQuads();
				tess.setColorOpaque_I(8421504);
				tess.addVertexWithUV(this.left, y + cellSize + 2, 0.0, 0.0, 1.0);
				tess.addVertexWithUV(this.right, y + cellSize + 2, 0.0, 1.0, 1.0);
				tess.addVertexWithUV(this.right, y - 2, 0.0, 1.0, 0.0);
				tess.addVertexWithUV(this.left, y - 2, 0.0, 0.0, 0.0);
				tess.setColorOpaque_I(0);
				tess.addVertexWithUV(this.left + 1, y + cellSize + 1, 0.0, 0.0, 1.0);
				tess.addVertexWithUV(this.right - 1, y + cellSize + 1, 0.0, 1.0, 1.0);
				tess.addVertexWithUV(this.right - 1, y - 1, 0.0, 1.0, 0.0);
				tess.addVertexWithUV(this.left + 1, y - 1, 0.0, 0.0, 0.0);
				tess.draw();
				GL11.glEnable(3553);
			}
			
			ITexturePack pack = get(i);
			mc.renderEngine.blurTexture = true;
			pack.bindThumbnailTexture(mc.renderEngine);
			mc.renderEngine.blurTexture = false;
			GL11.glColor4f(1, 1, 1, 1);
			tess.startDrawingQuads();
			tess.setColorOpaque_I(0xFFFFFF);
			tess.addVertexWithUV(x     , y + 32, 0, 0, 1);
			tess.addVertexWithUV(x + 32, y + 32, 0, 1, 1);
			tess.addVertexWithUV(x + 32, y     , 0, 1, 0);
			tess.addVertexWithUV(x     , y     , 0, 0, 0);
			tess.draw();
			FontRenderer fr = fontRenderer;
			fr.drawStringWithShadow(pack.getTexturePackFileName(), x + 32 + 2, y + 1, 0xFFFFFF);
			fr.drawStringWithShadow(pack.getFirstDescriptionLine(), x + 32 + 2, y + 12, 0x808080);
			fr.drawStringWithShadow(pack.getSecondDescriptionLine(), x + 32 + 2, y + 12 + 10, 0x808080);
		}
		
		protected abstract boolean wantLayerPacks();
		protected abstract int getXOffset();
		
		protected ITexturePack get(int i) {
			int j = 0;
			for (ITexturePack pack : (List<ITexturePack>)mc.texturePacks.availableTexturePacks()) {
				if (Layering.isLayerPack(pack) == wantLayerPacks()) {
					if (j == i) {
						return pack;
					}
					j++;
				}
			}
			throw new IndexOutOfBoundsException(""+i);
		}
		
	}
	
	public class TexturePackScrollPane extends AbstractTexturePackScrollPane {
		
		public TexturePackScrollPane() {
			super(ScreenTexturePacksWithLayers.this.mc, (ScreenTexturePacksWithLayers.this.width*2/3)-4, ScreenTexturePacksWithLayers.this.height, 32, ScreenTexturePacksWithLayers.this.height - 55 + 4, 36);
			this.left = 0;
			this.right = this.width;
		}
		
		@Override
		protected boolean wantLayerPacks() {
			return false;
		}

		@Override
		public void elementClicked(int i, boolean selected) {
			List<ITexturePack> avail = mc.texturePacks.availableTexturePacks();
			try {
				mc.texturePacks.setTexturePack(get(i));
				mc.renderEngine.refreshTextures();
			} catch (Exception e) {
				mc.texturePacks.setTexturePack(avail.get(0));
				mc.renderEngine.refreshTextures();
			}
			fontRenderer = mc.fontRenderer;
		}

		@Override
		public boolean isSelected(int i) {
			return mc.texturePacks.getSelectedTexturePack() == get(i);
		}

		@Override
		public int getContentHeight() {
			return getSize() * 36;
		}
		
		@Override
		protected int getXOffset() {
			return 0;
		}
		
	}
	
	public class LayerPackScrollPane extends AbstractTexturePackScrollPane {
		
		public LayerPackScrollPane() {
			super(ScreenTexturePacksWithLayers.this.mc, ScreenTexturePacksWithLayers.this.width/3, ScreenTexturePacksWithLayers.this.height, 32, ScreenTexturePacksWithLayers.this.height - 55 + 4, 36);
			this.left = ScreenTexturePacksWithLayers.this.width-this.width;
			this.right = ScreenTexturePacksWithLayers.this.width;
		}
		
		@Override
		protected boolean wantLayerPacks() {
			return true;
		}

		@Override
		public void elementClicked(int i, boolean selected) {
			ITexturePack pack = get(i);
			if (Layering.enabledLayerPacks.contains(pack)) {
				Layering.enabledLayerPacks.remove(pack);
			} else {
				Layering.enabledLayerPacks.add(pack);
			}
			Layering.saveEnabledLayers();
			mc.renderEngine.refreshTextures();
			fontRenderer = mc.fontRenderer;
		}

		@Override
		public boolean isSelected(int i) {
			return Layering.enabledLayerPacks.contains(get(i));
		}

		@Override
		public int getContentHeight() {
			return getSize() * 37;
		}
		
		@Override
		protected int getXOffset() {
			return 40;
		}
		
	}

}
