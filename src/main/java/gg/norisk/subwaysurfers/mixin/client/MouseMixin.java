package gg.norisk.subwaysurfers.mixin.client;

import gg.norisk.subwaysurfers.client.ClientSettings;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @ModifyArgs(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void subwaySurferStaticView(Args args) {
        if (ClientSettings.INSTANCE.isEnabled()) {
            args.set(0, 0D);
            args.set(1, 0D);
        }
    }
}
