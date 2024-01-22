package gg.norisk.subwaysurfers.mixin.client.option;

import gg.norisk.subwaysurfers.client.ClientSettings;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Inject(method = "setPerspective", at = @At("HEAD"), cancellable = true)
    private void subwaySurfersStaticPerspective(Perspective perspective, CallbackInfo ci) {
        if (ClientSettings.INSTANCE.isEnabled()) ci.cancel();
    }
}
