package com.rewindmc.upsilonfixes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class EmptyInventory implements IInventory {

	public static final EmptyInventory INSTANCE = new EmptyInventory();
	
	@Override
	public void closeChest() {
		
	}

	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		throw new ArrayIndexOutOfBoundsException(arg0);
	}

	@Override
	public String getInvName() {
		return "empty";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int arg0) {
		throw new ArrayIndexOutOfBoundsException(arg0);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int arg0) {
		throw new ArrayIndexOutOfBoundsException(arg0);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer arg0) {
		return false;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public void openChest() {
	}

	@Override
	public void setInventorySlotContents(int arg0, ItemStack arg1) {
		throw new ArrayIndexOutOfBoundsException(arg0);
	}

}
