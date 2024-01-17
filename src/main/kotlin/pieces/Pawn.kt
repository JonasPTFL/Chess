package pieces

import board.*

class Pawn(
    override val color: PieceColor,
    override var position: Position,
    override var board: Board
) : Piece(PieceType.Pawn, color, position, board) {
    override fun getValidMoves(board: Board): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(
            getValidMovesInDirection(
                if (color == PieceColor.White) MoveDirection.Up else MoveDirection.Down,
                type.defaultMoveCount,
                MoveType.NoHit
            )
        )
        validMoves.addAll(
            getValidMovesInDirection(
                if (color == PieceColor.White) MoveDirection.UpRight else MoveDirection.DownRight,
                type.defaultMoveCount,
                MoveType.OnlyHit
            )
        )
        validMoves.addAll(
            getValidMovesInDirection(
                if (color == PieceColor.White) MoveDirection.UpLeft else MoveDirection.DownLeft,
                type.defaultMoveCount,
                MoveType.OnlyHit
            )
        )
        if (!hasMoved) {
            validMoves.addAll(
                getValidMovesInDirection(
                    if (color == PieceColor.White) MoveDirection.Up else MoveDirection.Down,
                    type.defaultMoveCount + 1,
                    MoveType.NoHit
                )
            )
        }
        return validMoves
    }
}
