package gg.norisk.subwaysurfers.mixin.entity;

import gg.norisk.subwaysurfers.entity.ModifiedEntityDimensions;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDimensions.class)
public abstract class EntityDimensionsMixin implements ModifiedEntityDimensions {
    @Shadow
    @Final
    public float width;
    @Shadow
    @Final
    public float height;
    @Unique
    private float length = -1;

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public void setLength(float v) {
        this.length = v;
    }

    @Inject(method = "getBoxAt(DDD)Lnet/minecraft/util/math/Box;", at = @At("RETURN"), cancellable = true)
    private void injected(double d, double y1, double f, CallbackInfoReturnable<Box> cir) {
        if (length != -1) {
            float boxLength = this.length / 2.0F;
            float boxWidth = this.width / 2.0F;
            float boxHeight = this.height;
            var x1 = d - (double) boxWidth;
            var x2 = d + (double) boxWidth;
            var y2 = y1 + boxHeight;
            var z1 = f - (double) boxLength;
            var z2 = f + (double) boxLength;
            var box = new Box(x1, y1, z1, x2, y2, z2);
            cir.setReturnValue(box);
        }
    }
}
