package ru.johnspade.logins

import java.time.LocalDateTime

import org.scalatest.{FunSuite, Matchers}

class LoginTest extends FunSuite with Matchers {

  private val login = Login("TheRealJJ", "77.92.76.250", LocalDateTime.parse("2015-11-30 23:11:40", Login.frmt))

  test("parse login from string") {
    val result = Login.parse(""""TheRealJJ","77.92.76.250","2015-11-30 23:11:40"""")
    result shouldBe login
  }

  test("print login") {
    val result = login.print()
    result shouldBe "TheRealJJ:2015-11-30 23:11:40"
  }

}
