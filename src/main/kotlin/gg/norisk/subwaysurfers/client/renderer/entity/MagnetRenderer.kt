package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.MagnetModel
import gg.norisk.subwaysurfers.entity.MagnetEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class MagnetRenderer(context: EntityRendererFactory.Context?) : GeoEntityRenderer<MagnetEntity>(context, MagnetModel())
