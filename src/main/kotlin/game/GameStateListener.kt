package game

import board.Move


/**
 *
 * @author Jonas Pollpeter
 */

interface GameStateListener {
    fun onGameStateChanged(gameState: GameState)

    fun onMoveExecuted(move: Move)
}