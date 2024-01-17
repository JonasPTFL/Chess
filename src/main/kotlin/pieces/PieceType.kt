package pieces

/**
 *
 * @author Jonas Pollpeter
 */

enum class PieceType(val description: String, val asciiSymbol: String, val defaultMoveCount: Int) {
    Queen("Queen", "♕", 7),
    King("King", "♔", 1),
    Rook("Rook", "♖", 7),
    Bishop("Bishop", "♗", 7),
    Knight("Knight", "♘", 1),
    Pawn("Pawn", "♙", 1)
}