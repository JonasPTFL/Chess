package game

import board.Move
import gui.Visualizer
import pieces.PieceColor
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

/**
 *
 * @author Jonas Pollpeter
 */

class OfflineGameManager {

    fun playGames(
        n: Int = 1,
        delayBetweenMoves: Long = 100,
        delayBetweenGames: Long = 1000,
        concurrent: Boolean = false,
        whitePlayerType: PlayerType = PlayerType.Human,
        blackPlayerType: PlayerType = PlayerType.Engine,
    ) {
        if (n < 0) {
            if (concurrent) throw IllegalStateException("concurrent games not allowed for infinite games")
            playGame(delayBetweenMoves, delayBetweenGames, whitePlayerType) {
                playGames(n, delayBetweenMoves, delayBetweenGames, false, whitePlayerType, blackPlayerType)
            }
        } else if (n > 0) {
            if (concurrent) {
                repeat(n) {
                    playGame(delayBetweenMoves, delayBetweenGames, whitePlayerType, blackPlayerType)
                }
            } else {
                playGame(delayBetweenMoves, delayBetweenGames, whitePlayerType, blackPlayerType) {
                    playGames(n - 1, delayBetweenMoves, delayBetweenGames, false, whitePlayerType, blackPlayerType)
                }
            }
        }
    }

    fun playGame(
        delayBetweenMoves: Long = 0,
        delayBeforeCloseVisualizer: Long = 1000,
        whitePlayerType: PlayerType = PlayerType.Human,
        blackPlayerType: PlayerType = PlayerType.Engine,
        onGameFinished: (() -> Unit)? = null
    ) {
        val game = Game(randomMoveDelay = delayBetweenMoves)
        var visualizer: Visualizer? = null

        // add listener to close visualizer when game is over
        game.addGameStateListener(object : GameStateListener {
            override fun onGameStateChanged(gameState: GameState) {
                if (gameState != GameState.Running) {
                    thread {
                        Thread.sleep(delayBeforeCloseVisualizer)
                        SwingUtilities.invokeLater {
                            visualizer?.close()
                        }
                        onGameFinished?.invoke()
                    }
                }
            }

            override fun onMoveExecuted(move: Move) {}
        })

        val actionAllowedForColor = mutableSetOf<PieceColor>()

        // set allowed action for human players
        if (whitePlayerType == PlayerType.Human) actionAllowedForColor += PieceColor.White
        if (blackPlayerType == PlayerType.Human) actionAllowedForColor += PieceColor.Black

        // set white player action according to player type
        if (whitePlayerType != PlayerType.Human) {
            game.onWhiteTurn = {
                game.doMove(whitePlayerType)
            }
        }

        // set black player action according to player type
        if (blackPlayerType != PlayerType.Human) {
            game.onBlackTurn = {
                game.doMove(blackPlayerType)
            }
        }

        SwingUtilities.invokeLater {
            visualizer = Visualizer(game, actionAllowedForColor)
            visualizer?.isVisible = true

            game.start()
        }
    }

    fun playAgainst(type: PlayerType) = playGame(whitePlayerType = PlayerType.Human, blackPlayerType = type)
}