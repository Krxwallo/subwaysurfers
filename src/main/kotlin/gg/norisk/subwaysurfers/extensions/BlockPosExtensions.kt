package gg.norisk.subwaysurfers.extensions

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

fun BlockPos.toAxisPos(direction: Direction): BlockPos {
    return BlockPos(
        x * direction.offsetX, y * direction.offsetY, z * direction.offsetZ
    )
}