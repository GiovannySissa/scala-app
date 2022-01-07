package com.admios.flix.authentication.gen

import com.admios.flix.authentication.{Password, UserName}
import org.scalacheck.{Arbitrary, Gen}

// Define users generators
object UsersGens {

  def userNameGen: Gen[UserName] =
    for {
      n  <- Gen.choose(3, 50)
      un <- Gen.listOfN(n, Gen.alphaChar)
    } yield UserName(un.mkString("")).toOption.getOrElse(throw new Exception("Invalid username generation"))

  def passwordGen: Gen[Password] =
    for {
      n  <- Gen.choose(3, 50)
      pw <- Gen.listOfN(n, Gen.alphaChar)
    } yield Password(pw.mkString("")).getOrElse(throw new Exception("Invalid password generation"))

  implicit val userArb: Arbitrary[UserName] = Arbitrary(userNameGen)
  implicit val passArb: Arbitrary[Password] = Arbitrary(passwordGen)

}
