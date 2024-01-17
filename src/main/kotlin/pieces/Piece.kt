package pieces

import board.*

/**
 *
 * @author Jonas Pollpeter
 */

abstract class Piece(
    val type: PieceType,
    open val color: PieceColor,
    protected open var position: Position,
    open val board: Board,
) {

    protected var hasMoved: Boolean = false

    abstract fun getValidMoves(board: Board): List<Move>

    protected fun getValidMovesInDirection(
        moveDirection: MoveDirection,
        moveCount: Int = 1,
        moveType: MoveType = MoveType.Normal,
    ): List<Move> {
        if (moveCount < 1) {
            throw IllegalArgumentException("moveWidth must be greater than 0")
        }

        val validMoves = mutableListOf<Move>()
        var nextPosition: Position = position
        var remainingMoves = moveCount
        if (moveType in listOf(MoveType.Normal, MoveType.NoHit, MoveType.Promotion)) {
            while (remainingMoves > 0) {
                nextPosition = nextPosition.plus(moveDirection)
                if (nextPosition.isOnBoard() && !board.isOccupied(nextPosition)) {
                    validMoves.add(Move(this, position, nextPosition))
                    remainingMoves--
                } else {
                    break
                }
            }
        }

        // check if another move is valid by hitting an enemy piece
        if (remainingMoves > 0 && moveType.canHit() && nextPosition.isOnBoard() && board.isOccupiedByColor(nextPosition, color.opposite())) {
            validMoves.add(Move(this, position, nextPosition))
        }
        return validMoves
    }

    fun move(to: Position) {
        position = to
        if (!hasMoved) {
            hasMoved = true
        }
    }
}