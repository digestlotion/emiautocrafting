package com.example.emiautocrafting;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.registry.EmiRecipeFiller;
import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public final class EmiAutocrafting {

    public static final String MOD_ID = "emiautocrafting";

    public static void craftTree() {
        ArrayDeque<MaterialNode> q = new ArrayDeque<>();
        q.push(BoM.tree.goal);
        while (!q.isEmpty()) {
            MaterialNode n = q.pop();
            if (n.children != null) n.children.forEach(q::push);

            if (n.recipe == null) continue;

            if (
                EmiRecipeFiller.performFill(
                    n.recipe,
                    EmiApi.getHandledScreen(),
                    EmiCraftContext.Type.CRAFTABLE,
                    EmiCraftContext.Destination.INVENTORY,
                    (int) n.neededBatches
                )
            ) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                BoM.tree.recalculate();
            }
        }
    }
}
