package com.libgdx.game.screens.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.libgdx.game.util.SkinData

class MyMenuItem(text: String, skinData: SkinData, theme: String) :
    TextButton(text, skinData.skin, "menuItem_$theme") {

    init {
        pad(skinData.buttonPadTop, 4f, skinData.buttonPadBottom - 1f, 4f)
        label.setAlignment(Align.left)
    }

    fun setAction(action: () -> Unit) {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                action.invoke()
            }
        })
    }
}
