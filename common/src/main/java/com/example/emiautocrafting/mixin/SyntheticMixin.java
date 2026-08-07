package com.example.emiautocrafting.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.example.emiautocrafting.EmiAutocraftingConfig;

import dev.emi.emi.EmiPort;
import dev.emi.emi.runtime.EmiFavorite;
import net.minecraft.client.gui.tooltip.TooltipComponent;

@Mixin(value = EmiFavorite.Synthetic.class, remap = false)
public class SyntheticMixin {

    @Inject(method = "getTooltip", at = @At(value = "RETURN"), cancellable = true)
    private void onGetTooltip(CallbackInfoReturnable<List<TooltipComponent>> cir) {
        cir.getReturnValue().add(TooltipComponent.of(EmiPort.translatable("tooltip.emiautocrafting.autocraft_tree", EmiAutocraftingConfig.craftTree.getBindText()).asOrderedText()));
    }
}
