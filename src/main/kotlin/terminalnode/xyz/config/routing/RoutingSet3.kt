package terminalnode.xyz.config.routing

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.StellarSet3

fun Application.configureRoutingSet3() {
  routing {
    route("/set/3/quest") {
      get("/1") {
        StellarSet3.quest1()
      }

      get("2/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest2(secretKey)
      }

      get("3/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest3(secretKey)
      }

      get("/4") {
        StellarSet3.quest4()
      }

      get("/5") {
        StellarSet3.quest5()
      }

      get("/6") {
        StellarSet3.quest6()
      }

      get("/7") {
        StellarSet3.quest7()
      }

      get("/8") {
        StellarSet3.quest8()
      }
    }
  }
}