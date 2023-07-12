package com.libgdx.game.screens.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.libgdx.game.util.SkinData

class MyTextButton(text: String, skinData: SkinData, theme: String, red: Boolean = false) :
    TextButton(text, skinData.skin, if (red) "redButton_$theme" else theme) {

    init {
        pad(skinData.buttonPadTop, 8f, skinData.buttonPadBottom, 8f)
    }

    fun setAction(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }
}
