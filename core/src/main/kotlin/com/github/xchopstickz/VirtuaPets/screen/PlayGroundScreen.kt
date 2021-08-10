package com.github.xchopstickz.VirtuaPets.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.xchopstickz.VirtuaPets.VirtuaPets
import com.github.xchopstickz.VirtuaPets.ashley.component.*
import com.github.xchopstickz.VirtuaPets.ashley.system.AnimationSystem
import com.github.xchopstickz.VirtuaPets.ashley.system.RenderSystem
import com.github.xchopstickz.VirtuaPets.assets.TextureAtlasAssets
import com.github.xchopstickz.VirtuaPets.game.AbstractScreen
import kotlinx.coroutines.launch
import ktx.ashley.entity
import ktx.ashley.with
import ktx.async.KtxAsync

class PlayGroundScreen(
    game: VirtuaPets
) : AbstractScreen(game) {
    private val viewport = FitViewport( 16f,9f)
    private val engine = PooledEngine().apply {
        addSystem(AnimationSystem(assetStorage,VirtuaPets.UNIT_SCALE))
        addSystem(RenderSystem(batch,viewport))
    }


    override fun resize(width: Int, height: Int) {
        viewport.update(width,height,true)
    }


    override fun show() {
        super.show()
        println("show")
        KtxAsync.launch {
            assetStorage.apply {
                load(TextureAtlasAssets.PINGII.descriptor)
            }
        }

        engine.entity{
            with<TransformComponent>{
                size.set(4f,4f)
                position.set(6f,0f,0f)
            }
            with<AnimationComponent>{
                this.atlasFilePath = TextureAtlasAssets.PINGII.descriptor.fileName
                this.regionKey = "pingii-egg"
                this.animationSpeed = 0.8f


            }
            with<RenderComponent>()
        }
    }

    override fun hide() {
        super.hide()
        KtxAsync.launch {
            assetStorage.apply {
                unload(TextureAtlasAssets.PINGII.descriptor)
            }
        }
        engine.removeAllEntities()
    }

    override fun render(delta: Float) {



        if (assetStorage.progress.isFinished) {
            engine.update(delta)
        }
    }
}