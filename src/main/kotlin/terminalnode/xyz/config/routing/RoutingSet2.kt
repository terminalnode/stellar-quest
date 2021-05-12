package terminalnode.xyz.config.routing

import io.ktor.application.*
import io.ktor.routing.*
import terminalnode.xyz.quests.StellarSet2
import terminalnode.xyz.quests.StellarSet3

fun Application.configureRoutingSet2() {
  routing {
    route("/set/2/quest") {
      get("/1") {
        StellarSet2.quest1()
      }

      get("/2") {
        StellarSet2.quest2()
      }

      get("/3") {
        StellarSet2.quest3()
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