package terminalnode.xyz

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import terminalnode.xyz.config.configureRouting

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(StatusPages)
    install(ContentNegotiation) {
      gson { setPrettyPrinting() }
    }

    configureRouting()
  }.start(wait = true)
}