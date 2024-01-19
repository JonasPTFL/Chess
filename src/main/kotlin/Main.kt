import game.Game
import gui.Visualizer
import javax.swing.SwingUtilities

fun main() {
    println("Chess")

    val game = Game()

    SwingUtilities.invokeLater {
        val visualizer = Visualizer(game)
        visualizer.isVisible = true

        game.start()
    }
}