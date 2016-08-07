package com.github.funprog.funbot

import org.scalatest.FunSuite

import scala.util.Random

class DiceBotTest extends FunSuite {

  Seq(
    "invalid string",
    "",
    "roll 1d").foreach(
    input => {
      test(s"process with invalid input '$input' returns none.") {
        val expected = None
        val sut = new DiceBot(new Random)

        val actual = sut.process(input)

        assert(expected == actual)
      }
    })

  Seq(
    "roll 1d1",
    "roll 2d1",
    "roll 20000d11232",
    "roll    2d1",
    "  roll \t  2d1  ",
    "  \r\n roll \r\n\t  2d1 \r\n ",
    "roll 1n1",
    "roll 2n1",
    "roll 20000n11232",
    "roll    2n1",
    "  roll \t  2n1  ",
    "  \r\n roll \r\n\t  2n1 \r\n "
  ).foreach(
    input => {
      test(s"process with valid input '$input' returns some.") {
        val sut = new DiceBot(new Random)
        val actual = sut.process(input)
        assert(classOf[Some[String]] == actual.getClass)
      }
    })

  Seq(
    ("roll 1d1", "1"),
    ("RoLl 1d1", "1"),
    ("roLL 1D1", "1"),
    ("roll 1n1", "1"),
    ("RoLl 1n1", "1"),
    ("roLL 1N1", "1")
  ).foreach {
    case (input, result) =>
      test(s"process with input '$input' is case-insensitive.") {
        val expected = Some(result)
        val sut = new DiceBot(new Random)

        val actual = sut.process(input)

        assert(expected == actual)
      }
  }

  test("process returns correct result.") {
    // Fixture setup
    val random = new Random(1)

    val sut = new DiceBot(new Random(1))

    val orderString: String =
      (1 to 3)
        .map(_ => random.nextInt(100) + 1)
        .mkString(", ")

    val expected = Some(orderString)

    // Exercise system
    val actual = sut.process("roll 3d100")

    // Verify outcome
    assert(expected == actual)
  }

  test("process returns correct result for non-duplicate.") {
    // Fixture setup
    val random = new Random(1)

    val sut = new DiceBot(new Random(1))

    val testDice = (1 to 100).toList.map(_.toString)
    val result = random.shuffle(testDice).take(3).mkString(", ")
    val expected = Some(result)

    // Exercise system
    val actual = sut.process("roll 3n100")

    // Verify outcome
    assert(expected == actual)
  }

  Seq("roll 1lotto",
    "roll 3LOTTO",
    "roll 4Lotto",
    "roll 2loTTo").foreach(
    input => {
      test(s"process with '$input' test") {
        val dicebotRandom = new DiceBot(new Random)

        val actual = dicebotRandom.process(input)

        assert(classOf[Some[String]] == actual.getClass)
      }
    })

  Seq("roll lotto",
    "roll 3lotto1",
    "roll lotto12"
  ).foreach(
    input => {
      test(s"process with '$input' test") {
        val dicebotRandom = new DiceBot(new Random)

        val actual = dicebotRandom.process(input)

        assert(actual == None)
      }
    }
  )

}
