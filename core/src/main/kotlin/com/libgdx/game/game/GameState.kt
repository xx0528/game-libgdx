package com.libgdx.game.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxArray
import ktx.collections.GdxIntArray
import ktx.collections.GdxMap
import ktx.log.logger
import com.libgdx.game.game.layout.Layout
import com.libgdx.game.util.all

class GameState private constructor(
    val layout: Layout,
    val sockets: GdxArray<SocketState>,
    val stack: GdxIntArray,
    val discard: GdxIntArray,
    val minDiscarded: Int,
    val stats: Statistics,
    stalled1: Boolean
) {
    var stalledOnce: Boolean = stalled1
        private set

    val canUndo get() = discard.size > minDiscarded
    val canDeal get() = stack.size > 0
    val won get() = sockets.all { it.isEmpty }
    val stalled: Boolean
        get() {
            val s = !(canDeal ||
                    discard.isEmpty ||
                    ((layout.numberOfSockets - 1) downTo 0).any {
                        isOpen(it) && Card.areNeighbors(discard.peek(), sockets[it].card)
                    })
            stalledOnce = stalledOnce || s
            return s
        }

    val statistics = stats


    /** Takes a card from the table and put it to the discard.
     *  @return true if the action is performed, false otherwise.
     */
    fun take(socketIndex: Int): Boolean {
        require(socketIndex in 0 until layout.numberOfSockets)
        if (isOpen(socketIndex) &&
            (discard.isEmpty || Card.areNeighbors(discard.peek(), sockets[socketIndex].card))
        ) {
            sockets[socketIndex].isEmpty = true
            discard.add(sockets[socketIndex].card)
            statistics.take()
            return true
        }
        return false
    }

    /** Takes a new card from the stack
     * @return true if there was at least one card in the stack, false otherwise.
     */
    fun deal(): Boolean {
        if (!canDeal) return false
        discard.add(stack.pop())
        stats.deal()
        return true
    }

    /** Undoes an action.
     *  @return null if can't undo, socket number if the card came from a socket, -1 if the card came from the stack.
     */
    fun undo(): Int? {
        if (discard.size <= minDiscarded) return null

        val card = discard.pop()
        val socketIndex = sockets.indexOfFirst { it.card == card }

        if (socketIndex < 0) {
            stack.add(card)
        } else {
            sockets[socketIndex].isEmpty = false
        }

        stats.undo(socketIndex < 0)
        return socketIndex
    }

    fun onWin() {
        statistics.win()
    }

    /** Checks if a socket is not blocked. */
    fun isOpen(socketIndex: Int) =
        (!sockets[socketIndex].isEmpty) && layout[socketIndex].blockedBy.all { sockets[it].isEmpty }


    fun save() {
        val preferences = Gdx.app.getPreferences(SAVE_NAME)
        preferences.clear()

        val serializedSockets = sockets.map { it.serialize() }.joinToString(SEPARATOR)
        preferences.putString(SOCKETS, serializedSockets)

        preferences.putString(DISCARD, intArrayToString(discard))
        preferences.putString(STACK, intArrayToString(stack))

        preferences.putString(LAYOUT, layout.tag)
        preferences.putInteger(MIN_DISCARDED, minDiscarded)
        preferences.putBoolean(STALLED_ONCE, stalledOnce)
        statistics.save(preferences)

        preferences.flush()
    }

    fun discardToString() = intArrayToString(discard)

    fun stackToString() = intArrayToString(stack)

    companion object {
        const val LAYOUT = "layout"
        const val DISCARD = "discard"
        const val SOCKETS = "sockets"
        const val STACK = "stack"
        const val MIN_DISCARDED = "minDiscarded"
        const val SEPARATOR = ";"
        const val STALLED_ONCE = "stalledOnce"
        const val SAVE_NAME = "save"

        private val log = logger<GameState>()

        fun new(cards: IntArray, emptyDiscard: Boolean, layout: Layout, stats: Statistics): GameState {
            require(cards.size == 52 && cards.distinct().size == cards.size)

            val sockets = GdxArray<SocketState>(layout.numberOfSockets)
            val stack = GdxIntArray.with()
            val discard = GdxIntArray.with()
            val minDiscarded = if (emptyDiscard) 0 else 1

            for (i in 0 until layout.numberOfSockets) sockets.add(SocketState(cards[i], false))
            for (i in (layout.numberOfSockets - 1 + minDiscarded) downTo layout.numberOfSockets) discard.add(
                cards[i]
            )
            for (i in 51 downTo (layout.numberOfSockets + minDiscarded)) {
                stack.add(cards[i])
            }

            return GameState(
                layout = layout,
                sockets = sockets,
                stack = stack,
                discard = discard,
                minDiscarded = minDiscarded,
                stats = stats,
                stalled1 = false
            )
        }

        fun new(cards: IntArray, emptyDiscard: Boolean, layout: Layout): GameState {
            return new(cards, emptyDiscard, layout, Statistics.getInstance(layout.tag).apply { startNewGame(layout.tag) })
        }

        fun load(layouts: GdxMap<String, Layout>): GameState? {
            val preferences = Gdx.app.getPreferences(SAVE_NAME)

            try {
                if (!preferences.contains(LAYOUT)) return null

                val layout = layouts[preferences.getString(LAYOUT)]

                val serializedSockets = preferences.getString(SOCKETS)
                val socketStates = serializedSockets.split(SEPARATOR).map(SocketState::deserialize)
                val sockets = GdxArray<SocketState>(layout.numberOfSockets)
                socketStates.forEach(sockets::add)

                val discard = stringToIntArray(preferences.getString(DISCARD))
                val stack =stringToIntArray(preferences.getString(STACK))

                val minDiscarded = preferences.getInteger(MIN_DISCARDED)
                val stalled1 = preferences.getBoolean(STALLED_ONCE)
                val stats =
                    Statistics.load(preferences, layout.tag,  ObjectMap.Values(layouts).map { it.tag })

                require((socketStates.count { !it.isEmpty } + stack.size + discard.size) == 52)

                return GameState(
                    layout = layout,
                    sockets = sockets,
                    stack = stack,
                    discard = discard,
                    minDiscarded = minDiscarded,
                    stats = stats,
                    stalled1 = stalled1
                )
            } catch (e: Exception) {
                log.error { "Error loading game state: ${e.message}\n\n${e.stackTrace.joinToString("\n")}" }
            }

            return null
        }

        fun clearSave() {
            val preferences = Gdx.app.getPreferences(SAVE_NAME)
            preferences.clear()
            preferences.flush()
        }

        fun intArrayToString(intArray: GdxIntArray): String =
            intArray.toArray().joinToString(SEPARATOR)

        fun stringToIntArray(string: String): GdxIntArray {
            val elements = string.split(SEPARATOR).map(Integer::parseInt)
            val result = GdxIntArray(elements.size)
            elements.forEach(result::add)
            return result
        }
    }
}


