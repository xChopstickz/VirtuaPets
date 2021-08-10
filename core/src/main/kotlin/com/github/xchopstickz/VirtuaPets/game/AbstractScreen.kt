package com.github.xchopstickz.VirtuaPets.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class AbstractScreen(
    val game: AbstractGame,
    val batch: Batch = game.batch,
    val stage: Stage = game.stage,
    val assetStorage: AssetStorage = game.assetStorage
) : KtxScreen {
    override fun hide() {
      //  stage.clear
    }
}