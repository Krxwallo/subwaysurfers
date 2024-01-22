package gg.norisk.subwaysurfers.client.player

import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.api.layered.ModifierLayer

interface IAnimatedPlayer {
    fun subwaysurfers_getModAnimation(): ModifierLayer<IAnimation>
}