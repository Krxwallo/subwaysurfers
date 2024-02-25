package gg.norisk.subwaysurfers.worldgen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos
import java.util.*

class PatternGenerator(
    val startPos: BlockPos,
    var patternStack: Stack<Stack<String>>,
    var mirror: BlockMirror = BlockMirror.NONE
) {
    var nextZ = startPos.z
    var currentPatternStack: Stack<String> = patternStack.pop()
    var nextStructure: StructureTemplate? = StructureManager.readOrLoadTemplate(currentPatternStack.pop())

    //TODO erstmal aktuelles Pattern ablaufen lassen bevor wir neu handlen.

    private fun handleNextStructure(): StructureTemplate? {
        if (currentPatternStack.isNotEmpty()) {
            return StructureManager.readOrLoadTemplate(currentPatternStack.pop())
        } else {
            if (patternStack.isNotEmpty()) {
                currentPatternStack = patternStack.pop()
                return handleNextStructure()
            }
            return null
        }
    }

    fun tick(player: ClientPlayerEntity) {
        if (nextStructure == null) {
            //TODO maybe hier z resetten?
            nextStructure = handleNextStructure()
            return
        }

        if (nextZ < player.blockPos.z + MinecraftClient.getInstance().options.viewDistance.value * 6) {

            val xOffset = if (mirror == BlockMirror.FRONT_BACK) {
                nextStructure!!.size.x + 1
            } else {
                0
            }

            StructureManager.placeStructure(
                player,
                BlockPos(startPos.x + xOffset, startPos.y, nextZ),
                nextStructure!!,
                StructurePlacementData().setMirror(mirror)
            )

            nextStructure = handleNextStructure()

            if (nextStructure != null) {
                nextZ += (nextStructure?.size?.z ?: 0)
            }
        }
    }
}
