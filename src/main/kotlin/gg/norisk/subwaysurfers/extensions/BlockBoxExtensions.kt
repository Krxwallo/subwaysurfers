package gg.norisk.subwaysurfers.extensions

import net.minecraft.block.BlockState
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i

fun BlockBox.indexOfPlayerPos(pos: BlockPos): Int {
    var playerIndex = -1
    forEachIndexed { index, blockPos ->
        if (blockPos == pos) {
            playerIndex = index
        }
    }
    return playerIndex
}

fun BlockBox.normalized(): BlockBox {
    return BlockBox.create(Vec3i(0, 0, 0), Vec3i(blockCountX - 1, blockCountY - 1, blockCountZ - 1))
}

fun BlockBox.getBlockStates(world: ServerWorld): List<BlockState> {
    return buildList {
        this@getBlockStates.forEachIndexed { _, blockPos ->
            add(world.getBlockState(blockPos))
        }
    }
}

fun BlockBox.forEachIndexed(action: (index: Int, BlockPos) -> Unit) {
    BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ).forEachIndexed(action::invoke)
}
