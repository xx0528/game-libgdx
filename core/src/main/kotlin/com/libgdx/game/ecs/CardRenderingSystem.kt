package com.libgdx.game.ecs

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.g2d.Batch
import com.libgdx.game.Const
import ktx.ashley.allOf
import ktx.ashley.get
import com.libgdx.game.game.GameState
import com.libgdx.game.util.SpriteCollection
import kotlin.math.roundToInt

class CardRenderingSystem(
    private val batch: Batch,
    private val sprites: SpriteCollection,
    private val state: GameState,
    var allOpen: Boolean = false
) :
    SortedIteratingSystem(allOf(CardRenderComponent::class).get(), compareBy {
        it[CardRenderComponent.mapper]?.let { component -> state.layout[component.socketIndex].z }
    }) {

    private val startX = ((Const.CONTENT_WIDTH - Const.CELL_WIDTH * state.layout.numberOfColumns) / 2f + 0.5f)
        .roundToInt()
        .toFloat()

    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[CardRenderComponent.mapper]?.let { component ->
            val socket = state.layout[component.socketIndex]
            val x = socket.column * Const.CELL_WIDTH + startX + Const.SPRITE_X
            val y = Const.CONTENT_HEIGHT -
                    Const.VERTICAL_PADDING -
                    socket.row * Const.CELL_HEIGHT -
                    (Const.SPRITE_HEIGHT + Const.SPRITE_Y)
            batch.draw(sprites.card, x, y)
            if (state.isOpen(component.socketIndex)) {
                batch.draw(sprites.faces[component.cardIndex], x + Const.FACE_X, y + Const.FACE_Y)
            } else if (allOpen) {
                batch.draw(sprites.faces[component.cardIndex], x + Const.FACE_X, y + Const.FACE_Y)
                val smallSprite = sprites.smallFaces[component.cardIndex]
                batch.draw(smallSprite, x + Const.SPRITE_WIDTH - smallSprite.width - 2f, y + Const.FACE_Y)
            } else {
                batch.draw(sprites.back, x, y)
            }
        }
    }
}
