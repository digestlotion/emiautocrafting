package com.example.emiautocrafting.mixin;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.emiautocrafting.EmiAutocrafting;
import com.example.emiautocrafting.EmiAutocraftingConfig;

import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.input.EmiBind;
import dev.emi.emi.screen.EmiScreenManager;

@Mixin(value = EmiScreenManager.class, remap = false)
public class EmiScreenManagerMixin {

    @Inject(method = "stackInteraction", at = @At("HEAD"), cancellable = true)
    private static void onStackInteraction(EmiStackInteraction stack, Function<EmiBind, Boolean> function, CallbackInfoReturnable<Boolean> cir) {
        if (function.apply(EmiAutocraftingConfig.craftTree) && !EmiAutocrafting.isWorking) {
            EmiAutocrafting.performTreeCraft();
            cir.setReturnValue(true);
        }
    }
}
