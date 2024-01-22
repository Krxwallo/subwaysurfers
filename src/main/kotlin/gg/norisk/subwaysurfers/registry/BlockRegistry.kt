package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.block.SubwayRailBlock
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.BlockSoundGroup

object BlockRegistry {
    val SUBWAY_RAIL = registerBlock(
        "subway_rail",
        SubwayRailBlock(AbstractBlock.Settings.create().noCollision().nonOpaque().strength(0.7f).sounds(BlockSoundGroup.METAL))
    )

    private fun <B : Block> registerBlock(name: String, block: B): B {
        return Registry.register(Registries.BLOCK, name.toId(), block)
    }

    fun init() {
    }
}