package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.CoinModel
import gg.norisk.subwaysurfers.entity.CoinEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class CoinRenderer(context: EntityRendererFactory.Context?) : GeoEntityRenderer<CoinEntity>(context, CoinModel())
