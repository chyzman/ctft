package com.chyzman.ctft.mixin.accessor;

import net.minecraft.util.SignType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SignType.class)
public interface SignTypeInvoker {

    @Invoker("<init>")
    static SignType ctft$invokeNew(String name) {
        throw new IllegalStateException("How did this mixin stub get called conc");
    }

    @Invoker("register")
    static SignType ctft$invokeRegister(SignType type) {
        throw new IllegalStateException("How did this mixin stub get called conc");
    }
}
