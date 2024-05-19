package com.chyzman.ctft.item;

import com.chyzman.ctft.util.CtftOverrideHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CtftBrushItem extends BrushItem implements CtftItem {
    public CtftBrushItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Text getName(ItemStack stack) {
        return CtftOverrideHelper.getName(stack, "item.ctft.brush", stack1 -> super.getName(stack));
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