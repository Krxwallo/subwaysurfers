package gg.norisk.subwaysurfers.extensions

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun Vec3d.toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())