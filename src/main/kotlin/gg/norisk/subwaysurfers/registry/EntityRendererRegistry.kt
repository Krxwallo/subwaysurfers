package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.client.renderer.entity.CoinRenderer
import gg.norisk.subwaysurfers.client.renderer.entity.MagnetRenderer
import gg.norisk.subwaysurfers.client.renderer.entity.TrainRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EntityRendererRegistry {
    fun init() {
        EntityRendererRegistry.register(EntityRegistry.COIN, ::CoinRenderer)
        EntityRendererRegistry.register(EntityRegistry.TRAIN, ::TrainRenderer)
        EntityRendererRegistry.register(EntityRegistry.MAGNET, ::MagnetRenderer)
    }
}
