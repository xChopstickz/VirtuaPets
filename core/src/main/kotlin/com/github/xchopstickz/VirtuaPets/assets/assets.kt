package com.github.xchopstickz.VirtuaPets.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum  class TextureAtlasAssets(
    val filePath: String,
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor(filePath, TextureAtlas::class.java)
) {
    PINGII("graphics/pingii.atlas")
}