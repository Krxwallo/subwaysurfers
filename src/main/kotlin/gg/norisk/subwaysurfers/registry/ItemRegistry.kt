package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object ItemRegistry {
    val COIN: Item = registerItem("coin", Item(Item.Settings()))

    fun init() {

    }

    private fun <I : Item> registerItem(name: String, item: I): I {
        return Registry.register(Registries.ITEM, name.toId(), item)
    }
}
