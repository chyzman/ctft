package com.chyzman.ctft.mixin.client;

import com.chyzman.ctft.item.CtftItem;
import com.chyzman.ctft.particle.ItemParticle;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.registry.Registries;
import org.joml.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(CrackParticle.ItemFactory.class)
public abstract class CrackParticleItemFactoryMixin {
    @Inject(method = "createParticle(Lnet/minecraft/particle/ItemStackParticleEffect;Lnet/minecraft/client/world/ClientWorld;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    public void ctftParticles(ItemStackParticleEffect effect, ClientWorld world, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<Particle> cir) throws CommandSyntaxException {
        var stack = effect.getItemStack();
        if (!(stack.getItem() instanceof CtftItem)) return;
        ItemVariant variant;
        var nbt = stack.getNbt();
        var material = nbt.getString("material");
        if (material.equals("random")) {
            cir.setReturnValue(new ItemParticle(world, d, e, f, g, h, i));
        } else {
            var result = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), new StringReader(material));
            variant = ItemVariant.of(new ItemStackArgument(result.item(), result.nbt()).createStack(1, false));
            cir.setReturnValue(new ItemParticle(world, d, e, f, g, h, i, variant));
        }
    }
}