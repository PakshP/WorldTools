package org.waste.of.time.gui

import me.shedaniel.autoconfig.AutoConfigClient
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.waste.of.time.WorldTools.MAX_LEVEL_NAME_LENGTH
import org.waste.of.time.config.WorldToolsConfig
import org.waste.of.time.manager.CaptureManager
import org.waste.of.time.manager.CaptureManager.currentLevelName
import org.waste.of.time.manager.CaptureManager.levelName

object ManagerScreen : Screen(Component.translatable("worldtools.gui.manager.title")) {
    private lateinit var worldNameTextEntryWidget: EditBox
    private lateinit var titleWidget: StringWidget
    private lateinit var downloadButton: Button
    private lateinit var configButton: Button
    private lateinit var cancelButton: Button
    private const val BUTTON_WIDTH = 90

    override fun init() {
        setupTitle()
        setupEntryGrid()
        setupBottomGrid()
    }

    override fun tick() {
        if (CaptureManager.capturing) {
            downloadButton.message = Component.translatable("worldtools.gui.manager.button.stop_download")
            worldNameTextEntryWidget.setHint(Component.literal(currentLevelName))
            worldNameTextEntryWidget.setEditable(false)
        } else {
            downloadButton.message = Component.translatable("worldtools.gui.manager.button.start_download")
            worldNameTextEntryWidget.setEditable(true)
        }
        super.tick()
    }

    private fun setupTitle() {
        titleWidget = StringWidget(Component.translatable("worldtools.gui.manager.title"), font)
        FrameLayout.alignInRectangle(titleWidget, 0, 0, width, height, 0.5f, 0.01f)
        addRenderableWidget(titleWidget)
    }

    private fun setupEntryGrid() {
        val entryGrid = createGrid()
        val helper = entryGrid.createRowHelper(3)

        worldNameTextEntryWidget = EnterTextField(
            font, 0, 0, 250, 20, Component.literal(levelName), minecraft
        ).apply {
            setHint(Component.translatable("worldtools.gui.manager.world_name_placeholder", levelName))
            setMaxLength(MAX_LEVEL_NAME_LENGTH)
        }
        downloadButton = createButton("worldtools.gui.manager.button.start_download") {
            if (CaptureManager.capturing) {
                minecraft.gui.setScreen(null)
                CaptureManager.stop()
            } else {
                minecraft.gui.setScreen(null)
                CaptureManager.start(worldNameTextEntryWidget.value)
            }
        }

        helper.addChild(worldNameTextEntryWidget, 2)
        helper.addChild(downloadButton, 1)

        entryGrid.arrangeElements()
        FrameLayout.alignInRectangle(entryGrid, 0, titleWidget.y, width, height, 0.5f, 0.05f)
        entryGrid.visitWidgets(this::addRenderableWidget)
    }

    private fun setupBottomGrid() {
        val bottomGrid = createGrid()
        val bottomHelper = bottomGrid.createRowHelper(2)
        configButton = createButton("worldtools.gui.manager.button.config") {
            minecraft.gui.setScreen(AutoConfigClient.getConfigScreen(WorldToolsConfig::class.java, this).get())
        }
        cancelButton = createButton("worldtools.gui.manager.button.cancel") {
            minecraft.gui.setScreen(null)
        }

        bottomHelper.addChild(configButton, 1)
        bottomHelper.addChild(cancelButton, 1)

        bottomGrid.arrangeElements()
        FrameLayout.alignInRectangle(bottomGrid, 0, 0, width, height, 0.5f, .95f)
        bottomGrid.visitWidgets(this::addRenderableWidget)
    }

    private fun createGrid() = GridLayout().apply {
        defaultCellSetting().padding(4, 4, 4, 4)
    }

    private fun createButton(textKey: String, onClick: (Button) -> Unit) =
        Button.builder(Component.translatable(textKey), onClick).width(BUTTON_WIDTH).build()
}
