package com.example.emiautocrafting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.tom.storagemod.gui.CraftingTerminalMenu;
import com.tom.storagemod.gui.StorageTerminalMenu;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext.Destination;
import dev.emi.emi.api.recipe.handler.EmiCraftContext.Type;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.runtime.EmiFavorite.Synthetic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class EmiAutocrafting {

    public static final String MOD_ID = "emiautocrafting";
    public static boolean lock = false;

    public static boolean craftToNode(EmiIngredient ingredient) {
        if (ingredient.equals(EmiIngredient.of(Ingredient.ofItems(Items.AIR), 1))) {
            return craftToNode(BoM.tree.goal);
        } else {
            return craftToNode(getNode(ingredient));
        }
    }

    private static boolean craftToNode(MaterialNode root) {
        if (lock) return false;
        lock = true;
        boolean crafted = false;
        for (MaterialNode node : getTree(root)) {
            if (node.recipe == null || node.neededBatches <= 0 || !node.recipe.getCategory().getId().equals(Identifier.of("minecraft", "crafting"))) continue;
            if (craftNode(node)) {
                if (EmiAgnos.isModLoaded("toms_storage") && EmiApi.getHandledScreen().getScreenHandler() instanceof CraftingTerminalMenu menu) onTomsCraft(menu, node);
                crafted = true;
                break;
            }
        }
        lock = false;
        return crafted;
    }

    private static MaterialNode getNode(EmiIngredient ingredient) {
        EmiIngredient target = ingredient instanceof Synthetic synthetic ? synthetic.getStack() : ingredient;
        for (MaterialNode node : getTree(BoM.tree.goal)) {
            if (node.ingredient.equals(target)) return node;
        }
        return null;
    }

    private static boolean craftNode(MaterialNode node) {
        lock = true;
        if (EmiRecipeFiller.performFill(node.recipe, EmiApi.getHandledScreen(), Type.FILL_BUTTON, Destination.INVENTORY, (int) node.neededBatches)) {
            BoM.tree.recalculate();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return true;
        }
        return false;
    }

    private static List<MaterialNode> getTree(MaterialNode node) {
        List<MaterialNode> result = new ArrayList<>();
        if (node == null) return result;
        Deque<MaterialNode> stack = new ArrayDeque<>();
        stack.push(node);
        while (!stack.isEmpty()) {
            MaterialNode n = stack.pop();
            if (n == null) continue;
            result.add(n);
            if (n.children != null) {
                for (MaterialNode child : n.children) {
                    if (child != null) stack.push(child);
                }
            }
        }
        return result;
    }

    private static void onTomsCraft(CraftingTerminalMenu menu, MaterialNode node) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (int i = 0; i < Math.ceil(node.neededBatches); ++i) {
            client.interactionManager.clickSlot(menu.syncId, 0, 0, SlotActionType.PICKUP, client.player);
            menu.sync.sendInteract(null, StorageTerminalMenu.SlotAction.PULL_OR_PUSH_STACK, false);
        }
    }
}
