import board.Board
import gui.Visualizer
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

fun main() {
    println("Chess")
    val board = Board()

    var visualizer: Visualizer? = null
    SwingUtilities.invokeLater {
        visualizer = Visualizer(board)
        visualizer?.isVisible = true
    }

    thread {
        while (true) {
            board.doRandomValidMove()
            visualizer?.update()
            Thread.sleep(1000)
        }
    }
}