package pieces

import board.*

/**
 *
 * @author Jonas Pollpeter
 */

open class Piece(
    val type: PieceType,
    open val color: PieceColor,
    initialPosition: Position,
    open val board: Board,
) {

    protected var hasMoved: Boolean = false
    var position = initialPosition
        protected set

    open fun getValidMoves(board: Board): List<Move> {
        return type.moveDirections
            .map { getValidMovesInDirection(it, type.defaultMoveCount) }
            .flatten()
    }

    protected fun getValidMovesInDirection(
        moveDirection: MoveDirection,
        moveCount: Int = 1,
        moveType: MoveType = MoveType.Normal,
    ): List<Move> {
        val validMoves = mutableListOf<Move>()
        var remainingMoves = moveCount
        var newPosition = position

        while (remainingMoves > 0) {
            newPosition = newPosition.plus(moveDirection)
            // cancel move if position is not on board
            if (!newPosition.isOnBoard()) break

            val pieceAtPosition = board.getPiece(newPosition)
            when {
                // no piece at position, move normally
                moveType.canMoveNormally() && pieceAtPosition == null -> validMoves.add(Move(this, position, newPosition))
                // opponent piece at position, move by hitting
                moveType.canHit() && pieceAtPosition.isOppositeColor(this) -> {
                    validMoves.add(Move(this, position, newPosition))
                    break
                }
                // own piece at position, cancel move
                pieceAtPosition != null -> break
            }

            remainingMoves--
        }
        return validMoves
    }

    fun move(to: Position) {
        position = to
        if (!hasMoved) {
            hasMoved = true
        }
    }


    companion object {
        fun Piece?.isOppositeColor(other: Piece?): Boolean {
            return this != null && other != null && this.color.isOpposite(other.color)
        }
    }
}