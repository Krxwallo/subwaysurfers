package gg.norisk.subwaysurfers.mixin.entity.player;

import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import kotlin.collections.EmptyList;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements SubwaySurfer {
    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    public abstract PlayerAbilities getAbilities();

    @Shadow
    public abstract float getMovementSpeed();

    @Shadow
    public abstract void sendMessage(Text text, boolean bl);

    private static final TrackedData<Boolean> SLIDING = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SLIDING.equals(data)) {
            calculateDimensions();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickInjection(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            SubwaySurferKt.handleMagnet((PlayerEntity) (Object) this);
            SubwaySurferKt.handlePunishTicks((PlayerEntity) (Object) this);
        }
    }


    @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        if (SubwaySurferKt.isSubwaySurfers((PlayerEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void slidingHitboxInjection(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (isSliding()) cir.setReturnValue(EntityDimensions.fixed(0.2f, 0.2f));
    }

    @Inject(method = "getOffGroundSpeed", at = @At("HEAD"), cancellable = true)
    private void getOffGroundSpeedInjection(CallbackInfoReturnable<Float> cir) {
        if (SubwaySurferKt.isSubwaySurfers((PlayerEntity) (Object) this)) {
            cir.setReturnValue(this.getMovementSpeed());
        }
    }

    @ModifyArgs(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;", ordinal = 1))
    private void tickMovementInjection(Args args) {
        if (SubwaySurferKt.isSubwaySurfers((PlayerEntity) (Object) this)) {
            args.setAll(0.3, 0.0, 0.3);
        }
    }

    //TODO das anders coden lol
    @Override
    public boolean isOnGround() {
        if (SubwaySurferKt.isSubwaySurfers((PlayerEntity) (Object) this)) {
            return true;
        } else {
            return super.isOnGround();
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerInjection(CallbackInfo ci) {
        this.dataTracker.startTracking(SLIDING, false);
        this.dataTracker.startTracking(SubwaySurferKt.getGravityTracker(), 0.3f);
        this.dataTracker.startTracking(SubwaySurferKt.getDashStrengthTracker(), 2.0f);
        this.dataTracker.startTracking(SubwaySurferKt.getMultiplierTracker(), 1);
        this.dataTracker.startTracking(SubwaySurferKt.getJumpStrengthTracker(), 1.8f);
        this.dataTracker.startTracking(SubwaySurferKt.getRailDataTracker(), 1);
        this.dataTracker.startTracking(SubwaySurferKt.getLastPatternUpdatePosTracker(), 0);
        this.dataTracker.startTracking(SubwaySurferKt.getPunishTicksTracker(), 0);
        this.dataTracker.startTracking(SubwaySurferKt.getCoinDataTracker(), 0);
        this.dataTracker.startTracking(SubwaySurferKt.getSubwaySurfersTracker(), false);
        this.dataTracker.startTracking(SubwaySurferKt.getMagnetTracker(), false);
        this.dataTracker.startTracking(SubwaySurferKt.getLeftPatternTracker(), Lists.newArrayList());
    }

    @Override
    public void setSliding(boolean b) {
        this.dataTracker.set(SLIDING, b);
    }

    @Override
    public boolean isSliding() {
        return this.dataTracker.get(SLIDING);
    }
}
