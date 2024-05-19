package com.chyzman.ctft.item;

import com.chyzman.ctft.classes.CtftMaterial;
import com.chyzman.ctft.mixin.ItemAccessor;
import com.chyzman.ctft.util.CtftOverrideHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.chyzman.ctft.registries.CtftStats.MATERIALS;

public class CtftShearsItem extends ShearsItem implements CtftItem {
    public CtftShearsItem(Settings settings) {
        super(settings.maxCount(1).maxDamage(1));
    }

    @Override
    public Text getName(ItemStack stack) {
        return CtftOverrideHelper.getName(stack, "item.ctft.shears", stack1 -> super.getName(stack));
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
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.LEAVES)) {
            return 15.0F;
        } else if (state.isIn(BlockTags.WOOL)) {
            return 5.0F;
        } else {
            return !state.isOf(Blocks.VINE) && !state.isOf(Blocks.GLOW_LICHEN) ? CtftOverrideHelper.getMiningSpeedMultiplier(stack, state) : 2.0F;
        }
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