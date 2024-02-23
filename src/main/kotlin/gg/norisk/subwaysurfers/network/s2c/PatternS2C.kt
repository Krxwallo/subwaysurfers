package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class PatternPacket(
    val left: List<String> = emptyList(),
    val middle: List<String> = emptyList(),
    val right: List<String> = emptyList(),
)

val patternPacketS2C = s2cPacket<PatternPacket>("pattern".toId())