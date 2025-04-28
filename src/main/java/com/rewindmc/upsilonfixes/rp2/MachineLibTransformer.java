package com.rewindmc.upsilonfixes.rp2;

import buildcraft.api.transport.IPipeEntry;
import com.eloraam.redpower.core.WorldCoord;
import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.EmptyInventory;
import com.rewindmc.upsilonfixes.UpsilonFixesConfig;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("com.eloraam.redpower.core.MachineLib")
@ConfigOptions({"redpowerBCCompat", "fixRedPowerMissingInventoryNPE"})
public class MachineLibTransformer extends UpsilonMiniTransformer {

	@Patch.Method("addToInventoryCore(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lcom/eloraam/redpower/core/WorldCoord;IZ)Z")
	@Patch.Method.AffectsControlFlow
	public void patchPipesIntoRP(PatchContext ctx) {
		if (UpsilonFixesConfig.redpowerBCCompat) {
			ctx.jumpToStart();
			LabelNode label = new LabelNode();
			ctx.add(
					ALOAD(0),
					ALOAD(1),
					ALOAD(2),
					ILOAD(3),
					ILOAD(4),
					INVOKESTATIC(hooks(), "pipeInsert", "(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lcom/eloraam/redpower/core/WorldCoord;IZ)Z"),
					IFEQ(label),
					ICONST_1(),
					IRETURN(),
					label
			);
		}
	}
	
	@Patch.Method("getInventory(Lnet/minecraft/world/World;Lcom/eloraam/redpower/core/WorldCoord;)Lnet/minecraft/inventory/IInventory;")
	public void patchGetInventory(PatchContext ctx) {
		if (UpsilonFixesConfig.fixRedPowerMissingInventoryNPE) {
			ctx.search(
				ALOAD(2),
				ARETURN()
			).jumpBefore();
			ctx.jumpForward(1);
			
			ctx.add(
				INVOKESTATIC(hooks(), "preventNullInventory", "(Lnet/minecraft/inventory/IInventory;)Lnet/minecraft/inventory/IInventory;")
			);
		}
	}

	public static class Hooks {
		public static boolean pipeInsert(World world, ItemStack ist, WorldCoord wc, int side, boolean act) {
			TileEntity te = world.getBlockTileEntity(wc.x, wc.y, wc.z);
			if (te instanceof IPipeEntry && ((IPipeEntry) te).acceptItems()) {
				if (act) {
					((IPipeEntry) te).entityEntering(ist.copy(), ForgeDirection.getOrientation(side).getOpposite());
				}
				return true;
			}
			return false;
		}
		
		public static IInventory preventNullInventory(IInventory subject) {
			if (subject == null) {
				return EmptyInventory.INSTANCE;
			}
			return subject;
		}
	}
	
}
