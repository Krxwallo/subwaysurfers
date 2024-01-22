package gg.norisk.subwaysurfers.client.input

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.event.events.KeyEvents
import gg.norisk.subwaysurfers.network.c2s.MovementType
import gg.norisk.subwaysurfers.network.c2s.movementTypePacket

object KeyboardInput {
    fun init() {
        sendClientInput()
    }

    private fun sendClientInput() {
        KeyEvents.keyPressedOnce.listen {
            if (!ClientSettings.isEnabled()) {
                return@listen
            }
            if (it.client.options.leftKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.LEFT)
            } else if (it.client.options.rightKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.RIGHT)
            } else if (it.client.options.jumpKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.JUMP)
            } else if (it.client.options.sneakKey.matchesKey(it.key, it.scanCode)) {
                movementTypePacket.send(MovementType.SLIDE)
            }
        }
    }
}
