import game.Game
import game.GameState
import gui.Visualizer
import pieces.PieceColor
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

fun main() {
    println("Chess")

    thread {
        playGames(-1, 10, 1000)
    }
}

fun playGames(n: Int = 1, delayBetweenMoves: Long = 100, delayBetweenGames: Long = 1000, asColor: Set<PieceColor> = emptySet()) {
    if (n <= 0) {
        while (true){
            playGame(delayBetweenMoves, delayBetweenGames, asColor)
        }
    } else {
        repeat(n) {
            playGame(delayBetweenMoves, delayBetweenGames, asColor)
        }
    }
}

fun playGame(delayBetweenMoves: Long = 0, delayBeforeCloseVisualizer: Long = 1000, asColor: Set<PieceColor> = emptySet()){
    val game = Game()
    game.onWhiteTurn = {}
    game.onBlackTurn = {}
    game.start()

    var visualizer: Visualizer? = null
    SwingUtilities.invokeLater {
        visualizer = Visualizer(game, actionAllowedForColor = asColor)
        visualizer?.isVisible = true
    }
    while (game.state == GameState.Running) {
        game.doRandomValidMove()
        Thread.sleep(delayBetweenMoves)
    }
    Thread.sleep(delayBeforeCloseVisualizer)
    SwingUtilities.invokeLater {
        visualizer?.close()
    }
}