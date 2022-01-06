package com.chyzman.ctft.classes;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class CustomCompassItem extends Item implements Vanishable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String LODESTONE_POS_KEY = "LodestonePos";
    public static final String LODESTONE_DIMENSION_KEY = "LodestoneDimension";
    public static final String LODESTONE_TRACKED_KEY = "LodestoneTracked";
    public boolean isfoil;
    public String texturetype;

    public CustomCompassItem(boolean isFoil, String textureType, Item.Settings settings) {
        super(settings);
        isfoil = isFoil;
        texturetype = textureType;
    }

    public static boolean hasLodestone(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains(LODESTONE_DIMENSION_KEY) || nbtCompound.contains(LODESTONE_POS_KEY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isfoil || net.minecraft.item.CompassItem.hasLodestone(stack) || super.hasGlint(stack);
    }

    public static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get(LODESTONE_DIMENSION_KEY)).result();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (net.minecraft.item.CompassItem.hasLodestone(stack)) {
            BlockPos blockPos;
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            if (nbtCompound.contains(LODESTONE_TRACKED_KEY) && !nbtCompound.getBoolean(LODESTONE_TRACKED_KEY)) {
                return;
            }
            Optional<RegistryKey<World>> optional = net.minecraft.item.CompassItem.getLodestoneDimension(nbtCompound);
            if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains(LODESTONE_POS_KEY) && (!world.isInBuildLimit(blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound(LODESTONE_POS_KEY))) || !((ServerWorld) world).getPointOfInterestStorage().hasTypeAt(PointOfInterestType.LODESTONE, blockPos))) {
                nbtCompound.remove(LODESTONE_POS_KEY);
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        if (world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
            boolean bl;
            world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            PlayerEntity playerEntity = context.getPlayer();
            ItemStack itemStack = context.getStack();
            boolean bl2 = bl = !playerEntity.getAbilities().creativeMode && itemStack.getCount() == 1;
            if (bl) {
                this.writeNbt(world.getRegistryKey(), blockPos, itemStack.getOrCreateNbt());
            } else {
                ItemStack itemStack2 = new ItemStack(this, 1);
                NbtCompound nbtCompound = itemStack.hasNbt() ? itemStack.getNbt().copy() : new NbtCompound();
                itemStack2.setNbt(nbtCompound);
                if (!playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                this.writeNbt(world.getRegistryKey(), blockPos, nbtCompound);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }
            }
            return ActionResult.success(world.isClient);
        }
        return super.useOnBlock(context);
    }

    private void writeNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
        nbt.put(LODESTONE_POS_KEY, NbtHelper.fromBlockPos(pos));
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(LOGGER::error).ifPresent(nbtElement -> nbt.put(LODESTONE_DIMENSION_KEY, (NbtElement) nbtElement));
        nbt.putBoolean(LODESTONE_TRACKED_KEY, true);
    }

    @Override
    public Text getName() {
        var baseitemname = (Registry.ITEM.getId(this.asItem())).getPath();
        return (new TranslatableText("ctft.item.compass_preffix")
                .append(new TranslatableText(this.texturetype + ".minecraft." + baseitemname
                        .substring(0, baseitemname
                                .lastIndexOf('_'))))
                .append(new TranslatableText("ctft.item.compass_suffix")));
    }

    @Override
    public Text getName(ItemStack stack) {
        var baseitemname = (Registry.ITEM.getId(this.asItem())).getPath();
        if (CompassItem.hasLodestone(stack)) {
            return (new TranslatableText("ctft.item.lodestone_compass_preffix")
                    .append(new TranslatableText(this.texturetype + ".minecraft." + baseitemname
                            .substring(0, baseitemname
                                    .lastIndexOf('_'))))
                    .append(new TranslatableText("ctft.item.lodestone_compass_suffix")));
        } else return this.getName();
    }
}
