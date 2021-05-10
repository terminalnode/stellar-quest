package terminalnode.xyz.config

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import terminalnode.xyz.functions.*

fun Application.configureRouting() {
  routing {
    get("/") {
      call.respondText("I'm alive")
    }

    get("/{test}") {
      println(call.parameters["test"])
      call.respondText("I'm alive")
    }

    get("/set3/quest2/{secretKey}") {
      val secretKey = call.parameters["secretKey"]
        ?: throw Exception("Please provide the secret key of your account")
      set3quest2(secretKey)
    }

    get("/set3/quest3/{secretKey}") {
      val secretKey = call.parameters["secretKey"]
        ?: throw Exception("Please provide the secret key of your account")
      set3quest3(secretKey)
    }
  }
}