package ru.johnspade.logins

import java.io.{File, FileNotFoundException, PrintWriter}
import java.nio.file.{FileAlreadyExistsException, Files, Paths}

import org.rogach.scallop.{ScallopConf, ScallopOption}
import ru.johnspade.logins.Serie._

import scala.concurrent.duration.Duration
import scala.io.Source

object Main extends App {
  val conf = new Conf(args)
  if (Files.notExists(Paths.get(conf.input())))
    throw new FileNotFoundException(s"Input file ${conf.input()} not found")
  if (Files.exists(Paths.get(conf.output())))
    throw new FileAlreadyExistsException(s"Output file ${conf.output()} already exists")
  val window = Duration(conf.window())
  val source = Source.fromFile(conf.input())
  val pw = new PrintWriter(new File(conf.output()))
  pw.println("IP address,Start,Stop,Users")
  val left = source.getLines().foldLeft(Vector.empty[Serie]) { (series, line) =>
    val login = Login.parse(line)
    val serie = findSerieForLogin(series, login, window)
    // Обновляем серию, которой принадлежит логин, или создаем новую
    val serieUpd = serie.map(s => Serie(s.ip, s.start, login.timestamp, s.logins :+ login))
      .getOrElse(Serie(login.ip, login.timestamp, login.timestamp, Vector(login)))
    if (login.ip == "0.0.0.0") // Так как логины с IP 0.0.0.0 не упорядочены по времени, не ищем завершенные серии
      series.filterNot(s => serie.contains(s)) :+ serieUpd
    else {
      val finished = findFinished(series, login.timestamp, window)
      writeToFile(finished, pw)
      // Дальше для анализа передаются только еще не завершенные (не записанные в файл) серии
      series.filterNot(s => serie.contains(s) || finished.contains(s)) :+ serieUpd
    }
  }
  writeToFile(left, pw)
  source.close()
  pw.close()
  println(s"Results: ${conf.output()}")
}

class Conf(args: Seq[String]) extends ScallopConf(args) {
  val input: ScallopOption[String] = opt[String](default = Some("logins0.csv"))
  val output: ScallopOption[String] = opt[String](default = Some("result.csv"))
  val window: ScallopOption[String] = opt[String](default = Some("1 hour"))
  verify()
}
