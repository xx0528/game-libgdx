package com.libgdx.game.screens.dialogs

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.collections.toGdxArray
import com.libgdx.game.Const
import com.libgdx.game.game.layout.Layout
import com.libgdx.game.screens.controls.MyTextButton
import com.libgdx.game.util.GamePreferences
import com.libgdx.game.util.SkinData

class OptionsDialog(
    skinData: SkinData,
    theme: String,
    gamePreferences: GamePreferences,
    layouts: List<Layout>,
    private val ui: TextureAtlas,
    private val res: I18NBundle,
) : Dialog("", skinData.skin, theme) {

    var onThemeChanged: ((useDarkTheme: Boolean) -> Unit)? = null
    var onGameLayoutChanged: ((layout: Layout) -> Unit)? = null

    private val useDarkTheme =
        CheckBox(res.get("darkTheme"), skinData.skin, gamePreferences.themeKey).apply {
            imageCell.padRight(4f)
        }
    private val showAllCards =
        CheckBox(res.get("showAll"), skinData.skin, gamePreferences.themeKey).apply {
            imageCell.padRight(4f)
        }
    private val startWithEmptyDiscard =
        CheckBox(res.get("emptyDiscard"), skinData.skin, gamePreferences.themeKey).apply {
            imageCell.padRight(4f)
        }
    private val newGameLayout = SelectBox<String>(skinData.skin, gamePreferences.themeKey).also {
        it.setAlignment(Align.center)
        it.style.listStyle.selection.apply {
            topHeight = 4f
            bottomHeight = 4f
            leftWidth = 4f
            rightWidth = 4f
        }
    }

    init {
        useDarkTheme.isChecked = gamePreferences.useDarkTheme
        showAllCards.isChecked = gamePreferences.showAllCards
        startWithEmptyDiscard.isChecked = gamePreferences.startWithEmptyDiscard

        val items = layouts.map { res.get(it.tag) }.toGdxArray()
        newGameLayout.items = items

        val selectedIndex = layouts.indexOfFirst { it.tag == gamePreferences.layout }
        newGameLayout.selectedIndex = if (selectedIndex >= 0) selectedIndex else 0

        val saveButton = MyTextButton(res.get("save"), skinData, theme).apply {
            setAction {
                if (gamePreferences.useDarkTheme != useDarkTheme.isChecked) {
                    gamePreferences.useDarkTheme = useDarkTheme.isChecked
                    onThemeChanged?.invoke(gamePreferences.useDarkTheme)
                }

                if (selectedIndex < 0 || selectedIndex != newGameLayout.selectedIndex) {
                    gamePreferences.layout = layouts[newGameLayout.selectedIndex].tag
                    onGameLayoutChanged?.invoke(layouts[newGameLayout.selectedIndex])
                }

                gamePreferences.showAllCards = showAllCards.isChecked
                gamePreferences.startWithEmptyDiscard = startWithEmptyDiscard.isChecked

                gamePreferences.save()
                hide()
            }
        }

        val cancelButton = MyTextButton(res.get("cancel"), skinData, theme).apply {
            setAction { hide() }
        }

        pad(4f, 16f, 12f, 16f)

        contentTable.apply {
            defaults()
                .align(Align.left)
                .space(0f, 0f, 2f, 0f)
                .pad(0f)

            pad(0f)

            add(Image(ui.findRegion("options_$theme"))).space(0f).align(Align.center)
            row()
            add(Label(res.get("layout"), skinData.skin, gamePreferences.themeKey)).height(16f)
            row()
            add(newGameLayout).width(220f).height(Const.BUTTON_HEIGHT - 1f).padBottom(4f)
            row()
            add(showAllCards).pad(2f, 0f, 2f, 0f)
            row()
            add(startWithEmptyDiscard).pad(2f, 0f, 2f, 0f)
            row()
            add(useDarkTheme).align(Align.left).pad(2f, 0f, 0f, 0f)
        }
        buttonTable.apply {
            pad(4f, 0f, 0f, 0f)
            defaults().width(108f).height(Const.BUTTON_HEIGHT).align(Align.center).space(4f).pad(0f)
            add(saveButton)
            add(cancelButton)
        }
    }
}
