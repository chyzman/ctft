package com.chyzman.ctft;

import com.chyzman.ctft.command.CtftItemCommand;
import com.chyzman.ctft.particle.CtftParticleTypes;
import com.chyzman.ctft.registries.CtftRecipeSerializers;
import com.chyzman.ctft.registries.CtftRegistry;
import com.chyzman.ctft.registries.CtftStats;
import com.chyzman.ctft.registries.ServerEventListeners;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupButton;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;

import static com.chyzman.ctft.registries.CtftRegistry.*;
import static com.chyzman.ctft.util.CtftRegistryHelper.id;

public class Ctft implements ModInitializer {
    public static final String MODID = "ctft";

    public static final OwoItemGroup CTFT_GROUP = OwoItemGroup.builder(id("ctft_group"), () -> Icon.of(CTFT_SWORD))
            .initializer(group -> {
                group.addCustomTab(Icon.of(CTFT_SWORD), "ctft_swords", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_SWORD));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_PICKAXE), "ctft_pickaxes", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_PICKAXE));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_AXE), "ctft_axes", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_AXE));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_SHOVEL), "ctft_shovels", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_SHOVEL));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_HOE), "ctft_hoes", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_HOE));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_SHEARS), "ctft_shears", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_SHEARS));
                }, false);
                group.addCustomTab(Icon.of(CtftRegistry.CTFT_BRUSH), "ctft_brushes", (context, entries) -> {
                    GetCtftItemsFor(entries, new ItemStack(CTFT_BRUSH));
                }, false);
                group.hasScrollbar();
                group.addButton(ItemGroupButton.github(group, "https://github.com/chyzman"));
                group.addButton(ItemGroupButton.curseforge(group, "https://www.curseforge.com/members/chyzman5253/projects"));
                group.addButton(ItemGroupButton.modrinth(group, "https://modrinth.com/user/chyzman"));
            })
            .tabStackHeight(5)
            .build();

    @Override
    public void onInitialize() {
        CtftRegistry.Init();
        CtftRecipeSerializers.Init();
        CtftStats.Init();
        ServerEventListeners.init();
        CTFT_GROUP.initialize();
        CtftItemCommand.register();
        CtftParticleTypes.init();
    }

    public static void GetCtftItemsFor(ItemGroup.Entries entries, ItemStack itemStack) {
        Registries.ITEM.forEach(item -> {
            ItemStack stack = itemStack.copy();
            var nbt = stack.getOrCreateNbt();
            nbt.putString("material", Registries.ITEM.getId(item).toString());
            nbt.putString("material_type", "item");
            entries.add(stack);
        });
        Registries.BLOCK.forEach(block -> {
            if (block != Blocks.MOVING_PISTON) {
                ItemStack stack = itemStack.copy();
                var nbt = stack.getOrCreateNbt();
                nbt.putString("material", Registries.BLOCK.getId(block).toString());
                nbt.putString("material_type", "block");
                entries.add(stack);
            }
        });
        Registries.ENTITY_TYPE.forEach(entityType -> {
            ItemStack stack = itemStack.copy();
            var nbt = stack.getOrCreateNbt();
            nbt.putString("material", Registries.ENTITY_TYPE.getId(entityType).toString());
            nbt.putString("material_type", "entity");
            entries.add(stack);
        });
        Registries.PARTICLE_TYPE.forEach(particleType -> {
            if (particleType instanceof DefaultParticleType) {
                ItemStack stack = itemStack.copy();
                var nbt = stack.getOrCreateNbt();
                nbt.putString("material", Registries.PARTICLE_TYPE.getId(particleType).toString());
                nbt.putString("material_type", "particle");
                entries.add(stack);
            }
        });
    }
}