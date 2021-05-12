package terminalnode.xyz.config.routing

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.StellarSet2
import terminalnode.xyz.quests.StellarSet3

fun Application.configureRoutingSet2() {
  routing {
    route("/set/2/quest") {
      get("/1/{publicKey}") {
        val publicKey = call.parameters["publicKey"]
          ?: throw Exception("Please provide the public key of your account")
        StellarSet2.quest1(publicKey)
      }

      get("/2/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet2.quest2(secretKey)
      }

      get("/3/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet2.quest3(secretKey)
      }

      get("/4") {
        StellarSet2.quest4()
      }

      get("/5") {
        StellarSet2.quest5()
      }

      get("/6") {
        StellarSet2.quest6()
      }

      get("/7") {
        StellarSet2.quest7()
      }

      get("/8") {
        StellarSet2.quest8()
      }
    }
  }
}