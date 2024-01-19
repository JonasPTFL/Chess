package board


/**
 *
 * @author Jonas Pollpeter
 */

enum class MoveType {
    Normal, NoHit, OnlyHit, Castling, EnPassant, Promotion, ThreatensKing;

    fun canHit() = this == Normal || this == OnlyHit || this == EnPassant
    fun canMoveNormally() = this == Normal || this == Promotion || this == NoHit || this == ThreatensKing
}
