package com.libgdx.game.util

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.math.max
import kotlin.math.min

/**
 * Pixel perfect fit viewport that only scales up.
 * Taken from [https://gist.github.com/mgsx-dev] _PixelPerfectViewport.java_.
 */
class IntegerScalingViewport(worldWidth: Int, worldHeight: Int, camera: Camera)
    : FitViewport(worldWidth.toFloat(), worldHeight.toFloat(), camera) {

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val minRatio = min(screenWidth / worldWidth, screenHeight / worldHeight)
        val scale = max(minRatio, 1.0f)
        val width = (worldWidth * scale).toInt()
        val height = (worldHeight * scale).toInt()
        setScreenBounds((screenWidth - width) / 2, (screenHeight - height) / 2, width, height)
        apply(centerCamera)
    }
}
