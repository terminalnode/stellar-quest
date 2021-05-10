package terminalnode.xyz.config

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.*

fun Application.configureRouting() {
  routing {
    get("/set3/quest2/{secretKey}") {
      val secretKey = call.parameters["secretKey"]
        ?: throw Exception("Please provide the secret key of your account")
      StellarSet3.quest2(secretKey)
    }

    get("/set3/quest3/{secretKey}") {
      val secretKey = call.parameters["secretKey"]
        ?: throw Exception("Please provide the secret key of your account")
      StellarSet3.quest3(secretKey)
    }
  }
}