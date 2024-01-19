import game.Game
import game.GameState
import gui.Visualizer
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

fun main() {
    println("Chess")

    thread {
        while (true) {
            val game = Game()

            var visualizer: Visualizer? = null
            SwingUtilities.invokeLater {
                visualizer = Visualizer(game, actionAllowedForColor = emptySet())
                visualizer?.isVisible = true

                game.start()
            }
            while (game.state == GameState.Running) { }
            Thread.sleep(1000)
            SwingUtilities.invokeLater {
                visualizer?.close()
            }
        }
    }
}