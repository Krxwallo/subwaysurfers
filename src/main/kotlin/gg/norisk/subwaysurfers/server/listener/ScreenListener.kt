package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import gg.norisk.subwaysurfers.server.command.StartCommand

object ScreenListener {
    fun init() {
        restartPacketC2S.receiveOnServer { packet, context ->
            StartCommand.handleStartGame(context.player)
        }
    }
}