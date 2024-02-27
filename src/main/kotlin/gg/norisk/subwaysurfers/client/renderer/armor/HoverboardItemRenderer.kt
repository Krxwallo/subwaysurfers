package gg.norisk.subwaysurfers.client.renderer.armor

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.item.HoverboardItem
import software.bernie.geckolib.model.DefaultedItemGeoModel
import software.bernie.geckolib.renderer.GeoArmorRenderer

class HoverboardItemRenderer :
    GeoArmorRenderer<HoverboardItem>(DefaultedItemGeoModel("armor/hoverboard".toId()))
