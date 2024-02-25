package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.extensions.toBlockPos
import gg.norisk.subwaysurfers.extensions.toStack
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfersOrSpectator
import gg.norisk.subwaysurfers.worldgen.PatternGenerator
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.BlockMirror
import net.silkmc.silk.core.event.Event
import net.silkmc.silk.core.text.literal
import java.util.*

object ClientGameStartLifeCycle : ClientTickEvents.EndWorldTick {
    var leftWallPatternGenerator: PatternGenerator? = null
    var railPatternGenerator: PatternGenerator? = null
    var rightWallPatternGenerator: PatternGenerator? = null

    val clientGameStartEvent = Event.onlySync<Unit>()

    fun init() {
        clientGameStartEvent.listen { _ ->
            onStart()
        }
        ClientTickEvents.END_WORLD_TICK.register(this)
    }

    private fun handleWallGeneration(player: ClientPlayerEntity) {
        val leftOffset = 3.0
        val rightOffset = -20.0
        val offset = 5.0
        leftWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(leftOffset + offset, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getLeftPattern().toStack()) }
        )
        railPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(-4.0, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getMiddlePattern().toStack()) }
        )
        rightWallPatternGenerator = PatternGenerator(
            startPos = ClientSettings.startPos!!.add(rightOffset - offset, -1.0, 0.0).toBlockPos(),
            patternStack = Stack<Stack<String>>().apply { add(ClientSettings.getRightPattern().toStack()) },
            mirror = BlockMirror.FRONT_BACK
        )
    }

    private fun onStart() {
        handleWallGeneration(MinecraftClient.getInstance().player!!)
    }

    override fun onEndTick(world: ClientWorld) {
        if (MinecraftClient.getInstance().player?.isSubwaySurfersOrSpectator == true) {
            leftWallPatternGenerator?.tick(MinecraftClient.getInstance().player!!)
            railPatternGenerator?.tick(MinecraftClient.getInstance().player!!)
            rightWallPatternGenerator?.tick(MinecraftClient.getInstance().player!!)
        }
    }
}
