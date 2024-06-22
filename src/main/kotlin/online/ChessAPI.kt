package online

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*


/**
 *
 * @author Jonas Pollpeter
 */

class ChessAPI {
    companion object {
        const val API_URL = "http://localhost:3000"

        object Paths {
            const val CREATE_GAME = "/create-game"
            const val GAMES = "/games"
            const val JOIN_GAME = "/join-game"
            const val LOGIN = "/login"
        }

        fun endpoint(path: String): String {
            return "$API_URL$path"
        }
    }

    private var client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
    }

    private fun authenticateClient(loginReturnData: LoginReturnData) {
        client = client.config {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(loginReturnData.token, "")
                    }
                }
            }
        }
    }

    suspend fun login(loginData: LoginData): LoginReturnData {
        return client.post(endpoint(Paths.LOGIN)) {
            contentType(ContentType.Application.Json)
            setBody(loginData)
        }.body<LoginReturnData>().also { authenticateClient(it) }
    }

    /**
     * Creates a new game and returns the game ID
     * @return the gameID or null if the game could not be created
     */
    suspend fun createGame(): OnlineGame {
        return client.post(endpoint(Paths.CREATE_GAME)).body()
    }

    /**
     * Creates a new game and returns the game ID
     * @return the gameID or null if the game could not be created
     */
    suspend fun joinGame(gameID: String): OnlineGame {
        return client.post(endpoint(Paths.JOIN_GAME)) {
            contentType(ContentType.Application.Json)
            setBody(JoinGameData(gameID))
        }.body()
    }

    suspend fun getGames(): List<OnlineGame> {
        return client.get(endpoint(Paths.GAMES)).body()
    }
}