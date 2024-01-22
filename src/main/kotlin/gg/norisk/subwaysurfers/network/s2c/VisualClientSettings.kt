package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class VisualClientSettings(
    var isEnabled: Boolean = false,
    var desiredCameraDistance: Double = 6.0,
    var yaw: Float = 0f,
    var pitch: Float = 20f
)

val visualClientSettingsS2C = s2cPacket<VisualClientSettings>("visualclientsettings".toId())
