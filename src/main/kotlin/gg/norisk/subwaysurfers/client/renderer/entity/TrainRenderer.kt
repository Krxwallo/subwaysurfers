package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.TrainModel
import gg.norisk.subwaysurfers.entity.TrainEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class TrainRenderer(context: EntityRendererFactory.Context) : GeoEntityRenderer<TrainEntity>(context, TrainModel())
