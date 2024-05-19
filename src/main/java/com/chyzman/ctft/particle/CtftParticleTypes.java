package com.chyzman.ctft.particle;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.chyzman.ctft.util.CtftRegistryHelper.id;

public class CtftParticleTypes implements AutoRegistryContainer<ParticleType<?>> {

    public static MatrixStack particlesMatrix;
    public static VertexConsumerProvider particlesVertexConsumerProvider;

    public static final ParticleType<ItemParticleEffect> ITEM = FabricParticleTypes.complex(ItemParticleEffect.FACTORY);

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, id("item"), ITEM);
    }

    @Override
    public Registry<ParticleType<?>> getRegistry() {
        return Registries.PARTICLE_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ParticleType<?>> getTargetFieldType() {
        return (Class<ParticleType<?>>) (Object) ParticleType.class;
    }
}