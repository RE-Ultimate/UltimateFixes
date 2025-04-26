package com.rewindmc.upsilonfixes;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.handled.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.inventory.Slot;

import static org.lwjgl.opengl.GL11.*;

/**
 * Like the Wizard's Companion but it has been smeared across space and time
 */
public class SmearingCompanion {

	private final HandledScreen gui;
	
	private int buttonDown = -1;
	private long lastLeftClick = -4000;
	private final Set<Slot> smearedSlots = new LinkedHashSet<>();

	public SmearingCompanion(HandledScreen gui) {
		this.gui = gui;
	}

	private Set<Slot> determineEligibleSlots() {
		Set<Slot> li = Collections.emptySet();
		ItemStack cursor = gui.mc.player.inventory.getItemStack();
		if (cursor == null) return li;
		int eligibleCount = 0;
		for (Slot s : smearedSlots) {
			if (!s.hasStack() || canStack(cursor, s.getStack())) {
				eligibleCount++;
			}
		}
		int total = cursor.count;
		int available = cursor.count;
		for (Slot s : smearedSlots) {
			if (available <= 0) break;
			if (!s.hasStack() || canStack(cursor, s.getStack())) {
				int amt = s.hasStack() ? s.getStack().count : 0;
				int max = Math.min(s.getMaxCount(), s.hasStack() ? s.getStack().getMaxCount() : cursor.getMaxCount());
				int toAdd = Math.min(available, Math.min(max-amt, buttonDown == 0 ? total/eligibleCount : 1));
				available -= toAdd;
				if (li.isEmpty()) li = new LinkedHashSet<>();
				li.add(s);
			}
		}
		return li;
	}
	
	public static boolean canStack(ItemStack a, ItemStack b) {
		return a.isSameItem(b) && ItemStack.tagEquals(a, b);
	}

	public void mouseDown(int x, int y, int btn) {
		if (btn != 0 && btn != 1) return;
		if (btn == 0) {
			long now = Minecraft.getSystemTime();
			if (gui.mc.player.inventory.getItemStack() == null && now-lastLeftClick < 250) {
				Slot s = gui.getSlotAtPosition(x, y);
				if (s != null) {
					byte[] bys = new byte[4];
					ByteBuffer.wrap(bys)
						.putInt(s.slotNumber);
					Minecraft.instance().getSendQueue().addToSendQueue(new Packet250CustomPayload("υcollect", bys));
					lastLeftClick = -4000;
				}
			} else {
				lastLeftClick = now;
			}
		}
		if (buttonDown == -1) {
			if (gui.mc.player.inventory.getItemStack() == null) {
				vanillaClick(x, y, btn, true);
			} else {
				buttonDown = btn;
			}
		}
	}
	
	public void mouseUp(int x, int y, int btn) {
		if (btn != 0 && btn != 1) return;
		if (smearedSlots.size() <= 1 && buttonDown != -1) {
			smearedSlots.clear();
			if (btn == gui.mc.options.keyBindPickBlock.keyCode + 100) {
				// already handled
				return;
			}
			vanillaClick(x, y, btn, false);
		} else if (btn == buttonDown) {
			ItemStack hand = gui.mc.player.inventory.getItemStack();
			if (hand != null) {
				Set<Slot> eligibleSlots = determineEligibleSlots();
				if (eligibleSlots.isEmpty()) return;
				int amt = (btn == 1 ? 1 : hand.count/eligibleSlots.size());
				for (Slot s : eligibleSlots) {
					for (int i = 0; i < amt; i++) {
						// dirty hack for compatibility; just right-click N times
						gui.handleMouseClick(s, s.slotNumber, 1, 0);
					}
				}
			}
		}
		if (btn == buttonDown) {
			buttonDown = -1;
			smearedSlots.clear();
		}
	}
	
	private void vanillaClick(int x, int y, int btn, boolean down) {
		Slot slot = gui.getSlotAtPosition(x, y);
		
		int left = gui.guiLeft;
		int top = gui.guiTop;
		int right = left + gui.xSize;
		int bottom = top + gui.ySize;
		
		boolean clickedOutside = x < left || y < top || x >= right || y >= bottom;
		int slotNum;

		if (clickedOutside) {
			if (!down) {
				return;
			}
			slotNum = -999;
		} else if (slot != null) {
			slotNum = slot.slotNumber;
		} else {
			return;
		}
		boolean quickMove = smearedSlots.isEmpty() && slotNum != -999 && Screen.isShiftKeyDown();
		gui.handleMouseClick(slot, slotNum, btn, quickMove ? 1 : 0);
	}

	private int lastMouseX;
	private int lastMouseY;
	
	private Set<Slot> eligibleSlotsForDraw;
	
	public void drawScreenBg(int mX, int mY, float tickDelta) {
		if (buttonDown != -1) {
			if (mX != lastMouseX || mY != lastMouseY) {
				int xD = lastMouseX-mX;
				int yD = lastMouseY-mY;
				for (int i = 0; i < 8; i++) {
					float a = i/8f;
					Slot slot = gui.getSlotAtPosition((int)(lastMouseX+(xD*a)), (int)(lastMouseY+(yD*a)));
					if (slot != null) {
						smearedSlots.add(slot);
					}
				}
			}
		}
		eligibleSlotsForDraw = determineEligibleSlots();
		if (eligibleSlotsForDraw.size() == 1) {
			eligibleSlotsForDraw = Collections.emptySet();
		}
		glDisable(GL_DEPTH_TEST);
		for (Slot s : eligibleSlotsForDraw) {
			int x = s.x+gui.guiLeft;
			int y = s.y+gui.guiTop;
			gui.drawGradientRect(x, y, x + 16, y + 16, 0x80FFFFFF, 0x80FFFFFF);
		}
		glEnable(GL_DEPTH_TEST);
		lastMouseX = mX;
		lastMouseY = mY;
	}
	
	public ItemStack modifySlotStack(Slot slot, ItemStack stack) {
		if (eligibleSlotsForDraw.contains(slot)) {
			if (stack == null) {
				ItemStack cursor = gui.mc.player.inventory.getItemStack();
				if (cursor != null) {
					stack = cursor.copy();
					stack.count = 1;
				}
			} else {
				stack = stack.copy();
				stack.count = 1;
			}
		}
		return stack;
	}
	
	private int remainderForRender = 0;
	
	public void drawScreenFg(int mX, int mY, float tickDelta) {
		ItemStack cursor = gui.mc.player.inventory.getItemStack();
		if (cursor != null) {
			int total = cursor.count;
			int available = cursor.count;
			for (Slot s : eligibleSlotsForDraw) {
				int effective = s.hasStack() ? s.getStack().count : 0;
				int max = Math.min(s.getMaxCount(), s.hasStack() ? s.getStack().getMaxCount() : cursor.getMaxCount());
				int toAdd = Math.min(available, Math.min(max-effective, buttonDown == 0 ? total/eligibleSlotsForDraw.size() : 1));
				available -= toAdd;
				effective += toAdd;
				if (effective <= 1) continue;
				String str = ""+effective;
				if (effective >= max) {
					str = "§e"+str;
				}
				glDisable(GL_LIGHTING);
				glDisable(GL_DEPTH_TEST);
				gui.fontRenderer.drawStringWithShadow(str, s.x + 19 - 2 - gui.fontRenderer.getStringWidth(str), s.y + 6 + 3, 0xFFFFFFFF);
				glEnable(GL_LIGHTING);
				glEnable(GL_DEPTH_TEST);
			}
			remainderForRender = available;
		}
	}
	
	public ItemStack modifyCursorStack(ItemStack stack) {
		if (remainderForRender == 0) {
			return null;
		} else if (stack != null && stack.count != remainderForRender) {
			stack = stack.copy();
			stack.count = remainderForRender;
		}
		eligibleSlotsForDraw = null;
		return stack;
	}

}
