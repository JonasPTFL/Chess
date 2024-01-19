package pieces

import board.Board
import board.Move
import board.Position


/**
 *
 * @author Jonas Pollpeter
 */

class King(
    override val color: PieceColor,
    initialPosition: Position,
    override var board: Board
) : Piece(PieceType.King, color, initialPosition, board) {
    override fun getPossibleMoves(): List<Move> {
        return PieceType.King.moveDirections.filterNot { moveDirection ->
            position.plus(moveDirection).isThreatened(color.opposite(), board)
        }.flatMap { getValidMovesInDirection(it) }
    }
}