package com.chyzman.ctft.client;

import com.chyzman.ctft.particle.CtftParticleTypes;
import com.chyzman.ctft.particle.ItemParticleEffect;
import com.chyzman.ctft.registries.CtftRegistry;
import com.chyzman.ctft.renderer.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;

import static com.chyzman.ctft.util.CtftRegistryHelper.id;

public class CtftClient implements ClientModInitializer {
    public static final Tessellator CTFTESSELLATOR = new Tessellator();
    public static int recursions = 0;
    @Override
    public void onInitializeClient() {
        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_SWORD, new CtftSwordItemRenderer(id("item/base/sword_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/sword_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_PICKAXE, new CtftPickaxeItemRenderer(id("item/base/pickaxe_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/pickaxe_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_AXE, new CtftAxeItemRenderer(id("item/base/axe_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/axe_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_SHOVEL, new CtftShovelItemRenderer(id("item/base/shovel_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/shovel_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_HOE, new CtftHoeItemRenderer(id("item/base/hoe_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/hoe_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_SHEARS, new CtftShearsItemRenderer(id("item/base/shears_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/shears_base")));

        BuiltinItemRendererRegistry.INSTANCE.register(CtftRegistry.CTFT_BRUSH, new CtftBrushItemRenderer(id("item/base/brush_base")));
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(id("item/base/brush_base")));
        ModelPredicateProviderRegistry.register(CtftRegistry.CTFT_BRUSH, new Identifier("brushing"), (stack, world, entity, seed) -> entity != null && entity.getActiveItem() == stack ? (float)(entity.getItemUseTimeLeft() % 10) / 10.0F : 0.0F);

        ParticleFactoryRegistry.getInstance().register(CtftParticleTypes.ITEM, ItemParticleEffect.ParticleFactory::new);

    }
}