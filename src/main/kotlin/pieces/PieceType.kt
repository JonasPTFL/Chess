package pieces

import board.MoveDirection

/**
 *
 * @author Jonas Pollpeter
 */

enum class PieceType(
    val description: String,
    val asciiSymbol: String,
    val defaultMoveCount: Int,
    val value: Int,
    val moveDirections: Set<MoveDirection>
) {
    Queen(
        "Queen",
        "♕",
        7,
        9,
        MoveDirection.getAllNormalDirections()
    ),
    King(
        "King",
        "♔",
        1,
        0,
        MoveDirection.getAllNormalDirections()
    ),
    Rook(
        "Rook",
        "♖",
        7,
        5,
        setOf(MoveDirection.Up, MoveDirection.Right, MoveDirection.Down, MoveDirection.Left)
    ),
    Bishop(
        "Bishop",
        "♗",
        7,
        3,
        setOf(MoveDirection.UpRight, MoveDirection.DownRight, MoveDirection.DownLeft, MoveDirection.UpLeft)
    ),
    Knight(
        "Knight",
        "♘",
        1,
        3,
        setOf(
            MoveDirection.UpUpRight,
            MoveDirection.UpRightRight,
            MoveDirection.DownRightRight,
            MoveDirection.DownDownRight,
            MoveDirection.DownDownLeft,
            MoveDirection.DownLeftLeft,
            MoveDirection.UpLeftLeft,
            MoveDirection.UpUpLeft
        )
    ),
    Pawn(
        "Pawn",
        "♙",
        1,
        1,
        setOf(MoveDirection.Up, MoveDirection.UpRight, MoveDirection.UpLeft)
    );

    fun getCharacterIdentifier(): Char {
        return description.firstOrNull().takeUnless { this == King } ?: '+'
    }

    companion object {
        fun isValidPromotionPiece(pieceType: PieceType) = pieceType == Queen || pieceType == Rook || pieceType == Bishop || pieceType == Knight
    }
}