package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.minecraft.entity.attribute.EntityAttributes
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.Events
import net.silkmc.silk.core.event.Server
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.infiniteMcCoroutineTask

object SpeedManager {
    val vanillaSpeed = EntityAttributes.GENERIC_MOVEMENT_SPEED.defaultValue

    const val SURFER_BASE_SPEED = 0.15
    private const val SURFER_MAX_SPEED = 0.4
    private const val SURFER_ACCELERATION = 0.005 // speed increase per second

    @OptIn(ExperimentalSilkApi::class)
    fun init() {
        Events.Server.preStart.listen { evt ->
            val server = evt.server

            // Increase speed for players
            infiniteMcCoroutineTask(period = 20.ticks) {
                server.playerManager.playerList.filter {
                    it.isSubwaySurfers
                }.forEach {
                    // Gradually increase movement speed over time
                    it.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)?.let { attr ->
                        attr.baseValue = (attr.baseValue + SURFER_ACCELERATION).coerceIn(SURFER_BASE_SPEED, SURFER_MAX_SPEED)
                    }
                }
            }
        }
    }
}