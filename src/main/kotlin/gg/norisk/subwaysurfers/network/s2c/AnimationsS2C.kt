package gg.norisk.subwaysurfers.network.s2c

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer
import dev.kosmx.playerAnim.core.data.KeyframeAnimation
import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.common.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket
import java.util.*

@Serializable
data class AnimationPacket(
    @Serializable(with = UUIDSerializer::class)
    val playerUuid: UUID,
    val animation: String
)
class NamedKeyframeAnimationPlayer(
    val name: String,
    animation: KeyframeAnimation,
    t: Int = 0,
    mutable: Boolean = false
) : KeyframeAnimationPlayer(animation, t, mutable)

val playAnimationS2C = s2cPacket<AnimationPacket>("playanimation".toId())
