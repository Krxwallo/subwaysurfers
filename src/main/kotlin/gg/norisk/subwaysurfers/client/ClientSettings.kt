package gg.norisk.subwaysurfers.client

import gg.norisk.subwaysurfers.network.s2c.VisualClientSettings
import gg.norisk.subwaysurfers.network.s2c.visualClientSettingsS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d

object ClientSettings : ClientTickEvents.EndTick {
    var settings = VisualClientSettings()
    var startPos: Vec3d? = null
    var ridingTicks = 0

    fun init() {
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
        }
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    fun isEnabled(): Boolean {
        return settings.isEnabled
    }

    fun disable() {
        settings.isEnabled = false
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
