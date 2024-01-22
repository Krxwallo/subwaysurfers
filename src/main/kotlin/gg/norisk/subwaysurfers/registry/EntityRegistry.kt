package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.entity.CoinEntity
import gg.norisk.subwaysurfers.entity.MagnetEntity
import gg.norisk.subwaysurfers.entity.ModifiedEntityDimensions
import gg.norisk.subwaysurfers.entity.TrainEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object EntityRegistry {
    val TRAIN: EntityType<TrainEntity> = registerMob("train", ::TrainEntity, 1.8f, 2.7f, 5.5f)
    val COIN: EntityType<CoinEntity> = registerMob("coin", ::CoinEntity, 0.3f, 0.3f)
    val MAGNET: EntityType<MagnetEntity> = registerMob("magnet", ::MagnetEntity, 0.3f, 0.3f)

    fun registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(TRAIN, createGenericEntityAttributes())
        FabricDefaultAttributeRegistry.register(COIN, createGenericEntityAttributes())
        FabricDefaultAttributeRegistry.register(MAGNET, createGenericEntityAttributes())
    }

    private fun createGenericEntityAttributes(): DefaultAttributeContainer.Builder {
        return PathAwareEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.80000000298023224)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.1)
    }

    private fun <T : MobEntity> registerMob(
        name: String, entity: EntityType.EntityFactory<T>,
        width: Float, height: Float, length: Float = -1f
    ): EntityType<T> {
        val dimension = EntityDimensions.changing(width, height)
        if (length != -1f) {
            (dimension as ModifiedEntityDimensions).length = length
        }
        return Registry.register(
            Registries.ENTITY_TYPE,
            name.toId(),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, entity).dimensions(dimension).build()
        )
    }
}
