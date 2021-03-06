//import clients.ClientServiceDB
//import clients.clientRouter
import clients.Cats
import clients.CatsServiceDB
import clients.catRouter
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    val server = embeddedServer(Netty, port, module = Application::mainModule)

    server.start()
}

fun Application.mainModule() {

    DB.connect()

    transaction {
        SchemaUtils.create(Cats)
    }

    install(ContentNegotiation) {
        jackson{
            enable(SerializationFeature.INDENT_OUTPUT)//Make sure your application outputs JSON
        }
    }
    routing {
        trace {
            application.log.debug(it.buildText())
        }
        get("/{name}") {
            val name = call.parameters["name"]

            context.respond(mapOf("Cat name:" to name))
        }
        get {
            context.respond(mapOf("Welcome" to "our Cat Hotel"))
        }
        catRouter(CatsServiceDB())
    }
}

//fun main() {
//    val port = 8080
//
//    val server = embeddedServer(Netty, port, module = Application::mainModule)
//
//    server.start()
//}
//
//fun Application.mainModule() {
//
//    DB.connect()
//
//    transaction{
//        SchemaUtils.create(Cats)
//    }
//
//    install(ContentNegotiation) {
//        jackson {
//            enable(SerializationFeature.INDENT_OUTPUT)
//
//        }
//    }
//    routing {
//        trace {
//            application.log.debug(it.buildText())
//        }
//        get {
//            context.respond(mapOf("Welcome" to "to the EquisMate website"))
//        }
//        get("/{name}") {
//            val name = context.parameters["name"]
//            context.respond(mapOf("Cat name" to name))
//        }
//        catRouter(catServiceDB())
//    }
//}