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
                PieceType.Queen -> Queen(color, position, board)
                PieceType.King -> King(color, position, board)
                PieceType.Rook -> Rook(color, position, board)
                PieceType.Bishop -> Bishop(color, position, board)
                PieceType.Knight -> Knight(color, position, board)
                PieceType.Pawn -> Pawn(color, position, board)
            }
        }
    }
}