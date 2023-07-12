package com.libgdx.game.screens.dialogs

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.libgdx.game.Const
import com.libgdx.game.game.Statistics
import com.libgdx.game.game.layout.Layout
import com.libgdx.game.screens.controls.MyMenuItem
import com.libgdx.game.util.GamePreferences
import com.libgdx.game.util.SkinData

class GameMenu(
    skinData: SkinData,
    preferences: GamePreferences,
    layouts: List<Layout>,
    res: I18NBundle,
    ui: TextureAtlas,
    attached: Actor
) : Window("", skinData.skin, preferences.themeKey) {

    var onThemeChanged: ((useDarkTheme: Boolean) -> Unit)? = null
    var onNewDialogShown: (() -> Unit)? = null
    val newGameButton = MyMenuItem(res.get("newGame"), skinData, preferences.themeKey)
    val exitButton = MyMenuItem(res.get("exit"), skinData, preferences.themeKey)

    private val statsButton =
        MyMenuItem(res.get("statistics"), skinData, preferences.themeKey).apply {
            setAction {
                val statsDialog = StatisticsDialog(
                    skinData,
                    preferences.themeKey,
                    Statistics.getInstance(layouts.first().tag),
                    res,
                    ui
                )
                onNewDialogShown?.invoke()
                statsDialog.show(this@GameMenu.stage)
            }
        }

    private val optionsButton =
        MyMenuItem(res.get("options"), skinData, preferences.themeKey).apply {
            setAction {
                val optionsDialog = OptionsDialog(
                    skinData,
                    preferences.themeKey,
                    preferences,
                    layouts,
                    ui,
                    res
                )
                optionsDialog.onThemeChanged =
                    { useDarkTheme -> onThemeChanged?.invoke(useDarkTheme) }
                onNewDialogShown?.invoke()
                optionsDialog.show(this@GameMenu.stage)
            }
        }

    init {
        val buttonWidth = 80f
        isModal = false
        isVisible = false
        val layout = HorizontalGroup().apply {
            pad(0f)
            defaults()
                .space(0f)
                .align(Align.left)
                .pad(0f)
            add(newGameButton).width(buttonWidth).height(Const.BUTTON_HEIGHT)
            row()
            add(optionsButton).width(buttonWidth).height(Const.BUTTON_HEIGHT)
            row()
            add(statsButton).width(buttonWidth).height(Const.BUTTON_HEIGHT)
            row()
            add(exitButton).width(buttonWidth).height(Const.BUTTON_HEIGHT)
        }
        add(layout)
        pad(4f, 4f, 6f, 4f)
        width = buttonWidth + 8f
        height = 4 * Const.BUTTON_HEIGHT + 10f
        setPosition(
            attached.x + attached.width,
            attached.y,
            Align.topRight
        )
    }
}
