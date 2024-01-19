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

    var hasMoved: Boolean = false

    var position = initialPosition
        protected set

    fun getValidMoves(): List<Move> {
        return if (board.isCheck(color) && type != PieceType.King) getPossibleMoves().filter { it.blocksCheck(board, color) }
        else getPossibleMoves()
    }

    open fun getPossibleMoves(): List<Move> {
        return type.moveDirections
            .map { getValidMovesInDirection(it, type.defaultMoveCount) }
            .flatten()
    }

    /**
     * Returns all positions, that are threatened by this piece.
     * This will ignore the opponent king, if it is in the way, to check positions behind it.
     * These positions are threatened for a king of the opposite color and can therefore not be moved to by the king.
     */
    open fun getThreatenedPositions(): List<Position> {
        return type.moveDirections
            .map { getValidMovesInDirection(it, type.defaultMoveCount, moveType = MoveType.ThreatensKing) }
            .flatten()
            .map { it.to }
    }

    protected fun getValidMovesInDirection(
        moveDirection: MoveDirection,
        moveCount: Int = 1,
        moveType: MoveType = MoveType.Normal
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
                moveType.canMoveNormally() && pieceAtPosition == null -> validMoves.add(
                    Move(
                        this,
                        position,
                        newPosition,
                        moveType
                    )
                )
                // opponent piece at position, move by hitting
                moveType.canHit() && pieceAtPosition.isOppositeColor(this) -> {
                    validMoves.add(Move(this, position, newPosition, moveType))
                    break
                }

                moveType == MoveType.ThreatensKing -> {
                    validMoves.add(Move(this, position, newPosition, moveType))
                    // proceed in direction and ignore king, to check if position behind the king is threatened too
                    if (pieceAtPosition?.type != PieceType.King) break
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