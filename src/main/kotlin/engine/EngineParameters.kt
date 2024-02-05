package engine


/**
 *
 * @author Jonas Pollpeter
 */

data class EngineParameters(
    /* search algorithm */
    val maxDepth: Int = 3,
    val maxDepthEndgame: Int = 5,
    val maxTime: Long = 5000,
    /* evaluation */
    val pieceValueWeight: Float = 1f,
    val checkmateWeight: Int = 1000,
    val checkWeight: Int = 5,
    /* chess definitions */
    val endgameMaxPieceCount: Int = 13
)
