package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.network.s2c.PatternPacket
import gg.norisk.subwaysurfers.network.s2c.patternPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.lastPatternUpdatePos
import gg.norisk.subwaysurfers.subwaysurfers.lastPatternUpdatePosTracker
import gg.norisk.subwaysurfers.worldgen.pattern.leftWallPattern
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import net.silkmc.silk.core.text.literal
import kotlin.math.absoluteValue
import kotlin.random.Random

object PatternManager : ServerTickEvents.EndWorldTick {
    val NEW_PATTERN_DISTANCE = 50

    fun init() {
        ServerTickEvents.END_WORLD_TICK.register(this)
    }

    override fun onEndTick(world: ServerWorld) {
        for (playerEntity in world.players.filter { it.isSubwaySurfers }) {
            val pos = playerEntity.z.absoluteValue.toInt()
            if (pos.mod(NEW_PATTERN_DISTANCE) == 0 && pos != playerEntity.lastPatternUpdatePos) {
                playerEntity.lastPatternUpdatePos = pos
                logger.info("Sending new Pattern to ${playerEntity.name}")
                playerEntity.sendMessage("Sending New Pattern".literal)
                patternPacketS2C.send(
                    if (Random.nextBoolean()) PatternPacket() else PatternPacket(
                        leftWallPattern, emptyList(),
                        leftWallPattern
                    ), playerEntity
                )
            }
        }
    }
}