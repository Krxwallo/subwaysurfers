package gg.norisk.subwaysurfers.event.events

import net.minecraft.client.MinecraftClient
import net.silkmc.silk.core.annotations.ExperimentalSilkApi
import net.silkmc.silk.core.event.Event

object KeyEvents {
    open class KeyEvent(val key: Int, val scanCode: Int, val client: MinecraftClient)

    @OptIn(ExperimentalSilkApi::class)
    val keyPressedOnce = Event.onlySync<KeyEvent>()
}
