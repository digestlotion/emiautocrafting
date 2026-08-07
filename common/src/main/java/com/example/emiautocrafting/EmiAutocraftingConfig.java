package com.example.emiautocrafting;

import org.lwjgl.glfw.GLFW;

import dev.emi.emi.config.EmiConfig.Comment;
import dev.emi.emi.config.EmiConfig.ConfigValue;
import dev.emi.emi.input.EmiBind;

public interface EmiAutocraftingConfig {
    @Comment("Move ingredients for highest leaf and put in inventory if possible.")
    @ConfigValue("binds.craft-tree")
    public static EmiBind craftTree = new EmiBind("key.emiautocrafting.craft_tree", GLFW.GLFW_KEY_N);
}
