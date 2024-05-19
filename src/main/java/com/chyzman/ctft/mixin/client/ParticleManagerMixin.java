package com.chyzman.ctft.mixin.client;

import com.chyzman.ctft.particle.CtftParticleTypes;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Inject(method = "renderParticles", at = @At(value = "HEAD"))
    private void stealTheParticleMatrix(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
        CtftParticleTypes.particlesMatrix = matrices;
        CtftParticleTypes.particlesVertexConsumerProvider = vertexConsumers;
    }

    @Inject(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.BEFORE, ordinal = 0))
    private void renderItemParticles(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
        if (CtftParticleTypes.particlesVertexConsumerProvider instanceof VertexConsumerProvider.Immediate) {
            ((VertexConsumerProvider.Immediate) CtftParticleTypes.particlesVertexConsumerProvider).draw();
        }
    }
}