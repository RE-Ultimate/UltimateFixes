package com.rewindmc.upsilonfixes.newjava;

import com.rewindmc.upsilonfixes.ConfigOptions;
import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import com.rewindmc.upsilonfixes.newjava.support.AltComputerThread;

import dan200.computer.core.ComputerThread;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("dan200.computer.core.ComputerThread")
@ConfigOptions("modernJavaSupport")
public class ComputerThreadTransformer extends UpsilonMiniTransformer {

	@Patch.Method("start()V")
	public void patchStart(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			INVOKESTATIC(hooks(), "start", "()V"),
			RETURN()
		);
	}

	@Patch.Method("stop()V")
	public void patchStop(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			INVOKESTATIC(hooks(), "stop", "()V"),
			RETURN()
		);
	}
	
	@Patch.Method("queueTask(Ldan200/computer/core/ComputerThread$Task;)V")
	public void patchQueueTask(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			ALOAD(0),
			INVOKESTATIC(hooks(), "queueTask", "(Ldan200/computer/core/ComputerThread$Task;)V"),
			RETURN()
		);
	}

	@Patch.Method("isBusy()Z")
	public void patchIsBusy(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
			INVOKESTATIC(hooks(), "isBusy", "()Z"),
			IRETURN()
		);
	}
	
	public static class Hooks {
		
		public static void start() {
			AltComputerThread.start();
		}
		
		public static void stop() {
			AltComputerThread.stop();
		}
		
		public static void queueTask(ComputerThread.Task task) {
			AltComputerThread.queueTask(task, task.getOwner());
		}
		
		public static boolean isBusy() {
			// nothing calls this method...?
			return false;
		}
		
	}
	
}
