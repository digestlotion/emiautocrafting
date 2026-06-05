package com.example.emiautocrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.example.emiautocrafting.EmiAutocraftingConfig;

import dev.emi.emi.config.EmiConfig;

@Mixin(value = EmiConfig.class, remap = false)
public abstract class EmiConfigMixin implements EmiAutocraftingConfig {}
