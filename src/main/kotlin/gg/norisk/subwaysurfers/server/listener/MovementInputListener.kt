package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.entity.TrainEntity
import gg.norisk.subwaysurfers.network.c2s.MovementType
import gg.norisk.subwaysurfers.network.c2s.movementTypePacket
import gg.norisk.subwaysurfers.network.s2c.AnimationPacket
import gg.norisk.subwaysurfers.network.s2c.playAnimationS2C
import gg.norisk.subwaysurfers.registry.SoundRegistry
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.subwaysurfers.*
import net.minecraft.network.packet.s2c.play.PositionFlag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.entity.modifyVelocity
import net.silkmc.silk.core.task.mcCoroutineTask
import kotlin.time.Duration.Companion.seconds

object MovementInputListener {

    //Sprunganimation kürzer machen
    //fade hinzufügen
    //dash spekatulärer machen

    /** Punish player for dashing against train or wall */
    private fun ServerPlayerEntity.punishHit() {
        world.playSoundFromEntity(
            null,
            this,
            SoundEvents.ENTITY_PLAYER_HURT,
            SoundCategory.PLAYERS,
            0.4f,
            0.8f
        )
        punishTicks += 20 * 3
        checkGameOver()
    }

    fun init() {

        movementTypePacket.receiveOnServer { packet, context ->
            val player = context.player

            if (packet == MovementType.SLIDE) {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid, "subway_jump"))

                // add downward velocity to player
                player.modifyVelocity(Vec3d(0.0, -1.0, 0.0))

                player.surfer.isSliding = true
                mcCoroutineTask(delay = 3.seconds) {
                    player.surfer.isSliding = false
                }
            } else if (packet == MovementType.JUMP) {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid, "subway_jump"))
                (player.world.playSoundFromEntity(
                    null,
                    player,
                    SoundRegistry.WHOOSH,
                    SoundCategory.PLAYERS,
                    0.4f,
                    0.8f
                ))
                player.modifyVelocity(
                    Vec3d(0.0, player.jumpStrength, 0.0)
                )
            } else if (player.rail == 0 && packet == MovementType.LEFT) {
                // Krxwallo - Also punish player when they try to dash against a wall
                // ist im echten Subway Surfers auch so lg
                player.punishHit()
            } else if (player.rail == 2 && packet == MovementType.RIGHT) {
                player.punishHit()
            } else {
                playAnimationS2C.sendToAll(AnimationPacket(player.uuid, "subway_dash"))

                val centerPos = player.pos
                val newPos = Vec3d(
                    centerPos.x + if (packet == MovementType.LEFT) player.dashStrength else -player.dashStrength,
                    player.y,
                    centerPos.z,
                )

                //Teleportation is better than modifying the velocity right=?
                if (player.punishDash(newPos)) {
                    // player hit train
                    player.punishHit()
                } else {
                    (player.world.playSoundFromEntity(
                        null,
                        player,
                        SoundRegistry.WHOOSH,
                        SoundCategory.PLAYERS,
                        0.4f,
                        0.8f
                    ))

                    player.teleport(
                        player.serverWorld,
                        newPos.x,
                        newPos.y,
                        newPos.z,
                        PositionFlag.VALUES.toSet(),
                        player.yaw,
                        player.pitch
                    )
                    player.rail = player.rail + (if (packet == MovementType.LEFT) -1 else 1)
                }
            }
        }
    }

    /** check for potential loss, depending on current punish ticks */
    private fun ServerPlayerEntity.checkGameOver() {
        if (punishTicks > 70) {
            StartCommand.handleGameStop(this)
        }
    }

    private fun ServerPlayerEntity.punishDash(position: Vec3d): Boolean {
        return world.getOtherEntities(this, Box.from(position)) {
            it is TrainEntity
        }.isNotEmpty()
    }
}
