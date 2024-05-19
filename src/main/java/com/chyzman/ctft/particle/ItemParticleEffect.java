package com.chyzman.ctft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public record ItemParticleEffect(ItemVariant variant, Vec3d velocity, Integer lifespan) implements ParticleEffect {

    public static final Factory<ItemParticleEffect> FACTORY =
            new Factory<>() {
                @Override
                public ItemParticleEffect read(ParticleType<ItemParticleEffect> type, StringReader reader) {
                    var variant = readVariant(reader);
                    return new ItemParticleEffect(variant, readVelocity(reader), readLifeSpan(reader));
                }

                @Override
                public ItemParticleEffect read(ParticleType<ItemParticleEffect> type, PacketByteBuf buf) {
                    return new ItemParticleEffect(
                            ItemVariant.fromPacket(buf),
                            buf.readBoolean() ? new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()) : null,
                            buf.readBoolean() ? buf.readInt() : null
                    );
                }
            };

    @Override
    public ParticleType<?> getType() {
        return CtftParticleTypes.ITEM;
    }

    @Override
    public void write(PacketByteBuf buf) {
        variant.toPacket(buf);
        buf.writeBoolean(velocity != null);
        if (velocity != null) {
            buf.writeDouble(velocity.x);
            buf.writeDouble(velocity.y);
            buf.writeDouble(velocity.z);
        }
        buf.writeBoolean(lifespan != null);
        if (lifespan != null) {
            buf.writeInt(lifespan);
        }
    }

    @Override
    public String asString() {
        return Registries.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.variant.toStack().getRegistryEntry(), this.variant.toStack().getNbt()).asString();
    }

    public static ItemVariant readVariant(StringReader reader) {
        var cursor = reader.getCursor();
        try {
            reader.expect(' ');
            ItemStringReader.ItemResult itemResult = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), reader);
            return ItemVariant.of(new ItemStackArgument(itemResult.item(), itemResult.nbt()).createStack(1, false));
        } catch (CommandSyntaxException e) {
            reader.setCursor(cursor);
            return ItemVariant.blank();
        }
    }

    @Nullable
    public static Vec3d readVelocity(StringReader reader) {
        var cursor = reader.getCursor();
        try {
            reader.expect(' ');
            var x = reader.readDouble();
            reader.expect(' ');
            var y = reader.readDouble();
            reader.expect(' ');
            var z = reader.readDouble();
            return new Vec3d(x, y, z);
        } catch (CommandSyntaxException e) {
            reader.setCursor(cursor);
            return null;
        }
    }

    @Nullable
    public static Integer readLifeSpan(StringReader reader) {
        var cursor = reader.getCursor();
        try {
            reader.expect(' ');
            return reader.readInt();
        } catch (CommandSyntaxException e) {
            reader.setCursor(cursor);
            return null;
        }
    }

    @Environment(EnvType.CLIENT)
    public record ParticleFactory(
            SpriteProvider spriteProvider) implements net.minecraft.client.particle.ParticleFactory<ItemParticleEffect> {
        @Override
        public @NotNull Particle createParticle(ItemParticleEffect effect, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            var velocity = new Vec3d(velocityX, velocityY, velocityZ);
            if (effect.velocity() != null) {
                velocity = velocity.add(effect.velocity());
            }
            var particle = new ItemParticle(world, x, y, z, velocity.x, velocity.y, velocity.z, effect.variant().isBlank() ? ItemVariant.of(Registries.ITEM.get(world.getRandom().nextInt(Registries.ITEM.size()))) : effect.variant());
            if (effect.lifespan() != null) {
                particle.setMaxAge(effect.lifespan());
            }
            return particle;
        }
    }
}