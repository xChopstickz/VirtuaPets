package com.github.xchopstickz.VirtuaPets.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.xchopstickz.VirtuaPets.VirtuaPets
import com.github.xchopstickz.VirtuaPets.ashley.component.*
import com.github.xchopstickz.VirtuaPets.ashley.system.AnimationSystem
import com.github.xchopstickz.VirtuaPets.ashley.system.RenderSystem
import com.github.xchopstickz.VirtuaPets.assets.TextureAtlasAssets
import kotlinx.coroutines.launch
import ktx.ashley.entity
import ktx.ashley.with
import ktx.async.KtxAsync

class PlayGroundScreen(
    game: VirtuaPets
) : AbstractScreen(game) {
    private val viewport = FitViewport( 16f,9f)
    private val engine = PooledEngine().apply {
        addSystem(AnimationSystem(assetStorage))
        addSystem(RenderSystem(batch,viewport))
    }
    override fun inputProcessor(): InputProcessor {
        return game.inputServiceProvider.InputService
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
                type = AnimationType.PINGII_EGG

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
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            engine.entities[0].animation.type = AnimationType.PINGII_CRACK

        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                engine.entities[0].animation.type = AnimationType.PINGII_JUMP
            }
        }


        if (assetStorage.progress.isFinished) {
            engine.update(delta)
        }
    }
}