package gg.norisk.subwaysurfers.client.model.entity

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.entity.CoinEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class CoinModel : DefaultedEntityGeoModel<CoinEntity>("coin".toId()) {
    // We want this entity to have a translucent render
    override fun getRenderType(animatable: CoinEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}
