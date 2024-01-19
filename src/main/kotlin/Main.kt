import board.Move
import game.Game
import game.GameState
import game.GameStateListener
import gui.Visualizer
import pieces.PieceColor
import java.lang.IllegalStateException
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

fun main() {
    println("Chess")

    playGames(n = 1, delayBetweenMoves = 10, delayBetweenGames = 1000, concurrent = false, asColor = setOf(PieceColor.White, PieceColor.Black))
}

fun playGames(
    n: Int = 1,
    delayBetweenMoves: Long = 100,
    delayBetweenGames: Long = 1000,
    concurrent: Boolean = false,
    asColor: Set<PieceColor> = emptySet()
) {
    if (n < 0) {
        if (concurrent) throw IllegalStateException("concurrent games not allowed for infinite games")
        playGame(delayBetweenMoves, delayBetweenGames, asColor) {
            playGames(n, delayBetweenMoves, delayBetweenGames, false, asColor)
        }
    } else if (n > 0) {
        if (concurrent) {
            repeat(n) {
                playGame(delayBetweenMoves, delayBetweenGames, asColor)
            }
        } else {
            playGame(delayBetweenMoves, delayBetweenGames, asColor) {
                playGames(n - 1, delayBetweenMoves, delayBetweenGames, false, asColor)
            }
        }
    }
}

fun playGame(
    delayBetweenMoves: Long = 0,
    delayBeforeCloseVisualizer: Long = 1000,
    asColor: Set<PieceColor> = emptySet(),
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

    SwingUtilities.invokeLater {
        visualizer = Visualizer(game, actionAllowedForColor = asColor)
        visualizer?.isVisible = true

        game.start()
    }
}