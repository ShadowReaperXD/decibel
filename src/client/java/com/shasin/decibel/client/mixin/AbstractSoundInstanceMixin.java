package com.shasin.decibel.client.mixin;

import com.shasin.decibel.config.DecibelConfig;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void decibel$scaleVolume(CallbackInfoReturnable<Float> cir) {
        AbstractSoundInstance sound = (AbstractSoundInstance) (Object) this;
        ResourceLocation location = sound.getLocation();

        if (location != null) {
            float customMultiplier = DecibelConfig.get().getVolume(location);
            cir.setReturnValue(cir.getReturnValueF() * customMultiplier);
        }
    }
}