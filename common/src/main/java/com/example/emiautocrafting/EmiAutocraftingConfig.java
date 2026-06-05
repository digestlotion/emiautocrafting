package com.example.emiautocrafting;

import org.lwjgl.glfw.GLFW;

import dev.emi.emi.config.EmiConfig.*;
import dev.emi.emi.input.EmiBind;

public interface EmiAutocraftingConfig {

    @Comment(
        "When on a stack with an associated recipe tree:\n" +
            "Move ingredients for as many results as possible and put in inventory if possible."
    )
    @ConfigValue("binds.craft-tree")
    public static EmiBind craftTree = new EmiBind("key.emiautocrafting.craft_tree", GLFW.GLFW_KEY_N);
}
