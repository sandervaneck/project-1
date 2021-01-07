package clients

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.catRouter(catsService: CatsService) {
    route("/cats") {
        post {
            with (call) {
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                val catId = catsService.create(name, age)
                respond(HttpStatusCode.Created, catId)
            }
        }
        put("/{id}") {
            with (call) {
                val id = requireNotNull(parameters["id"]).toInt()
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                catsService.update(id, name, age)
            }
        }
        get("/{id}") {
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()
                val cat = catsService.findById(id)

                if (cat == null) {
                    respond(HttpStatusCode.NotFound)
                }
                else {
                    respond(cat)
                }
            }
        }
        delete("/{id}") {
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()
                catsService.delete(id)
            }
        }
        get {
            call.respond(catsService.all())
        }
    }
}

//fun Route.clientRouter(clientService: ClientService) {
//    route("/clients") {
//        post{
//            with(call) {
//                val parameters = receiveParameters()
//                val name = requireNotNull(parameters["name"])
//                val age = parameters["age"]?.toInt()
//
//                val clientID = clientService.create(name, age)
//                respond(HttpStatusCode.Created, clientID)
//            }
//        }
//        put("/{id}") {
//            with(call) {
//                val id = requireNotNull(parameters["id"]).toInt()
//                val parameters = receiveParameters()
//                val name = requireNotNull(parameters["name"])
//                val age = parameters["age"]?.toInt()
//                clientService.update(id, name, age)
//            }
//        }
//        get {
//            call.respond(clientService.all())
//        }
//        get("/{id}") {
//            with(call) {
//                val id = requireNotNull(parameters["id"]).toInt()
//                val client = clientService.findById(id)
//
//                if (client == null) {
//                    respond(HttpStatusCode.NotFound)
//                }
//                else {
//                    respond(client)
//                }
//            }
//        }
//        delete("/{id}") {
//            with(call) {
//                val id = requireNotNull(parameters["id"]).toInt()
//                clientService.delete(id)
//            }
//        }
//    }
//}