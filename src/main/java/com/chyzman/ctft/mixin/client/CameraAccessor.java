package com.chyzman.ctft.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Mutable
    @Accessor("rotation")
    void ctft$setRotation(Quaternionf rotation);

    @Accessor("pos")
    void ctft$setPos(Vec3d pos);
}