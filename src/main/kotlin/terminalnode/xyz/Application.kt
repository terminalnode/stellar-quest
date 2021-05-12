package terminalnode.xyz

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import terminalnode.xyz.config.routing.configureRoutingSet2
import terminalnode.xyz.config.routing.configureRoutingSet3

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(StatusPages) {
      exception<NotImplementedError> {
        call.respondText(
          "The requested feature has not been implemented yet!\n",
          ContentType.Text.Plain
        )
      }

      exception<Throwable> { cause ->
        call.respondText(
          "${cause.message ?: "Unknown error"}\n".also { cause.printStackTrace() },
          ContentType.Text.Plain
        )
      }
    }

    install(ContentNegotiation) {
      gson { setPrettyPrinting() }
    }

    configureRoutingSet2()
    configureRoutingSet3()
  }.start(wait = true)
}