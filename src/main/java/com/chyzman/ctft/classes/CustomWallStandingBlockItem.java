package com.chyzman.ctft.classes;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class CustomWallStandingBlockItem extends WallStandingBlockItem {
    public boolean isfoil;
    public String texturetype;

    public CustomWallStandingBlockItem(Block standingBlock, Block wallBlock, boolean isFoil, String textureType, Settings settings) {
        super(standingBlock, wallBlock, settings);
        isfoil = isFoil;
        texturetype = textureType;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isfoil || super.hasGlint(stack);
    }

    @Override
    public Text getName() {
        var baseitemname = (Registry.ITEM.getId(this.asItem())).getPath();
        return (Text.translatable("ctft.item.torch_prefix")
                .append(Text.translatable(this.texturetype + ".minecraft." + baseitemname
                        .substring(0, baseitemname
                                .lastIndexOf('_'))))
                .append(Text.translatable("ctft.item.torch_suffix")));
    }

    @Override
    public Text getName(ItemStack stack) {
        return this.getName();
    }
}
