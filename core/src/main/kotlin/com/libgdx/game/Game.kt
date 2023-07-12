package com.libgdx.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.inject.Context
import ktx.inject.register
import com.libgdx.game.game.layout.*
import com.libgdx.game.screens.LoadingScreen
import com.libgdx.game.util.GamePreferences
import com.libgdx.game.util.IntegerScalingViewport

open class Game : KtxGame<KtxScreen>() {
    val context = Context()

    override fun create() {

        context.register {
            bindSingleton(AssetManager())
            bindSingleton(GamePreferences().load())
            bindSingleton<Viewport>(
                IntegerScalingViewport(
                    Const.CONTENT_WIDTH.toInt(),
                    Const.CONTENT_HEIGHT.toInt(),
                    OrthographicCamera()
                )
            )
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton(
                Layouts(
                    listOf(
                        BasicLayout(),
                        Inverted2ndLayout(),
                        DiamondsLayout()
                    )
                )
            )
        }

        addScreen(
            LoadingScreen(
                this,
                context.inject(),
                context.inject(),
                context.inject(),
                context.inject(),
                context.inject<Layouts>().list
            )
        )
        setScreen<LoadingScreen>()
        super.create()
    }

    override fun dispose() {
        context.dispose()
        super.dispose()
    }
}
