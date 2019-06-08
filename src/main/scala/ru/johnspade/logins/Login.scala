package ru.johnspade.logins

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import ru.johnspade.logins.Login.frmt

/**
  * Факт логина пользователя
  */
case class Login(username: String, ip: String, timestamp: LocalDateTime) {
  def print(): String = s"$username:${frmt.format(timestamp)}"
}

object Login {
  val frmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  /**
    * Парсит факт логина в формате CSV
    */
  def parse(s: String): Login = {
    val Array(username, ip, timestamp) = s.split(',')
    Login(username.replace("\"", ""), ip.replace("\"", ""), LocalDateTime.parse(timestamp.replace("\"", ""), frmt))
  }
}
