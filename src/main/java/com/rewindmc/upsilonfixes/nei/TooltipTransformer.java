package com.rewindmc.upsilonfixes.nei;

import com.rewindmc.upsilonfixes.UpsilonMiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("codechicken.nei.forge.GuiContainerManager")
public class TooltipTransformer extends UpsilonMiniTransformer {

    @Patch.Method("drawMultilineTip(IILjava/util/List;)V")
    public void patchDrawMultilineTip(PatchContext ctx) {
        ctx.search(
            ILOAD(1),
            ILOAD(4),
            IADD()
        ).jumpBefore();

        ctx.add(
            ILOAD(1),
            ILOAD(4),

            ALOAD(0),
            GETFIELD("codechicken/nei/forge/GuiContainerManager", "window", "Lavf;"),
            GETFIELD("avf", "g", "I"),

            INVOKESTATIC(hooks(), "flipTooltip", "(III)I"),
            ISTORE(1)
        );
    }

    public static class Hooks {
        public static int flipTooltip(int x, int tooltipWidth, int screenWidth) {
            if (x + tooltipWidth + 12 > screenWidth - 8) {
                return x - tooltipWidth - 24;
            }

            return x;
        }
    }
}
