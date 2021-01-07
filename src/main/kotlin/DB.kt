import ch.qos.logback.core.util.OptionHelper.getEnv
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import java.net.URI

object DB {
    private val host: String
    private val port: Int
    private val dbName: String
    private val dbUser: String
    private val dbPassword: String

init {
    val dbUrl = System.getenv("DATABASE_URL")
    if (dbUrl != null) {
        val dbUri = URI(dbUrl)
        host = dbUri.host
        port = dbUri.port
        dbName = dbUri.path.substring(1)
        val userInfo = dbUri.userInfo.split(":")
        dbUser = userInfo[0]
        dbPassword = userInfo[1]
    }
    else {
        host = System.getenv("DB_HOST")
        port = System.getenv("DB_PORT").toInt()
        dbName = System.getenv("DB_NAME")
        dbUser = System.getenv("DB_USER")
        dbPassword = System.getenv("DB_PASSWORD")
    }
}

    fun connect() = Database.connect("jdbc:postgresql://$host:$port/$dbName", driver = "org.postgresql.Driver",
        user = dbUser, password = dbPassword)
}

//fun getNonNullEnv(name: String) = System.getenv(name) ?: throw Exception("You must pass env with name $name")

//object DB {
//    private val host = System.getenv("DB_HOST")
//    private val port = System.getenv("DB_PORT")
//    private val dbName = System.getenv("DB_NAME")
//    private val dbUser = System.getenv("DB_USER")
//    private val dbPassword = System.getenv("DB_PASSWORD")
//    fun connect() = Database.connect("jdbc:postgresql://$host:$port/$dbName", driver = "org.postgresql.Driver",
//        user = dbUser, password = dbPassword)
//}
//
//object Clients: IntIdTable() {
//    val name = varchar("name", 20).uniqueIndex()
//    val age = integer("age").default(0)
//}
//
//data class Client(
//    val id: Int,
//    val name: String,
//    val age: Int
//)