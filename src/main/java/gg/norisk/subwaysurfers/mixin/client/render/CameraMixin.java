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
        var settings = ClientSettings.INSTANCE;
        if (settings.isEnabled() && settings.getStartPos() != null && entity instanceof PlayerEntity player) {
            var visualSettings = settings.getSettings();
            // - interpolate x and y coordinates
            // - offset camera up and forward
            // --- X ---
            currentCameraX = lerp(f * visualSettings.getCameraSpeedX(), currentCameraX, player.getX());
            System.out.println("currentCameraX: " + currentCameraX);
            // --- Y ---
            /*var y = lerp(f * visualSettings.getCameraSpeed(), entity.prevY + visualSettings.getCameraOffsetY(), entity.getY() + 1.0)
                    + lerp(f*//* * visualSettings.getCameraSpeed()*//*, this.lastCameraY, this.cameraY);*/
            cameraY = (float) lerp(f * visualSettings.getCameraSpeedY(), lastCameraY, entity.getY() + visualSettings.getCameraOffsetY());
            System.out.println("y: " + cameraY);
            // --- Z ---
            var z = lerp(f, entity.prevZ + visualSettings.getCameraOffsetZ(), entity.getZ() + visualSettings.getCameraOffsetZ());
            System.out.println("z: " + z);
            // ---------
            args.setAll(currentCameraX, (double) cameraY, z);
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
