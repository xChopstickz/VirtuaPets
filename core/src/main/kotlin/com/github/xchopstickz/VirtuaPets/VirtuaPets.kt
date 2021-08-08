package com.github.xchopstickz.VirtuaPets

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.xchopstickz.VirtuaPets.input.InputServiceProvider
import com.github.xchopstickz.VirtuaPets.screen.AbstractScreen
import com.github.xchopstickz.VirtuaPets.screen.PlayGroundScreen
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.info
import ktx.log.logger

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class VirtuaPets : KtxGame<AbstractScreen>(){
    val batch : Batch by lazy { SpriteBatch() }
    val inputServiceProvider by lazy { InputServiceProvider }
    val assetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }

    override fun create() {
        if ("true" == System.getProperty("devMode", "false")) {
            Gdx.app.logLevel = Application.LOG_DEBUG
        }

        addScreen(PlayGroundScreen(this))
        setScreen<PlayGroundScreen>()
    }

    override fun dispose() {
        super.dispose()
        assetStorage.dispose()

        if (Gdx.app.logLevel == Application.LOG_DEBUG) {
            if (batch is SpriteBatch) {
                val spriteBatch = batch as SpriteBatch
                LOG.debug { "Max Sprites in batch : ${spriteBatch.maxSpritesInBatch}" }
                LOG.debug { "Previous RenderCalls : '${spriteBatch.renderCalls}'" }
            } else {
                LOG.info { "Batch is not of type SpriteBatch. Debug performance logging is skipped!" }
            }
        }
        batch.dispose()
    }

    companion object {
        private val LOG = logger<VirtuaPets>()
        const val UNIT_SCALE = 1/ 16f
    }
}
