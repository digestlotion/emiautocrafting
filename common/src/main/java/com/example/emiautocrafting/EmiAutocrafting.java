package com.example.emiautocrafting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import com.tom.storagemod.gui.StorageTerminalMenu;

import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.registry.EmiRecipeFiller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public final class EmiAutocrafting {

    public static final String MOD_ID = "emiautocrafting";
    public static boolean isWorking = false;

    public static void performTreeCraft() {
        if (isWorking) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (BoM.tree == null || BoM.tree.goal == null) return;
        HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
        if (screen == null) return;
        ScreenHandler screenHandler = screen.getScreenHandler();
        isWorking = true;
        for (MaterialNode node : getTreeRecipes(BoM.tree.goal)) {
            if (node.recipe == null || node.neededBatches <= 0) continue;
            if (!node.recipe.supportsRecipeTree() || !node.recipe.getCategory().getId().equals(Identifier.of("minecraft", "crafting"))) continue;
            if (EmiRecipeFiller.performFill(node.recipe, screen, EmiCraftContext.Type.FILL_BUTTON, EmiCraftContext.Destination.INVENTORY, (int) Math.ceil(node.neededBatches))) {
                BoM.tree.recalculate();
                if (EmiAgnos.isModLoaded("toms_storage") && screenHandler instanceof CraftingTerminalMenu menu) {
                    for (int i = 0; i < Math.ceil(node.neededBatches); ++i) {
                        client.interactionManager.clickSlot(menu.syncId, 0, 0, SlotActionType.PICKUP, client.player);
                        menu.sync.sendInteract(null, StorageTerminalMenu.SlotAction.PULL_OR_PUSH_STACK, false);
                    }
                }
                break;
            }
        }
        isWorking = false;
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
