package terminalnode.xyz.config.routing

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.StellarSet2

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

      get("/4/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet2.quest4(secretKey)
      }

      get("/5/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet2.quest5(secretKey)
      }

      get("/6/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")
        StellarSet2.quest6(secretKey)
      }

      get("/7") {
        StellarSet2.quest7()
      }

      get("/8/{secretKey}") {
        val secretKey = call.parameters["secretKey"]
          ?: throw Exception("Please provide the secret key of your account")

        // Change this to whatever your domain should be, I don't feel like passing it as a param.
        StellarSet2.quest8(secretKey, "terminalnode.xyz")
      }
    }
  }
}