package pieces

import board.MoveDirection

/**
 *
 * @author Jonas Pollpeter
 */

enum class PieceType(
    val description: String,
    val asciiSymbol: String,
    val fenNotationCharacter: Char,
    val defaultMoveCount: Int,
    val value: Int,
    val moveDirections: Set<MoveDirection>
) {
    Queen(
        "Queen",
        "♕",
        'Q',
        7,
        90,
        MoveDirection.getAllNormalDirections()
    ),
    King(
        "King",
        "♔",
        'K',
        1,
        900,
        MoveDirection.getAllNormalDirections()
    ),
    Rook(
        "Rook",
        "♖",
        'R',
        7,
        50,
        setOf(MoveDirection.Up, MoveDirection.Right, MoveDirection.Down, MoveDirection.Left)
    ),
    Bishop(
        "Bishop",
        "♗",
        'B',
        7,
        30,
        setOf(MoveDirection.UpRight, MoveDirection.DownRight, MoveDirection.DownLeft, MoveDirection.UpLeft)
    ),
    Knight(
        "Knight",
        "♘",
        'N',
        1,
        30,
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
        'P',
        1,
        10,
        setOf(MoveDirection.Up, MoveDirection.UpRight, MoveDirection.UpLeft)
    );

    fun getFENNotation(color: PieceColor): String {
        return if (color == PieceColor.White) {
            fenNotationCharacter.uppercase()
        } else {
            fenNotationCharacter.lowercase()
        }
    }

    companion object {
        fun isValidPromotionPiece(pieceType: PieceType) = pieceType == Queen || pieceType == Rook || pieceType == Bishop || pieceType == Knight
    }
}