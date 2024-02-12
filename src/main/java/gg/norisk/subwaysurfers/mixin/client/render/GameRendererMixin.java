package gg.norisk.subwaysurfers.mixin.client.render;

import gg.norisk.subwaysurfers.client.ClientSettings;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    public void getFov(Camera camera, float f, boolean bl, CallbackInfoReturnable<Double> cir) {
        var settings = ClientSettings.INSTANCE;
        if (settings.isEnabled()) cir.setReturnValue(settings.getSettings().getFov());
    }
}
