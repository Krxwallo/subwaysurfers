package gg.norisk.subwaysurfers.mixin.client.render;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/DimensionEffects;useThickFog(II)Z"))
    private boolean injected(DimensionEffects instance, int i, int i2) {
        if (SubwaySurferKt.isEnabled()) {
            return false;
        } else {
            return instance.useThickFog(i, i2);
        }
    }
}
