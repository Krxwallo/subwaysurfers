package gg.norisk.subwaysurfers.client.hud

import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.VerticalAlignment
import io.wispforest.owo.ui.util.UISounds
import net.minecraft.text.Text
import net.silkmc.silk.core.text.literalText

class GameOverScreen : BaseOwoScreen<FlowLayout>() {
    val gameOverLabel: LabelComponent
    val restartButton: ButtonComponent
    val closeButton: ButtonComponent
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow)
    }

    override fun build(rootComponent: FlowLayout) {
        rootComponent.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        rootComponent.gap(5)
        rootComponent.child(gameOverLabel)
        rootComponent.child(restartButton)
        rootComponent.child(closeButton)
    }

    //TODO
    //auf höchsten block teleportieren

    override fun shouldPause(): Boolean {
        return false
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    init {
        gameOverLabel = Components.label(literalText {
            text("GAME OVER!!!")
            bold = true
        }).shadow(true)
        restartButton = Components.button(Text.of("RESTART")) {
            UISounds.playInteractionSound()
            restartPacketC2S.send(Unit)
            this.close()
        }
        closeButton = Components.button(Text.of("CLOSE")) {
            UISounds.playInteractionSound()
            this.close()
        }
    }
}
