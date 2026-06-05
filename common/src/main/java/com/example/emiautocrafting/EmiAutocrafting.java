package com.example.emiautocrafting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.registry.EmiRecipeFiller;
import net.minecraft.client.gui.screen.ingame.HandledScreen;


public final class EmiAutocrafting {
    public static final String MOD_ID = "emiautocrafting";
    public static void performTreeCraft() {
        if (BoM.tree == null || BoM.tree.goal == null) return;
        HandledScreen<?> screen = EmiApi.getHandledScreen();
        if (screen == null) return;
        for (MaterialNode node : getTreeRecipes(BoM.tree.goal)) {
            if (node.recipe == null || node.neededBatches <= 0) continue;
            if (
                EmiRecipeFiller.performFill(
                    node.recipe,
                    screen,
                    EmiCraftContext.Type.FILL_BUTTON,
                    EmiCraftContext.Destination.INVENTORY,
                    (int) Math.ceil(node.neededBatches)
                )
            ) {
                BoM.tree.recalculate();
                return;
            }
        }
    }

    public static List<MaterialNode> getTreeRecipes(MaterialNode root) {
        ArrayDeque<MaterialNode> stack1 = new ArrayDeque<>();
        ArrayDeque<MaterialNode> stack2 = new ArrayDeque<>();
        stack1.push(root);
        while (!stack1.isEmpty()) {
            MaterialNode n = stack1.pop();
            stack2.push(n);
            if (n.children != null) {
                for (MaterialNode child : n.children) {
                    stack1.push(child);
                }
            }
        }
        return new ArrayList<>(stack2);
    }
}
