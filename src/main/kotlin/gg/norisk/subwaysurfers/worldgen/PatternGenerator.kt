package gg.norisk.subwaysurfers.worldgen

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.BlockPos

class PatternGenerator(
    val startPos: BlockPos,
    var mirror: BlockMirror = BlockMirror.NONE,
    val patternProvider: () -> MutableList<String>
) {
    var nextZ = startPos.z
    var currentPattern = patternProvider.invoke()
    var nextStructure: StructureTemplate? = StructureManager.readOrLoadTemplate(currentPattern.removeFirstOrNull())

    fun handleNewPattern(player: ClientPlayerEntity) {
        nextZ = player.z.toInt()
    }

    //TODO erstmal aktuelles Pattern ablaufen lassen bevor wir neu handlen.

    private fun handleNextStructure(): StructureTemplate? {
        return StructureManager.readOrLoadTemplate(
            if (currentPattern.isNotEmpty()) {
                currentPattern.removeFirst()
            } else {
                currentPattern = patternProvider.invoke()
                currentPattern.removeFirstOrNull()
            }
        )
    }

    fun tick(player: ClientPlayerEntity) {
        if (nextStructure == null) {
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
                nextZ += (nextStructure?.size?.z ?: 0) + 2
            }
        }
    }
}