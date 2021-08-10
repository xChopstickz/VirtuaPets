package com.github.xchopstickz.VirtuaPets

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.xchopstickz.VirtuaPets.game.AbstractGame
import com.github.xchopstickz.VirtuaPets.input.InputServiceProvider
import com.github.xchopstickz.VirtuaPets.screen.PlayGroundScreen

class VirtuaPets : AbstractGame() {
    override val uiViewport: Viewport = FitViewport(320f,180f)
    val inputServiceProvider by lazy { InputServiceProvider }

    override fun create() {
        if ("true" == System.getProperty("devMode", "false")) {
            Gdx.app.logLevel = Application.LOG_DEBUG
        }

        addScreen(PlayGroundScreen(this))
        setScreen<PlayGroundScreen>()
    }

    companion object {
        const val UNIT_SCALE = 1/ 16f
    }
}
