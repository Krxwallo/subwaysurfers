package gg.norisk.subwaysurfers.mixin.client;

import gg.norisk.subwaysurfers.client.renderer.ShaderManager;
import net.coderbot.iris.Iris;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Iris.class)
public abstract class IrisMixin {
    @Inject(method = "onEarlyInitialize", remap = false, at = @At(value = "INVOKE", target = "Lnet/coderbot/iris/config/IrisConfig;<init>(Ljava/nio/file/Path;)V"))
    private void injected(CallbackInfo ci) {
        ShaderManager.INSTANCE.initShader();
    }
}
