package pieces

import board.Board
import board.Move
import board.MoveType
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

    override fun getValidMoves(): List<Move> {
        return getPossibleMoves()
    }

    override fun getPossibleMoves(): List<Move> {
        val moves = PieceType.King.moveDirections.filterNot { moveDirection ->
            position.plus(moveDirection).isThreatened(color.opposite(), board)
        }.flatMap { getValidMovesInDirection(it) }.toMutableList()

        // add castling moves
        if (!hasMoved && !board.isCheck(color)) {
            val rookPositions = listOf(Position(0, position.y), Position(7, position.y))
            rookPositions.forEach { rookPosition ->
                val rook = board.getPiece(rookPosition)
                if (rook?.type == PieceType.Rook && !rook.hasMoved) {
                    val positionsBetween = position.getBetweenPositions(rookPosition)
                    if (positionsBetween.none { board.isOccupied(it) || it.isThreatened(color.opposite(), board) }) {
                        moves.add(Move(this, position, rookPosition, if (rookPosition.x == 0) MoveType.CastlingQueenSide else MoveType.CastlingKingSide))
                    }
                }
            }
        }
        return moves
    }
}