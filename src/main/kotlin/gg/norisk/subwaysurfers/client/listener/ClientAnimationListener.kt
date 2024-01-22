package gg.norisk.subwaysurfers.client.listener

import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier
import dev.kosmx.playerAnim.core.util.Ease
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry
import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.client.player.IAnimatedPlayer
import gg.norisk.subwaysurfers.network.s2c.NamedKeyframeAnimationPlayer
import gg.norisk.subwaysurfers.network.s2c.playAnimationS2C

object ClientAnimationListener {
    fun init() {
        playAnimationS2C.receiveOnClient { packet, context ->
            val world = context.client.world ?: return@receiveOnClient
            val player = world.getPlayerByUuid(packet.playerUuid) ?: return@receiveOnClient

            val animationContainer = (player as IAnimatedPlayer).subwaysurfers_getModAnimation()

            val animationName = packet.animation

            var animation = PlayerAnimationRegistry.getAnimation(animationName.toId())
                ?: error("No animation found for: ${animationName.toId()}")

            //TODO das muss geiler gemacht werden ist jetzt nur für Darth Vader
            val builder = animation.mutableCopy()
            animation = builder.build()

            animationContainer.replaceAnimationWithFade(
                AbstractFadeModifier.standardFadeIn(3, Ease.INBOUNCE),
                NamedKeyframeAnimationPlayer(animationName, animation)
            )
        }
    }
}