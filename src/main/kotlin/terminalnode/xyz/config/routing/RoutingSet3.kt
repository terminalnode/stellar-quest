package terminalnode.xyz.config.routing

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.StellarSet3

fun Application.configureRoutingSet3() {
  routing {
    route("/set/3/quest") {
      get("/1/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest1(secretKey)
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

      get("/4/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest4(secretKey)
      }

      get("/5/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest5(secretKey)
      }

      get("/6/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest6(secretKey)
      }

      get("/6/{secretKey}/dirty") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet3.quest6Dirty(secretKey)
      }

      get("/6/daniel") {
        StellarSet3.quest6Daniel()
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