package com.libgdx.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.libgdx.game.BundleAssets
import com.libgdx.game.TextureAssets
import com.libgdx.game.TextureAtlasAssets
import ktx.app.KtxScreen
import com.libgdx.game.*
import com.libgdx.game.game.GameState
import com.libgdx.game.game.layout.Layout
import com.libgdx.game.screens.controls.MyTextButton
import com.libgdx.game.screens.dialogs.OptionsDialog
import com.libgdx.game.util.GamePreferences
import com.libgdx.game.util.SkinData

class StartScreen(
    private val game: Game,
    private val assets: AssetManager,
    private val viewport: Viewport,
    private val batch: Batch,
    private val skinData: SkinData,
    private val preferences: GamePreferences,
    private val layouts: List<Layout>
) :
    KtxScreen {

    private val stage = Stage(viewport)

    override fun show() {
        initUi()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    private fun initUi() {
        stage.actors.add(
            Image(if (preferences.useDarkTheme) assets[TextureAssets.DarkTitle] else assets[TextureAssets.LightTitle]).apply {
                setSize(300f, 168f)
                setPosition(0f, 0f)
            },

            Table().apply {
                width = 300f
                height = 168f
                setPosition(0f, 0f)
                defaults()
                    .space(4f)
                    .pad(0f)
                    .width(110f)
                    .height(Const.BUTTON_HEIGHT)
                    .align(Align.center)

                add(
                    MyTextButton(
                        assets[BundleAssets.Bundle].get("start"),
                        skinData,
                        preferences.themeKey
                    ).apply {
                        setAction {
                            game.addScreen(
                                GameScreen(
                                    game,
                                    assets,
                                    viewport,
                                    batch,
                                    preferences,
                                    skinData,
                                    layouts
                                )
                            )
                            game.setScreen<GameScreen>()
                            game.removeScreen<StartScreen>()
                            this@StartScreen.dispose()
                        }
                    }
                )
                row()
                add(
                    MyTextButton(
                        assets[BundleAssets.Bundle].get("options"),
                        skinData,
                        preferences.themeKey
                    ).apply {
                        setAction {
                            val optionsDialog = OptionsDialog(
                                skinData,
                                preferences.themeKey,
                                preferences,
                                layouts,
                                assets[TextureAtlasAssets.Ui],
                                assets[BundleAssets.Bundle]
                            )
                            optionsDialog.onGameLayoutChanged = { GameState.clearSave() }
                            optionsDialog.onThemeChanged = { initUi() }
                            optionsDialog.show(this@StartScreen.stage)
                        }
                    }
                )
                row()
                add(
                    MyTextButton(
                        assets[BundleAssets.Bundle].get("exit"),
                        skinData,
                        preferences.themeKey,
                    ).apply {
                        setAction { Gdx.app.exit() }
                    })
            }
        )
        Gdx.input.inputProcessor = stage
    }
}
