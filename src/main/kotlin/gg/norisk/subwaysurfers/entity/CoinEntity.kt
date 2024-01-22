package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.entity.TrainEntity.Companion.handleDiscard
import gg.norisk.subwaysurfers.subwaysurfers.coins
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

class CoinEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity, UUIDMarker {
    override var owner: UUID? = null
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    init {
        this.ignoreCameraFrustum = true
    }

    override fun tick() {
        super.tick()
        if (!world.isClient) {
            handleDiscard(owner)
        }
    }

    override fun pushAwayFrom(entity: Entity?) {
    }

    override fun pushAway(entity: Entity?) {
    }

    override fun onPlayerCollision(player: PlayerEntity) {
        if (!world.isClient) {
            player.coins++
            player.playSound(
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.5f,
                3f
            )
            this.discard()
        }
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

    // Apply player-controlled movement
    override fun travel(pos: Vec3d) {
        super.travel(pos)
    }

    // Get the controlling passenger
    override fun getControllingPassenger(): LivingEntity? {
        return firstPassenger as? LivingEntity?
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean {
        return true
    }

    // Add our generic idle animation controller
    override fun registerControllers(controllers: ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericIdleController(this))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return this.cache
    }

    override fun createChild(level: ServerWorld, partner: PassiveEntity): PassiveEntity? {
        return null
    }
}
