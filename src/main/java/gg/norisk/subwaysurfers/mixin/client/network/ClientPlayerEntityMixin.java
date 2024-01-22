package gg.norisk.subwaysurfers.mixin.client.network;

import com.mojang.authlib.GameProfile;
import gg.norisk.subwaysurfers.client.ClientSettings;
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurferKt;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld clientWorld, GameProfile gameProfile) {
        super(clientWorld, gameProfile);
    }

    @Inject(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V"))
    private void onTrackedDataSetMixin(TrackedData<?> trackedData, CallbackInfo ci) {
        if (SubwaySurferKt.getSubwaySurfersTracker().equals(trackedData)) {
            ClientSettings.INSTANCE.onToggle((ClientPlayerEntity) (Object) this);
        }
    }
}
