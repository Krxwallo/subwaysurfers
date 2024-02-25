package gg.norisk.subwaysurfers.client

import gg.norisk.subwaysurfers.client.lifecycle.ClientGameStartLifeCycle
import gg.norisk.subwaysurfers.extensions.toStack
import gg.norisk.subwaysurfers.network.s2c.VisualClientSettings
import gg.norisk.subwaysurfers.network.s2c.patternPacketS2C
import gg.norisk.subwaysurfers.network.s2c.visualClientSettingsS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d

//TODO 2 Neue Blöcke (Absperrband und So drunter Rutschen)

object ClientSettings : ClientTickEvents.EndTick {
    var settings = VisualClientSettings()
    var startPos: Vec3d? = null
    var ridingTicks = 0

    fun init() {
        patternPacketS2C.receiveOnClient { packet, context ->
            val player = MinecraftClient.getInstance().player ?: return@receiveOnClient
            settings.patternPacket = packet
            ClientGameStartLifeCycle.leftWallPatternGenerator?.patternStack?.add(getLeftPattern().toStack())
            ClientGameStartLifeCycle.railPatternGenerator?.patternStack?.add(getMiddlePattern().toStack())
            ClientGameStartLifeCycle.rightWallPatternGenerator?.patternStack?.add(getRightPattern().toStack())
        }

        visualClientSettingsS2C.receiveOnClient { packet, context ->
            val player = context.client.player ?: return@receiveOnClient
            if (packet.isEnabled) {
                startPos = player.blockPos.toCenterPos()
                player.yaw = 0f
                player.pitch = 0f
                MinecraftClient.getInstance().options.perspective = Perspective.THIRD_PERSON_BACK
            } else {
                MinecraftClient.getInstance().options.perspective = Perspective.FIRST_PERSON
            }
            settings = packet

            if (packet.isEnabled) {
                ClientGameStartLifeCycle.clientGameStartEvent.invoke(Unit)
            }
        }
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    fun isEnabled(): Boolean {
        return settings.isEnabled
    }

    fun disable() {
        settings.isEnabled = false
    }

    fun getLeftPattern(): List<String> {
        return settings.patternPacket.left
    }

    fun getMiddlePattern(): List<String> {
        return settings.patternPacket.middle
    }

    fun getRightPattern(): List<String> {
        return settings.patternPacket.right
    }

    fun onToggle(player: ClientPlayerEntity) {
        if (player.isSubwaySurfers) {
            ridingTicks = 0
        }
    }

    override fun onEndTick(client: MinecraftClient) {
        if (client.player?.isSubwaySurfers == true) {
            ridingTicks++
        }
    }
}
