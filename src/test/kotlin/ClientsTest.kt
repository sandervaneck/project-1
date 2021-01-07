import clients.Cats
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpMethod.Companion.Put
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CatsTest {
    @Test
    fun `Create cat`() {
        withTestApplication(Application::mainModule) {
            val call = createCat("Fuzzy", 3)

            assertEquals(HttpStatusCode.Created, call.response.status())
        }
    }

    @Test
    fun `All cats`() {
        withTestApplication(Application::mainModule) {
            val beforeCreate = handleRequest(HttpMethod.Get, "/cats")
            assertEquals("[]".asJSON(), beforeCreate.response.content?.asJSON())

            createCat("Shmuzy", 2)

            val afterCreate = handleRequest(HttpMethod.Get, "/cats")
            assertEquals("""[{"id":1,"name":"Shmuzy","age":2}]""".asJSON(), afterCreate.response.content?.asJSON())
        }
    }

    @Test
    fun `Cat by ID`() {
        withTestApplication(Application::mainModule) {
            val createCall = createCat("Apollo", 12)
            val id = createCall.response.content
            val afterCreate = handleRequest(Get, "/cats/$id")

            assertEquals("""{"id":1,"name":"Apollo","age":12}""".asJSON(), afterCreate.response.content?.asJSON())
        }
    }

    @Test
    fun `Delete cat`() {
        withTestApplication(Application::mainModule) {
            val createCall = createCat("Apollo", 12)
            val id = createCall.response.content
            handleRequest(Delete, "/cats/$id")

            val afterDelete = handleRequest(Get, "/cats/$id")

            assertEquals(HttpStatusCode.NotFound, afterDelete.response.status())
        }
    }

    @Test
    fun `Update cat`() {
        withTestApplication(Application::mainModule) {
            val createCall = createCat("Puzzy", 3)
            val id = createCall.response.content
            handleRequest(HttpMethod.Put, "/cats/$id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf("name" to "Fuzzy", "age" to 4.toString()).formUrlEncode())
            }

            val afterUpdate = handleRequest(HttpMethod.Get, "/cats/$id")
            assertEquals("""{"id":1,"name":"Fuzzy","age":4}""".asJSON(), afterUpdate.response.content?.asJSON())
        }
    }

    @Before
    fun setup() {
        DB.connect()
        transaction {
            SchemaUtils.drop(Cats)
        }
    }

}

fun TestApplicationEngine.createCat(name: String, age: Int): TestApplicationCall {
    return handleRequest(HttpMethod.Post, "/cats") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("name" to name, "age" to age.toString()).formUrlEncode())
    }
}

//class ClientsTest {
//    @Test
//    fun `Create client`() {
//        withTestApplication(Application::mainModule) {
//            val call = createClient("Fuzzy", 3)
//            Assert.assertEquals(HttpStatusCode.Created, call.response.status())
//        }
//    }
//
//    @Test
//    fun `All clients`() {
//        withTestApplication(Application::mainModule) {
//            val beforeCreate = handleRequest(HttpMethod.Get, "/clients")
//            assertEquals("[]".asJSON(), beforeCreate.response.content?.asJSON())
//
//            createClient("San", 22)
//            val afterCreate = handleRequest(HttpMethod.Get, "/clients")
//            assertEquals("""[{"id":1,"name":"San","age":22}]""".asJSON(), afterCreate.response.content?.asJSON())
//        }
//    }
//
//    @Test
//    fun `Client by ID`() {
//        withTestApplication(Application::mainModule) {
//            val createCall = createClient("Apollo", 12)
//            val id = createCall.response.content
//            val afterCreate = handleRequest(Get, "/clients/$id")
//
//            assertEquals("""{"id":1,"name":"Apollo","age":12}""".asJSON(), afterCreate.response.content?.asJSON())
//        }
//    }
//
//    @Test
//    fun `Delete client`() {
//        withTestApplication(Application::mainModule) {
//            val createCall = createClient("Apollo", 12)
//            val id = createCall.response.content
//            handleRequest(Delete, "/clients/$id")
//            val afterDelete = handleRequest(Get, "/clients/$id")
//
//            assertEquals(HttpStatusCode.NotFound, afterDelete.response.status())
//        }
//    }
//    @Test
//    fun `update`() {
//        withTestApplication(Application::mainModule) {
//            val createdClient = createClient("san", 22)
//            val id = createdClient.response.content
//            handleRequest(HttpMethod.Put, "/clients/$id") {
//                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//                setBody(listOf("name" to "Fuzzy", "age" to 4.toString()).formUrlEncode())
//            }
//            val afterUpdate = handleRequest(HttpMethod.Get, "/clients/$id")
//            assertEquals("""{"id":1,"name":"Fuzzy","age":4}""".asJSON(), afterUpdate.response.content?.asJSON())        }
//
//        }
//
//    @Before
//
//    fun cleanup() {
//        DB.connect()
//        transaction {
//            SchemaUtils.drop(Clients)
//        }
//    }
//}
//
//fun TestApplicationEngine.createClient(name: String, age: Int): TestApplicationCall {
//    return handleRequest(HttpMethod.Post, "/clients") {
//        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
//        setBody(listOf("name" to name, "age" to age.toString()).formUrlEncode())
//    }
//}