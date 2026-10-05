package com.shasin.decibel.client.mixin;

import com.shasin.decibel.config.DecibelConfig;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(
            method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F",
            at = @At("RETURN"),
            cancellable = true
    )
    private void decibel$applyCustomVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        if (sound != null && sound.getIdentifier() != null) {
            Identifier soundId = sound.getIdentifier();
            float customMultiplier = DecibelConfig.get().getVolume(soundId);

            // Multiply Minecraft's calculated volume by Decibel's custom multiplier
            cir.setReturnValue(cir.getReturnValueF() * customMultiplier);
        }
    }
}