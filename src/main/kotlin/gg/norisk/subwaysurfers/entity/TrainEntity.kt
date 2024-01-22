package gg.norisk.subwaysurfers.entity

import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib.animatable.GeoEntity
import software.bernie.geckolib.constant.DefaultAnimations
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*

class TrainEntity(type: EntityType<out AnimalEntity>, level: World) : AnimalEntity(type, level), GeoEntity, UUIDMarker {
    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    override var owner: UUID? = null

    var shouldDrive: Boolean
        get() {
            return this.dataTracker.get(DRIVE)
        }
        set(value) {
            this.dataTracker.set(DRIVE, value)
        }

    init {
        this.ignoreCameraFrustum = true
    }

    companion object {
        private val DRIVE: TrackedData<Boolean> =
            DataTracker.registerData(TrainEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)

        fun LivingEntity.handleDiscard(owner: UUID?) {
            val player = world.getPlayerByUuid(owner ?: return)
            if (player == null) {
                this.discard()
            } else {
                if (player.blockPos.z - 5 > this.blockPos.z) {
                    this.discard()
                }
            }
        }
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(DRIVE, false)
    }

    override fun collidesWith(entity: Entity): Boolean {
        if (entity is TrainEntity) {
            return false
        }
        return super.collidesWith(entity)
    }

    override fun travel(pos: Vec3d) {
        if (this.isAlive && shouldDrive) {
            val x = 0f
            var z = 1f

            movementSpeed = 0.3f

            super.travel(Vec3d(x.toDouble(), pos.y, z.toDouble()))
        }
    }

    override fun onPlayerCollision(playerEntity: PlayerEntity) {
        super.onPlayerCollision(playerEntity)
        if (!world.isClient) {
            if (playerEntity.y <= this.y && playerEntity.z <= this.z && playerEntity.isSubwaySurfers) {
                StartCommand.handleGameStop(playerEntity as ServerPlayerEntity)
            }
        }
    }

    override fun tick() {
        super.tick()
        if (!world.isClient) {
            handleDiscard(owner)
        }
    }

    override fun isLogicalSideForUpdatingMovement(): Boolean {
        return true
    }

    override fun isCollidable(): Boolean {
        return true
    }

    // Turn off step sounds since it's a bike
    override fun playStepSound(pos: BlockPos, block: BlockState) {}

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
