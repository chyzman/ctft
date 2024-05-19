package com.chyzman.ctft.item;

import com.chyzman.ctft.classes.CtftMaterial;
import com.chyzman.ctft.mixin.ItemAccessor;
import com.chyzman.ctft.util.CtftOverrideHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.chyzman.ctft.registries.CtftStats.MATERIALS;

public class CtftSwordItem extends SwordItem implements CtftItem {
    public CtftSwordItem(Settings settings) {
        super(
                CtftMaterial.CTFT,
                0,
                0,
                settings.maxCount(1)
        );
    }

    @Override
    public Text getName(ItemStack stack) {
        return CtftOverrideHelper.getName(stack, "item.ctft.sword", stack1 -> super.getName(stack));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        var attribute_modifiers = HashMultimap.create(super.getAttributeModifiers(stack, slot));
        if (stack.getNbt() != null && stack.getNbt().getString("material") != null) {
            if (stack.getNbt().getString("material").equals("random")) {
                if (slot.equals(EquipmentSlot.MAINHAND)) {
                    attribute_modifiers.clear();
                    attribute_modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ItemAccessor.ctft$ATTACK_DAMAGE_MODIFIER_ID(), "damage", new Random().nextDouble(0,20), EntityAttributeModifier.Operation.ADDITION));
                    attribute_modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ItemAccessor.ctft$ATTACK_SPEED_MODIFIER_ID(), "speed", new Random().nextDouble(0,5), EntityAttributeModifier.Operation.ADDITION));
                    attribute_modifiers.putAll(super.getAttributeModifiers(stack, slot));
                    return attribute_modifiers;
                }
            }
            var id = Identifier.tryParse(stack.getNbt().getString("material"));
            if (id == null) return attribute_modifiers;
            var material = MATERIALS.get(id);
            if (material == null) return attribute_modifiers;
            if (slot.equals(EquipmentSlot.MAINHAND)) {
                attribute_modifiers.clear();
                attribute_modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ItemAccessor.ctft$ATTACK_DAMAGE_MODIFIER_ID(), "damage", material.sword_damage, EntityAttributeModifier.Operation.ADDITION));
                attribute_modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ItemAccessor.ctft$ATTACK_SPEED_MODIFIER_ID(), "speed", material.sword_attack_speed, EntityAttributeModifier.Operation.ADDITION));
                attribute_modifiers.putAll(super.getAttributeModifiers(stack, slot));
            }
        }
        return attribute_modifiers;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return CtftOverrideHelper.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return CtftOverrideHelper.getItemBarColor(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        CtftOverrideHelper.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return CtftOverrideHelper.getTooltipData(stack);
    }
}