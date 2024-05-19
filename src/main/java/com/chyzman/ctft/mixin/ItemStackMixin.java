package com.chyzman.ctft.mixin;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static com.chyzman.ctft.Ctft.MODID;
import static com.chyzman.ctft.registries.CtftStats.MATERIALS;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    @Nullable
    public abstract NbtCompound getNbt();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract boolean isFood();

    @Shadow
    protected abstract Style method_7966(Style par1);

    @Shadow
    public abstract UseAction getUseAction();

    FoodComponent food;

    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    public void damage(CallbackInfoReturnable<Integer> cir) {
        if (!Registries.ITEM.getId(getItem()).getNamespace().equals(MODID)) return;
        if (getNbt() != null && getNbt().getString("material") != null) {
            if (getNbt().getString("material").equals("random")) {
                cir.setReturnValue(new Random().nextInt(Math.min(99, getDamage()), 100));
            }
            var id = Identifier.tryParse(getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            cir.setReturnValue(material.durability);
        }
    }

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    public void unbreakable(CallbackInfoReturnable<Boolean> cir) {
        if (!Registries.ITEM.getId(getItem()).getNamespace().equals(MODID)) return;
        if (getNbt() != null && getNbt().getString("material") != null) {
            if (getNbt().getString("material").equals("random")) {
                cir.setReturnValue(new Random().nextBoolean());
            }
            var id = Identifier.tryParse(getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            cir.setReturnValue(!material.unbreakable);
        }
    }

    @Inject(method = "hasGlint", at = @At("TAIL"), cancellable = true)
    public void shiny(CallbackInfoReturnable<Boolean> cir) {
        if (!Registries.ITEM.getId(getItem()).getNamespace().equals(MODID)) return;
        if (getNbt() != null && getNbt().getString("material") != null) {
            var id = Identifier.tryParse(getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            cir.setReturnValue(cir.getReturnValue() || material.foil || Registries.ITEM.get(id).hasGlint(new ItemStack(Registries.ITEM.get(id))));
        }
    }

    @Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
    public void makeToolsMineStuffTheyShouldBeAbleToMine(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!Registries.ITEM.getId(getItem()).getNamespace().equals(MODID)) return;
        if (!(getItem() instanceof MiningToolItem miningToolItem)) return;
        if (getNbt() != null && getNbt().getString("material") != null) {
            if (getNbt().getString("material").equals("random")) {
                cir.setReturnValue(new Random().nextBoolean());
            }
            var id = Identifier.tryParse(getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            if (material.mining_tier < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
                cir.setReturnValue(false);
            } else if (material.mining_tier < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
                cir.setReturnValue(false);
            } else {
                cir.setReturnValue((material.mining_tier >= MiningLevels.STONE || !state.isIn(BlockTags.NEEDS_STONE_TOOL)) && state.isIn(miningToolItem.effectiveBlocks));
            }
        }
    }

    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    public void shouldItBeFood(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!Registries.ITEM.getId(getItem()).getNamespace().equals(MODID)) return;
        if (getNbt() != null && getNbt().getString("material") != null) {
            var id = Identifier.tryParse(getNbt().getString("material"));
            if (id == null) return;
            var material = MATERIALS.get(id);
            if (material == null) return;
            FoodComponent foodComponent = null;
//            if (material.is_food) {
//                var builder = new FoodComponent.Builder().hunger(material.nutrition).saturationModifier(material.saturation);
//                if (material.can_always_eat) builder.alwaysEdible();
//                if (material.is_fast) builder.snack();
//                if (material.is_meat) builder.meat();
//                foodComponent = builder.build();
//            } else
                if (Registries.ITEM.containsId(id)) {
                var item = Registries.ITEM.get(id);
                if (item.isFood()) {
                    foodComponent = item.getFoodComponent();
                }
            }
            if (foodComponent != null && (user.isSneaking() || (getUseAction().equals(UseAction.NONE) || getUseAction().equals(UseAction.EAT)))) {
                food = foodComponent;
                ItemStack itemStack = user.getStackInHand(hand);
                if (user.canConsume(foodComponent.isAlwaysEdible())) {
                    user.setCurrentHand(hand);
                    cir.setReturnValue(TypedActionResult.consume(itemStack));
                } else {
                    cir.setReturnValue(TypedActionResult.fail(itemStack));
                }
            }
        }
    }

    @Inject(method = "isFood", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$isFood(CallbackInfoReturnable<Boolean> cir) {
        if (food != null) {
            cir.setReturnValue(true);
        }
    }

//    @Inject(method = "usageTick", at = @At(value = "HEAD"), cancellable = true)
//    public void eatAnything$usageTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
//        if (user instanceof PlayerEntity playerEntity && player == null) {
//            player = playerEntity;
//        }
//    }

    @Inject(method = "getUseAction", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$getUseAction(CallbackInfoReturnable<UseAction> cir) {
        if (food != null) {
            cir.setReturnValue(UseAction.EAT);
        }
    }

    @Inject(method = "getMaxUseTime", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        if (food != null) {
            cir.setReturnValue(32);
        }
    }

    @Inject(method = "isUsedOnRelease", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$isUsedOnRelease(CallbackInfoReturnable<Boolean> cir) {
        if (food != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onStoppedUsing", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$onStoppedUsing(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (food != null) {
            food = null;
            ci.cancel();
        }
    }

    @Inject(method = "finishUsing", at = @At(value = "HEAD"), cancellable = true)
    public void eatAnything$finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (food != null) {
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    user.getEatSound((ItemStack) (Object) this),
                    SoundCategory.NEUTRAL,
                    1.0F,
                    1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
            );
            for (Pair<StatusEffectInstance, Float> pair : food.getStatusEffects()) {
                if (!world.isClient && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                    user.addStatusEffect(new StatusEffectInstance(pair.getFirst()));
                }
            }
            if (!(user instanceof PlayerEntity player) || !player.getAbilities().creativeMode) {
                ((PlayerEntity)user).getHungerManager().add(food.getHunger(), food.getSaturationModifier());
                ((PlayerEntity)user).incrementStat(Stats.USED.getOrCreateStat(getItem()));
                ((ItemStack) (Object) this).decrement(1);
            }
            user.emitGameEvent(GameEvent.EAT);
        }
    }
}