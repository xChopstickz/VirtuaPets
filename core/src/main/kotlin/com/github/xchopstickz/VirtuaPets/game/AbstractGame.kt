package com.github.xchopstickz.VirtuaPets.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.info
import ktx.log.logger

abstract class AbstractGame : KtxGame<AbstractScreen>() {
    val batch: Batch by lazy { SpriteBatch() }
    val assetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    abstract val uiViewport: Viewport
    val stage: Stage by lazy { Stage(uiViewport,batch) }
    private var isPaused = false

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        uiViewport.update(width,height,true)
    }

    override fun pause() {
        isPaused = true
        //audioService.pause()
        super.pause()
    }

    override fun resume() {
        isPaused = false
        //audioService.resume()
        super.resume()
    }

    override fun render() {
        if (isPaused) {
            return
        }

        super.render()
        TMP_BATCH_COLOR.set(batch.color)
        with(stage) {
            act(Gdx.graphics.deltaTime)
            uiViewport.apply()
            draw()
        }
        batch.color.set(TMP_BATCH_COLOR)

        //audioService.update()
    }

    override fun dispose() {
        super.dispose()

        if (Gdx.app.logLevel == Application.LOG_DEBUG) {
            if (batch is SpriteBatch) {
                val spriteBatch = batch as SpriteBatch
                LOG.debug { "Max sprites in batch: '${spriteBatch.maxSpritesInBatch}'" }
                LOG.debug { "Previous renderCalls: '${spriteBatch.renderCalls}'" }
            } else {
                LOG.info { "Batch is not of type SpriteBatch. Debug performance logging is skipped!" }
            }
        }
        batch.dispose()
        stage.dispose()

        LOG.debug { assetStorage.takeSnapshot().prettyPrint() }
        assetStorage.dispose()
    }

    companion object {
        val LOG = logger<AbstractGame>()
        private val TMP_BATCH_COLOR = Color()
    }
}
