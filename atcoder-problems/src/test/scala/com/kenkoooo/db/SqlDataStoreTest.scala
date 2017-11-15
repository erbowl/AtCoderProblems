package com.kenkoooo.db

import com.kenkoooo.model.{Contest, Problem, Submission}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite, Matchers}
import scalikejdbc._

import scala.io.Source

class SqlDataStoreTest extends FunSuite with BeforeAndAfter with Matchers with BeforeAndAfterAll {
  val driver = "org.h2.Driver"
  val url = "jdbc:h2:mem:Test;mode=MySQL;DB_CLOSE_DELAY=-1"
  val sqlUser = "user"
  val sqlPass = "pass"

  Class.forName(driver)
  ConnectionPool.singleton(url, sqlUser, sqlPass)

  before {
    // initialize the test database
    DB.localTx { implicit session =>
      sql"DROP ALL OBJECTS".execute().apply()
      SQL(Source.fromResource("test-db.sql").getLines().mkString(" "))
        .execute()
        .apply()
    }
  }

  test("insert and reload submissions") {
    val id = 114514L
    val userId = "kenkoooo"
    val problemId = "arc999_a"

    val store = new SqlDataStore(url, sqlUser, sqlPass, driver)
    store.batchInsert(
      Seq(
        Submission(
          id = id,
          epochSecond = System.currentTimeMillis(),
          problemId = "old information...",
          userId = userId,
          language = "Rust (1.21.0)",
          point = 100,
          length = 200,
          result = "WA",
          executionTime = None
        )
      ),
      Submission
    )
    store.batchInsert(
      Seq(
        Submission(
          id = id,
          epochSecond = System.currentTimeMillis(),
          problemId = problemId,
          userId = userId,
          language = "Rust (1.21.0)",
          point = 100,
          length = 200,
          result = "WA",
          executionTime = None
        )
      ),
      Submission
    )
    store.reloadRecords()

    val submission = store.submissions(id)
    submission.id shouldBe id
    submission.problemId shouldBe problemId
    submission.userId shouldBe userId
  }

  test("insert and reload contests") {
    val id = "arc999"
    val store = new SqlDataStore(url, sqlUser, sqlPass, driver)
    store.batchInsert(Seq(Contest(id, 123456789, 987654321)), Contest)
    store.batchInsert(Seq(Contest(id, 123456789, 987654321)), Contest)
    store.reloadRecords()

    val contest = store.contests(id)
    contest.id shouldBe id
  }

  test("insert and reload problems") {
    val id = "arc999_d"
    val store = new SqlDataStore(url, sqlUser, sqlPass, driver)
    store.batchInsert(Seq(Problem(id, "arc999", "A+B Problem")), Problem)
    store.batchInsert(Seq(Problem(id, "arc999", "A+B Problem")), Problem)
    store.reloadRecords()

    val problem = store.problems(id)
    problem.id shouldBe id
  }
}