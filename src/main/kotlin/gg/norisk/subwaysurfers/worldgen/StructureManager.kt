package gg.norisk.subwaysurfers.worldgen

import com.google.common.cache.CacheBuilder
import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.client.structure.ClientStructureTemplate
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.datafixer.Schemas
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtIo
import net.minecraft.registry.Registries
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.Vec3i
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.commands.player
import java.io.IOException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object StructureManager {
    private val structureTemplates =
        CacheBuilder.newBuilder().expireAfterAccess(5.minutes.toJavaDuration()).build<String, StructureTemplate>()

    fun initClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            clientCommand("clientstructure") {
                argument<String>("name") { templateName ->
                    runs {
                        placeStructure(this.source.player, templateName())
                    }
                }
            }
        }
    }

    private fun placeStructure(player: ClientPlayerEntity, name: String) {
        val world = player.world as ClientWorld
        val structurePlacementData = StructurePlacementData()
            //.setMirror(BlockMirror.values().random())
            //.setRotation(BlockRotation.values().random())
            .setIgnoreEntities(
                true
            )

        val template = readOrLoadTemplate(name) ?: return
        (template as ClientStructureTemplate).placeClient(
            world,
            player.blockPos,
            player.blockPos.add(Vec3i(0, 1, 0)),
            structurePlacementData,
            world.random,
            2
        )
    }

    @Throws(IOException::class)
    private fun readOrLoadTemplate(name: String): StructureTemplate? {
        val template = structureTemplates.getIfPresent(name)

        if (template != null) {
            return template
        }

        var nbtCompound: NbtCompound? = null

        runCatching {
            javaClass.getResourceAsStream("/structures/$name.nbt")
        }.onSuccess {
            nbtCompound = NbtIo.readCompressed(it)
        }.onFailure {
            logger.error("Error loading Template $name")
            it.printStackTrace()
        }

        return nbtCompound?.let { this.createTemplate(it) }
    }

    private fun createTemplate(nbtCompound: NbtCompound): StructureTemplate {
        val structureTemplate = StructureTemplate()
        val i = NbtHelper.getDataVersion(nbtCompound, 500)
        structureTemplate.readNbt(
            Registries.BLOCK.readOnlyWrapper,
            DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbtCompound, i)
        )
        return structureTemplate
    }
}