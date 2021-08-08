package com.github.xchopstickz.VirtuaPets.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.xchopstickz.VirtuaPets.VirtuaPets

/** Launches the desktop (LWJGL3) application.  */

fun main() {
    Lwjgl3Application(VirtuaPets(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("VirtuaPets")
        setWindowedMode(1200, 675)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}