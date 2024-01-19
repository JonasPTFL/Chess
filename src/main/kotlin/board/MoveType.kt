package board


/**
 *
 * @author Jonas Pollpeter
 */

enum class MoveType {
    Normal, NoHit, OnlyHit, CastlingKingSide, CastlingQueenSide, EnPassant, Promotion, ThreatensKing;

    fun canHit() = this == Normal || this == OnlyHit || this == EnPassant
    fun canMoveNormally() = this == Normal || this == Promotion || this == NoHit || this == ThreatensKing
    fun isCastling() = this == CastlingKingSide || this == CastlingQueenSide
}
