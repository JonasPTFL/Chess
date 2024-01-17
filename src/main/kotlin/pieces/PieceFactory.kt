package pieces

import board.Board
import board.Position


/**
 *
 * @author Jonas Pollpeter
 */

class PieceFactory {

    companion object {
        fun createPiece(type: PieceType, color: PieceColor, position: Position, board: Board): Piece {
            return when (type) {
                PieceType.King -> King(color, position, board)
                PieceType.Pawn -> Pawn(color, position, board)
                else -> Piece(type, color, position, board)
            }
        }
    }
}