package com.example.emiautocrafting;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

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
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public final class EmiAutocrafting {

    public static final String MOD_ID = "emiautocrafting";
    public static boolean lock = false;

    public static void craftTree(MaterialNode root) {
        while (craftToNode(root));
    }

    public static boolean craftToNode(MaterialNode root) {
        if (lock) return false;
        lock = true;
        boolean crafted = false;
        for (Map.Entry<EmiIngredient, MaterialNode> leaf : getTree(root).entrySet()) {
            if (leaf.getValue().recipe == null || leaf.getValue().neededBatches <= 0 || !leaf.getValue().recipe.getCategory().getId().equals(Identifier.of("minecraft", "crafting"))) continue;
            if (craftNode(leaf.getValue())) {
                if (EmiAgnos.isModLoaded("toms_storage") && EmiApi.getHandledScreen().getScreenHandler() instanceof CraftingTerminalMenu menu) onTomsCraft(menu, leaf.getValue());
                crafted = true;
                break;
            }
        }
        lock = false;
        return crafted;
    }

    public static MaterialNode getNode(EmiIngredient ingredient) {
        if (ingredient instanceof Synthetic synthetic) return getTree(BoM.tree.goal).get(synthetic.getStack());
        return getTree(BoM.tree.goal).get(ingredient);
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

    private static LinkedHashMap<EmiIngredient, MaterialNode> getTree(MaterialNode node) {
        LinkedHashMap<EmiIngredient, MaterialNode> tree = new LinkedHashMap<>();
        Deque<MaterialNode> stack = new ArrayDeque<>();
        stack.push(node);
        while (!stack.isEmpty()) {
            MaterialNode n = stack.pop();
            tree.put(n.ingredient, n);
            if (n.children != null) n.children.forEach(stack::push);
        }
        return tree;
    }

    private static void onTomsCraft(CraftingTerminalMenu menu, MaterialNode node) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (int i = 0; i < Math.ceil(node.neededBatches); ++i) {
            client.interactionManager.clickSlot(menu.syncId, 0, 0, SlotActionType.PICKUP, client.player);
            menu.sync.sendInteract(null, StorageTerminalMenu.SlotAction.PULL_OR_PUSH_STACK, false);
        }
    }
}
