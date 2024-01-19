package pieces

import board.*

class Pawn(
    override val color: PieceColor,
    initialPosition: Position,
    override var board: Board
) : Piece(PieceType.Pawn, color, initialPosition, board) {
    override fun getPossibleMoves(): List<Move> {
        val validMoves = mutableListOf<Move>()
        validMoves.addValidMovesInDirection(MoveDirection.Up, MoveDirection.Down, MoveType.NoHit)
        validMoves.addValidMovesInDirection(MoveDirection.UpRight, MoveDirection.DownRight, MoveType.OnlyHit)
        validMoves.addValidMovesInDirection(MoveDirection.UpLeft, MoveDirection.DownLeft, MoveType.OnlyHit)
        validMoves.addValidMovesInDirection(MoveDirection.UpRight, MoveDirection.DownRight, MoveType.EnPassant)
        validMoves.addValidMovesInDirection(MoveDirection.UpLeft, MoveDirection.DownLeft, MoveType.EnPassant)
        if (isOnStartingPosition()) {
            validMoves.addValidMovesInDirection(MoveDirection.Up, MoveDirection.Down, MoveType.NoHit, type.defaultMoveCount + 1)
        }
        return validMoves
    }

    private fun isOnStartingPosition(): Boolean {
        return (color == PieceColor.White && position.y == 1) || (color == PieceColor.Black && position.y == 6)
    }

    override fun getThreatenedPositions(): List<Position> {
        val threatenedMoves = mutableListOf<Move>()
        threatenedMoves.addValidMovesInDirection(MoveDirection.UpRight, MoveDirection.DownRight, MoveType.ThreatensKing)
        threatenedMoves.addValidMovesInDirection(MoveDirection.UpLeft, MoveDirection.DownLeft, MoveType.ThreatensKing)
        return threatenedMoves.map { it.to }
    }

    private fun MutableList<Move>.addValidMovesInDirection(
        directionWhite: MoveDirection,
        directionBlack: MoveDirection,
        moveType: MoveType,
        moveCount: Int = type.defaultMoveCount
    ) {
        addAll(
            getValidMovesInDirection(
                if (color == PieceColor.White) directionWhite else directionBlack,
                moveCount,
                moveType
            )
        )
    }
}
