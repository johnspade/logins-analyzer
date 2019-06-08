package ru.johnspade.logins

import java.io.PrintWriter
import java.time.{Duration, LocalDateTime}

import ru.johnspade.logins.Login.frmt

import scala.concurrent.duration

/**
  * Серия множественных входов
  */
case class Serie(ip: String, start: LocalDateTime, stop: LocalDateTime, logins: Vector[Login]) {
  def print(): String = {
    val loginsStr = logins.map(_.print()).mkString(",")
    s"${'"'}$ip${'"'},${'"'}${frmt.format(start)}${'"'},${'"'}${frmt.format(stop)}${'"'},${'"'}$loginsStr${'"'}"
  }
}

object Serie {
  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = _ compareTo _

  /**
    * Проверяет, принадлежит ли время логина серии множественных входов
    * @param serie серия
    * @param timestamp момент логина
    * @param window размер временного окна
    * @return true, если принадлежит
    */
  def isInSerie(serie: Serie, timestamp: LocalDateTime, window: duration.Duration): Boolean =
    Duration.between(serie.start, timestamp).getSeconds <= window.toSeconds

  /**
    * Находит в списке серию, которой принадлежит переданный логин
    * @param series список серий
    * @param login логин
    * @param window размер временного окна
    */
  def findSerieForLogin(series: Vector[Serie], login: Login, window: duration.Duration): Option[Serie] =
    series.find(s => s.ip == login.ip && isInSerie(s, login.timestamp, window))

  /**
    * Находит в списке завершенные серии (серии, для которых интервал от начала до переданного момента времени больше,
    * чем заданный размер временного окна)
    * @param series список серий
    * @param timestamp момент логина
    * @param window размер временного окна
    */
  def findFinished(series: Vector[Serie], timestamp: LocalDateTime, window: duration.Duration): Vector[Serie] =
    series.filterNot(isInSerie(_, timestamp, window))

  /**
    * Записывает в файл в формате CSV переданный список серий
    * @param series список серий для записи
    * @param pw открытый PrintWriter
    */
  def writeToFile(series: Vector[Serie], pw: PrintWriter): Unit = {
    series.filterNot(_.logins.size == 1).sortBy(_.start).foreach(s => pw.println(s.print()))
  }
}
