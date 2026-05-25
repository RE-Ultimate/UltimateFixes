package com.rewindmc.upsilonfixes.vanilla.tattle;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.NonNullList;
import com.rewindmc.upsilonfixes.SingleThreadRandom;
import com.rewindmc.upsilonfixes.UpsilonFixesPremain;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;

import net.minecraft.world.World;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net/minecraft/world/World")
@ConfigOptions("tattleThreadSafety")
public class WorldTransformer extends UpsilonMiniTransformer {

	@Patch.Method("<init>(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;)V")
	@Patch.Method("<init>(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;)V")
	public void patchInit(PatchContext ctx) {
		ctx.jumpToLastReturn();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC(hooks(), "setup", "(Lnet/minecraft/world/World;)V")
		);
	}
	
	@Patch.Method("getBlockId(III)I")
	@Patch.Method("getBlockMetadata(III)I")
	@Patch.Method("setBlockMetadata(IIII)Z")
	@Patch.Method("getBlockTileEntity(III)Lnet/minecraft/tileentity/TileEntity;")
	@Patch.Method("getWorldVec3Pool()Lnet/minecraft/util/Vec3dPool;")
	@Patch.Method("getCollidingBoundingBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AABB;)Ljava/util/List;")
	@Patch.Method("getAllCollidingBoundingBoxes(Lnet/minecraft/util/AABB;)Ljava/util/List;")
	public void patch(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			LDC(ctx.getMethodName()),
			INVOKESTATIC(hooks(), "tattle", "(Lnet/minecraft/world/World;Ljava/lang/String;)V")
		);
	}

	public static class Hooks {
		
		private static final ReadWriteLock ownerLock = new ReentrantReadWriteLock(false);
		private static final Map<World, Thread> owners = new WeakHashMap<>();
		
		public static void setup(World world) {
			ownerLock.writeLock().lock();
			try {
				UpsilonFixesPremain.log.info("World created on "+Thread.currentThread().getName());
				owners.put(world, Thread.currentThread());
			} finally {
				ownerLock.writeLock().unlock();
			}
			world.rand = new SingleThreadRandom();
			world.collidingBoundingBoxes = new NonNullList<>();
		}
		
		public static void tattle(World world, String method) {
			ownerLock.readLock().lock();
			try {
				Thread owner = owners.get(world);
				if (owner != null && Thread.currentThread() != owner) {
					UpsilonFixesPremain.log.error("Unsafe off-thread access to World.{}! (Owning thread: {}, violating thread: {}, dim: {})",
							method, owner.getName(), Thread.currentThread().getName(), world.provider == null ? "CLIENT" : world.provider.dimensionId,
							new Throwable("Stack trace").fillInStackTrace());
				}
			} finally {
				ownerLock.readLock().unlock();
			}
		}
		
	}
	
}
