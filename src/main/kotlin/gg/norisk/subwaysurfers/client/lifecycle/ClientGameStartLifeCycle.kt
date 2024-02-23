package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.extensions.toBlockPos
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfersOrSpectator
import gg.norisk.subwaysurfers.worldgen.PatternGenerator
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.BlockMirror
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.text.literal

object ClientGameStartLifeCycle: ClientTickEvents.EndWorldTick {
    var leftWallPatternGenerator: PatternGenerator? = null
    var rightWallPatternGenerator: PatternGenerator? = null

    val clientGameStartEvent = Event.onlySyncImmutable<Unit>()

    fun init() {
        clientGameStartEvent.listen { _ ->
            onStart()
        }
        ClientTickEvents.END_WORLD_TICK.register(this)
    }

    private fun handleWallGeneration(player: ClientPlayerEntity) {
        player.sendMessage("Start".literal)
        leftWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(5.0, 0.0, 0.0).toBlockPos()
        ) { ClientSettings.getLeftPattern().toMutableList() }
        rightWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(-15.0, 0.0, 0.0).toBlockPos(),
            mirror = BlockMirror.FRONT_BACK
        ) { ClientSettings.getRightPattern().toMutableList() }
    }

    private fun onStart() {
        handleWallGeneration(MinecraftClient.getInstance().player!!)
    }

    override fun onEndTick(world: ClientWorld) {
        if (MinecraftClient.getInstance().player?.isSubwaySurfersOrSpectator == true) {
            leftWallPatternGenerator?.tick(MinecraftClient.getInstance().player!!)
            rightWallPatternGenerator?.tick(MinecraftClient.getInstance().player!!)
        }
    }
}