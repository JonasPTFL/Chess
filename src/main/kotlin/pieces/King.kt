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
    override fun getValidMoves(board: Board): List<Move> {
        return PieceType.King.moveDirections.filter {
            val newPos = position.plus(it)
            !newPos.isThreatened(color.opposite(), board)  && newPos.isOnBoard()&& !board.isOccupiedByColor(newPos, color)
        }.map { Move(this, position, position.plus(it)) }
    }
}