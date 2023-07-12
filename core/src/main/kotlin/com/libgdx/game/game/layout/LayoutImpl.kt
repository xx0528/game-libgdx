package com.libgdx.game.game.layout

import ktx.collections.GdxArray
import ktx.collections.GdxMap
import ktx.collections.set
import com.libgdx.game.game.Socket

abstract class LayoutImpl : Layout {

    protected abstract val sockets: GdxArray<Socket>

    protected val cellMap = GdxMap<Int, Socket>()

    protected fun getCellIndex(column: Int, row: Int) = row * numberOfColumns + column

    protected fun init() {
        sockets.items.forEach { socket ->
            cellMap[getCellIndex(socket.column, socket.row)] = socket
        }
    }

    override operator fun get(index: Int): Socket = sockets[index]

    override fun lookup(column: Int, row: Int): Socket? {
        if (column >= numberOfColumns || row >= numberOfRows) return null
        val index = getCellIndex(column, row)
        return if (cellMap.containsKey(index)) cellMap[index] else null
    }

    override val numberOfSockets: Int get() = sockets.size

}
