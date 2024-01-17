package board

import pieces.PieceColor
import pieces.PieceType
import kotlin.math.abs
import kotlin.math.max


/**
 *
 * @author Jonas Pollpeter
 */

data class Position(val x: Int, val y: Int) {
    fun isOnBoard(): Boolean {
        return x in 0..7 && y in 0..7
    }

    fun isThreatened(color: PieceColor, board: Board): Boolean {
        board.getAllPieces(color).forEach { piece ->
            if (piece.type == PieceType.King) return distanceTo(piece.position) <= 1
            piece.getValidMoves(board).forEach { move ->
                if (move.to == this) {
                    return true
                }
            }
        }
        return false
    }

    fun distanceTo(position: Position): Int {
        return max(abs(x - position.x), abs(y - position.y))
    }

    fun plus(direction: MoveDirection): Position {
        return Position(x + direction.xDirection, y + direction.yDirection)
    }
}
