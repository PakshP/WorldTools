package org.waste.of.time.gui

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.waste.of.time.manager.CaptureManager

class EnterTextField(
    font: Font, x: Int, y: Int, width: Int, height: Int, message: Component, val minecraft: Minecraft
) : EditBox(font, x, y, width, height, message) {

    override fun keyPressed(event: KeyEvent): Boolean {
        if (event.key() == InputConstants.KEY_RETURN) {
            if (CaptureManager.capturing) {
                minecraft.gui.setScreen(null)
                CaptureManager.stop()
            } else {
                minecraft.gui.setScreen(null)
                CaptureManager.start(value)
            }
            return true
        }
        return super.keyPressed(event)
    }
}
