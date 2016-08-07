package com.github.funprog.funbot

import scala.util.Random

class DiceBot(val random: Random) {
  private lazy val regex = """(?i)\s*roll\s+(\d+)([d|n])(\d+)\s*""".r
  private lazy val regexLotto = """(?i)\s*roll\s+(\d+)(lotto)\s*""".r
  private lazy val regexInteger = """(?i)(\d+)\s*""".r


  /** Processes an `input` to return sequential string values
    * representing order.
    *
    * @param input consists of the 'roll' word and two numbers concatenated
    *              using the 'n' or 'd' character. (eg. "roll 2d4")
    *              The two numbers represent the number of rolling and
    *              the number of dice dimensions(inclusive) respectively.
    * @return returns `None` if the `input` is invalid, otherwise `Some[String]`
    *         representing the order.
    */
  def process(input: String): Option[String] = {
    val (valid, count, method, faceOption) = parseInput(input)
    if (valid) {
      val dice = makeDiceFace(faceOption)
      val result = method.toLowerCase match {
        case "d" => rollNormalDice(dice, count)
        case "n" => rollNonDupDice(dice, count)
        case "lotto" => rollForNewLife(count, List.empty)
      }
      Some(result.mkString(", "))
    } else {
      None
    }
  }

  private def parseInput(input: String): (Boolean, Int, String, String) = input match {
    case regexLotto(count, method) => (true, count.toInt, method, "")
    case regex(count, method, max) => (true, count.toInt, method, max)
    case _ => (false, 0, "", "")
  }

  private def makeDiceFace(option: String): List[String] = option match {
    case regexInteger(str) => (1 to str.toInt).toList.map(_.toString)
    case _ => (1 to 6).toList.map(_.toString)
  }

  private def rollNormalDice(dice: List[String], count: Int): List[String] = {
    (1 to count).map(_ => dice(random.nextInt(dice.length))).toList
  }

  private def rollNonDupDice(dice: List[String], count: Int): List[String] = {
    random.shuffle(dice).take(count)
  }
  private def rollForNewLife(count: Int, result: List[String]): List[String] = {
    if (count == 0) {
      result
    } else {
      val `newLife and bonus` = random.shuffle((1 to 45).toList.map(_.toString)).take(7)
      val bonus = `newLife and bonus`.head
      val newLifeNumber = `newLife and bonus`.tail.mkString(" ")
      rollForNewLife(count - 1, result ::: List(s"\n[$newLifeNumber] and bonus(#$bonus)"))
    }
  }
}
