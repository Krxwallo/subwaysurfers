package gg.norisk.subwaysurfers.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager

class TopBarrierBlock(settings: Settings) : HorizontalFacingBlock(settings) {
    companion object {
        val CODEC: MapCodec<TopBarrierBlock> = createCodec(::TopBarrierBlock)
    }

    override fun getCodec(): MapCodec<out HorizontalFacingBlock> = CODEC

    override fun getPlacementState(itemPlacementContext: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, itemPlacementContext.horizontalPlayerFacing.opposite) as BlockState
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }
}
