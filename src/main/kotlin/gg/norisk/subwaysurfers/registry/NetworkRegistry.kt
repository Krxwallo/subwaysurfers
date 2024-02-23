package gg.norisk.subwaysurfers.registry

import net.minecraft.entity.data.TrackedDataHandler.ImmutableHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.network.PacketByteBuf

object NetworkRegistry {
    val STRING_LIST = object : ImmutableHandler<List<String>> {
        override fun write(packetByteBuf: PacketByteBuf, list: List<String>) {
            packetByteBuf.writeCollection(list, PacketByteBuf::writeString)
        }

        override fun read(packetByteBuf: PacketByteBuf): List<String> {
            return packetByteBuf.readList(PacketByteBuf::readString)
        }
    }

    fun init() {
        TrackedDataHandlerRegistry.register(STRING_LIST)
    }
}