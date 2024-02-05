package engine


/**
 *
 * @author Jonas Pollpeter
 */

data class EngineParameters(
    /* search algorithm */
    val maxDepth: Int = 2,
    val maxDepthEndgame: Int = 5,
    val maxTime: Long = 50000,
    /* evaluation */
    val pieceValueWeight: Float = 1f,
    val checkmateWeight: Int = 1000,
    val checkWeight: Int = 5,
    /* chess definitions */
    val endgameMaxPieceCount: Int = 13
) {
    fun printToConsole() {
        println("Engine parameters:")
        println("   maxDepth: $maxDepth")
        println("   maxDepthEndgame: $maxDepthEndgame")
        println("   maxTime: $maxTime")
        println("   pieceValueWeight: $pieceValueWeight")
        println("   checkmateWeight: $checkmateWeight")
        println("   checkWeight: $checkWeight")
        println("   endgameMaxPieceCount: $endgameMaxPieceCount")
    }
}
