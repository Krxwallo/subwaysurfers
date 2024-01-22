package gg.norisk.subwaysurfers.mixin.client.render;

import gg.norisk.subwaysurfers.client.ClientSettings;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.minecraft.util.math.MathHelper.lerp;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private float cameraY;

    @Shadow
    private float lastCameraY;

    @Unique
    private double currentCameraX;
    @Shadow
    private Entity focusedEntity;

    @ModifyConstant(method = "update", constant = @Constant(doubleValue = 4.0))
    private double subwaySurfersIncreaseCameraDistance(double constant) {
        return (ClientSettings.INSTANCE.isEnabled()) ? ClientSettings.INSTANCE.getSettings().getDesiredCameraDistance() : constant;
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void setPosInjection(Args args, BlockView blockView, Entity entity, boolean bl, boolean bl2, float f) {
        if (ClientSettings.INSTANCE.isEnabled() && ClientSettings.INSTANCE.getStartPos() != null && entity instanceof PlayerEntity player) {
            currentCameraX = lerp(f * 0.05, currentCameraX, player.getX());
            var y = lerp(f, entity.prevY, entity.getY()) + (double) lerp(f, this.lastCameraY, this.cameraY);
            var z = lerp(f, entity.prevZ, entity.getZ());
            args.setAll(currentCameraX, y, z);
        }
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0))
    private void subwaySurfersStaticPerspective(Args args) {
        if (ClientSettings.INSTANCE.isEnabled()) {
            args.set(0, ClientSettings.INSTANCE.getSettings().getYaw());
            args.set(1, ClientSettings.INSTANCE.getSettings().getPitch());
        }
    }
}
