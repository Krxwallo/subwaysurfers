package gg.norisk.subwaysurfers.client.model.entity

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.entity.TrainEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class TrainModel : DefaultedEntityGeoModel<TrainEntity>("train".toId()) {
    // We want this entity to have a translucent render
    override fun getRenderType(animatable: TrainEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}
