package gg.norisk.subwaysurfers

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.client.hud.InGameHud
import gg.norisk.subwaysurfers.client.input.KeyboardInput
import gg.norisk.subwaysurfers.client.lifecycle.ClientGameStartLifeCycle
import gg.norisk.subwaysurfers.client.listener.ClientAnimationListener
import gg.norisk.subwaysurfers.client.listener.GameOverListener
import gg.norisk.subwaysurfers.client.renderer.ShaderManager
import gg.norisk.subwaysurfers.registry.*
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.server.listener.MovementInputListener
import gg.norisk.subwaysurfers.server.listener.ScreenListener
import gg.norisk.subwaysurfers.server.mechanics.PatternManager
import gg.norisk.subwaysurfers.server.mechanics.SpeedManager
import gg.norisk.subwaysurfers.worldgen.RailWorldManager
import gg.norisk.subwaysurfers.worldgen.StructureManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.silkmc.silk.commands.clientCommand
import org.slf4j.LoggerFactory

object SubwaySurfers : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        EntityRegistry.registerEntityAttributes()
        SoundRegistry.init()
        BlockRegistry.init()
        ItemRegistry.init()
        EntityRendererRegistry.init()
        RailWorldManager.init()
        StartCommand.init()
        MovementInputListener.init()
        SpeedManager.init()
        PatternManager.init()
        NetworkRegistry.init()
    }

    override fun onInitializeClient() {
        ClientSettings.init()
        KeyboardInput.init()
        InGameHud.init()
        BlockRendererRegistry.init()
        ClientAnimationListener.init()
        GameOverListener.init()
        ScreenListener.init()
        ShaderManager.init()
        StructureManager.initClient()
        ClientGameStartLifeCycle.init()
        devCommands()
    }

    fun String.toId() = Identifier("subwaysurfers", this)
    val noriskSkin = "textures/norisk_skin.png".toId()
    val policeSkin = "textures/policeman.png".toId()
    val logger = LoggerFactory.getLogger("subwaysurfers")

    private fun devCommands() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            clientCommand("curvedshader") {
                runs {
                    ShaderManager.loadCurvedShader()
                }
            }
        }
    }
}
