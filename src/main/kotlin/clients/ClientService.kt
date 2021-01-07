package clients

//import Cat
//import Client
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface CatsService {
    suspend fun create(name: String, age: Int?): Int

    suspend fun all(): List<Cat>

    suspend fun findById(id: Int): Cat?

    suspend fun delete(id: Int)

    suspend fun update(id: Int, name: String, age: Int?)
}

class CatsServiceDB : CatsService {
    override suspend fun update(id: Int, name: String, age: Int?) {
        transaction {
            Cats.update({ Cats.id eq id }) {
                it[Cats.name] = name
                if (age != null) {
                    it[Cats.age] = age
                }
            }
        }
    }

    override suspend fun delete(id: Int) {
        transaction {
            Cats.deleteWhere {
                Cats.id eq id
            }
        }
    }

    override suspend fun findById(id: Int): Cat? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            Cats.select {
                Cats.id eq id // select cats.id, cats.name, cats.age from catsw where cats.id = 1
            }.firstOrNull()
        }
        return row?.asCat()
    }

    override suspend fun create(name: String, age: Int?): Int {
        val id = transaction {
            Cats.insertAndGetId { cat ->
                cat[Cats.name] = name
                if (age != null) {
                    cat[Cats.age] = age
                }
            }
        }
        return id.value
    }

    override suspend fun all(): List<Cat> {
        return transaction {
            Cats.selectAll().map { row ->
                row.asCat()
            }
        }
    }

    private fun ResultRow.asCat() = Cat(
        this[Cats.id].value,
        this[Cats.name],
        this[Cats.age]
    )

}

//interface ClientService {
//    suspend fun create(name: String, age: Int?): Int
//    suspend fun all(): List<Client>
//    suspend fun findById(id: Int): Client?
//    suspend fun update(id: Int, name: String, age: Int?)
//    suspend fun delete(id: Int)
//}
//
//class ClientServiceDB : ClientService {
//    override suspend fun delete(id: Int) {
//        transaction {
//            Clients.deleteWhere {
//                Clients.id eq id
//            }
//        }
//    }
//
//    override suspend fun create(name: String, age: Int?): Int {
//        val id = transaction {
//            Clients.insertAndGetId { client ->
//                client[Clients.name] = name
//                if (age != null) {
//                    client[Clients.age] = age
//                }
//            }
//        }
//        return id.value
//    }
//
//    override suspend fun all(): List<Client> {
//        return transaction {
//            Clients.selectAll().map { row ->
//                row.asClient()
//            }
//        }
//    }
//
//    override suspend fun findById(id: Int): Client? {
//        val row = transaction {
//            addLogger(StdOutSqlLogger)
//            Clients.select {
//                Clients.id eq id
//            }.firstOrNull()
//        }
//        return row?.asClient()
//    }
//
//    override suspend fun update(id: Int, name: String, age: Int?) {
//        transaction {
//            Clients.update({ Clients.id eq id }) {
//                it[Clients.name] = name
//                if (age != null) {
//                    it[Clients.age] = age
//                }
//            }
//        }
//    }
//
//
//    private fun ResultRow.asClient() = Client(
//        this[Clients.id].value,
//        this[Clients.name],
//        this[Clients.age]
//    )
//}

