package com.github.xchopstickz.VirtuaPets.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.Batch
import com.github.xchopstickz.VirtuaPets.VirtuaPets
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class AbstractScreen(
    val game: VirtuaPets,
    val batch: Batch = game.batch,
    val assetStorage: AssetStorage = game.assetStorage
) : KtxScreen {
    abstract fun inputProcessor() : InputProcessor

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = inputProcessor()
    }

    override fun hide() {
        super.hide()
        Gdx.input.inputProcessor = null
    }
}