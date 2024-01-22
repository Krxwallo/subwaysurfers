package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.c2sPacket

@Serializable
enum class MovementType {
    LEFT, RIGHT, JUMP, SLIDE
}

val movementTypePacket = c2sPacket<MovementType>("movement_type".toId())
