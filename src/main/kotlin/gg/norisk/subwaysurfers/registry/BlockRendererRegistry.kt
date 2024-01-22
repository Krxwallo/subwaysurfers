package gg.norisk.subwaysurfers.registry

import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl
import net.minecraft.client.render.RenderLayer

object BlockRendererRegistry {
    fun init() {
        BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.SUBWAY_RAIL, RenderLayer.getCutout())
    }
}