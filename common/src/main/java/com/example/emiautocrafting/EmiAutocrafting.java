package com.example.emiautocrafting;

import java.util.ArrayDeque;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import com.tom.storagemod.gui.StorageTerminalMenu.SlotAction;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.registry.EmiRecipeFiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;

public final class EmiAutocrafting {

    public static final String MOD_ID = "emiautocrafting";

    private static boolean lock = false;

    public static void craftTree() {
        if (BoM.tree == null || BoM.tree.goal == null) return;
        ArrayDeque<MaterialNode> q = new ArrayDeque<>();
        q.push(BoM.tree.goal);
        if (lock) return;
        lock = true;
        while (!q.isEmpty()) {
            MaterialNode n = q.pop();
            if (n.children != null) n.children.forEach(q::push);
            if (n.recipe == null) continue;
            // prettier-ignore
            if (EmiRecipeFiller.performFill(n.recipe, EmiApi.getHandledScreen(), EmiCraftContext.Type.CRAFTABLE,
                EmiCraftContext.Destination.INVENTORY, (int) n.neededBatches)) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                // there has to be a better way
                if (EmiAgnos.isModLoaded("toms_storage") && EmiApi.getHandledScreen().getScreenHandler() instanceof CraftingTerminalMenu menu) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    for (int i = 0; i < n.neededBatches; i++) {
                        client.interactionManager.clickSlot(menu.syncId, 0, 0, SlotActionType.PICKUP, client.player);
                        if (!n.equals(BoM.tree.goal)) menu.sync.sendInteract(null, SlotAction.PULL_OR_PUSH_STACK, false);
                    }
                }
                BoM.tree.recalculate();
            }
        }
        lock = false;
    }
}
