package ru.johnspade.logins

import java.time.LocalDateTime

import org.scalatest.{FunSuite, Matchers}
import ru.johnspade.logins.Login.frmt
import ru.johnspade.logins.Serie._

import scala.concurrent.duration._

class SerieTest extends FunSuite with Matchers {

  private val stop = LocalDateTime.parse("2015-11-30 23:15:44", frmt)
  private val login = Login("TobiNator2", "89.245.249.24", LocalDateTime.parse("2015-11-30 23:14:17", frmt))

  private val serie = Serie(
    "89.245.249.24",
    LocalDateTime.parse("2015-11-30 23:14:17", frmt),
    stop,
    Vector(
      login,
      Login("TobiNator", "89.245.249.24", LocalDateTime.parse("2015-11-30 23:14:50", frmt)),
      Login("TobiNator2", "89.245.249.24", LocalDateTime.parse("2015-11-30 23:15:34", frmt)),
      Login("TobiNator2", "89.245.249.24", LocalDateTime.parse("2015-11-30 23:15:44", frmt))
    )
  )

  test("print serie") {
    val result = serie.print()
    result shouldBe
      """"89.245.249.24","2015-11-30 23:14:17","2015-11-30 23:15:44","TobiNator2:2015-11-30 23:14:17,TobiNator:2015-11-30 23:14:50,TobiNator2:2015-11-30 23:15:34,TobiNator2:2015-11-30 23:15:44""""
  }

  test("login should be in serie") {
    val result = isInSerie(serie, stop, 1.hour)
    result shouldBe true
  }

  test("login should not be in serie") {
    val result = isInSerie(serie, stop.plusHours(1), 1.hour)
    result shouldBe false
  }

  test("find serie for login") {
    val newLogin = login.copy(timestamp = login.timestamp.plusSeconds(1))
    val result = findSerieForLogin(Vector(serie), newLogin, 1.hour)
    result shouldBe Some(serie)
  }

  test("login not in serie") {
    val newLogin = login.copy(timestamp = login.timestamp.plusHours(2))
    val result = findSerieForLogin(Vector(serie), newLogin, 1.hour)
    result shouldBe None
  }

  test("find finished with stopped window") {
    val result = findFinished(Vector(serie), serie.start.plusHours(2), 1.hour)
    result should have size 1
  }

  test("find finished with opened window") {
    val result = findFinished(Vector(serie), serie.start, 1.hour)
    result shouldBe empty
  }

}
