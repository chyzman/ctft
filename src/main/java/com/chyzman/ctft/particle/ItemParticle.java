package com.chyzman.ctft.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import static com.chyzman.ctft.particle.CtftParticleTypes.*;

@SuppressWarnings("UnstableApiUsage")
public class ItemParticle extends Particle {

    protected ItemVariant variant;

    public ItemParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemVariant variant, boolean random) {
        super(world, x, y, z);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.variant = random ? ItemVariant.of(Registries.ITEM.get(world.random.nextInt(Registries.ITEM.size()))) : variant;
        this.gravityStrength = 1F;
        this.angle = (float) (Math.random() * Math.PI * 2.0D);
    }

    public ItemParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemVariant variant) {
        this(world, x, y, z, velocityX, velocityY, velocityZ, variant, false);
    }

    public ItemParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(world, x, y, z, velocityX, velocityY, velocityZ, ItemVariant.blank(), true);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        var client = MinecraftClient.getInstance();
        var matrices = new MatrixStack();

        Quaternionf quaternionf;
        if (this.angle == 0.0F) {
            quaternionf = new Quaternionf();
        } else {
            quaternionf = new Quaternionf();
            quaternionf.rotateY(this.angle);
        }

        matrices.push();
        matrices.translate(
                MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camera.getPos().getX(),
                MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camera.getPos().getY(),
                MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camera.getPos().getZ()
        );
        matrices.multiply(quaternionf);

        var scale = 1/3f;
        matrices.scale(scale, scale, scale);

        matrices.translate(0, 1/4f, 0);

        client.getItemRenderer().renderItem(
                this.variant.toStack(),
                ModelTransformationMode.FIXED,
                getBrightness(tickDelta),
                OverlayTexture.DEFAULT_UV,
                matrices,
                particlesVertexConsumerProvider,
                world,
                0
        );
        matrices.pop();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }
}