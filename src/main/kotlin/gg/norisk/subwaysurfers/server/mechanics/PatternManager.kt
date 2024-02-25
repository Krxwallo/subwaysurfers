package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.extensions.next
import gg.norisk.subwaysurfers.network.s2c.PatternPacket
import gg.norisk.subwaysurfers.network.s2c.patternPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.lastPatternUpdatePos
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import kotlin.math.absoluteValue

object PatternManager : ServerTickEvents.EndWorldTick {
    val NEW_PATTERN_DISTANCE = 20

    fun init() {
        ServerTickEvents.END_WORLD_TICK.register(this)
    }

    var currentPattern: Pattern = Pattern.FIRST

    enum class Pattern(val left: List<String>, val middle: List<String>, val right: List<String>) {
        FIRST(
            listOf(
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
            ), listOf(
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1"
            ), listOf(
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
                "subway_house_1",
            )
        ),
        SECOND(
            listOf(
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
            ), listOf(
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1"
            ), listOf(
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
                "subway_house_2",
            )
        ),
        THIRD(
            listOf(
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
            ), listOf(
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1"
            ), listOf(
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
                "subway_house_3",
            )
        ),
        FOURTH(
            listOf(
                "subway_house_1",
                "subway_house_2",
                "subway_house_3",
                "subway_house_4",
                "subway_house_5",
                "subway_house_6",
                "subway_house_7",
                "subway_house_8",
                "subway_house_9",
                "subway_house_10",
                "subway_house_11",
            ), listOf(
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1",
                "subway_rail_1"
            ), listOf(
                "subway_house_1",
                "subway_house_2",
                "subway_house_3",
                "subway_house_4",
                "subway_house_5",
                "subway_house_6",
                "subway_house_7",
                "subway_house_8",
                "subway_house_9",
                "subway_house_10",
                "subway_house_11",
            )
        )
    }

    override fun onEndTick(world: ServerWorld) {
        for (playerEntity in world.players.filter { it.isSubwaySurfers }) {
            val pos = playerEntity.z.absoluteValue.toInt()
            if (pos.mod(NEW_PATTERN_DISTANCE) == 0 && pos != playerEntity.lastPatternUpdatePos) {
                playerEntity.lastPatternUpdatePos = pos
                logger.info("Sending new Pattern to ${playerEntity.name}")
                patternPacketS2C.send(
                    PatternPacket(
                        currentPattern.left,
                        currentPattern.middle,
                        currentPattern.right
                    ), playerEntity
                )
                currentPattern = currentPattern.next()
            }
        }
    }
}
