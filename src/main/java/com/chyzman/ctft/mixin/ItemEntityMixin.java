package com.chyzman.ctft.mixin;

import com.chyzman.ctft.item.CtftItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static com.chyzman.ctft.Ctft.MODID;
import static com.chyzman.ctft.registries.CtftStats.MATERIALS;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getStack();

    @Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
    public void noBurny(CallbackInfoReturnable<Boolean> cir) {
        if (!(getStack().getItem() instanceof CtftItem)) return;
        if (getStack().getNbt() != null && getStack().getNbt().getString("material") != null) {
            if (getStack().getNbt().getString("material").equals("random")) {
                cir.setReturnValue(new Random().nextBoolean());
            }
            var id = Identifier.tryParse(getStack().getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            if (material.fire_immune) {
                cir.setReturnValue(true);
            }
        }
    }
}