package suites

import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait TestSuite extends AnyFunSuite with ScalaCheckDrivenPropertyChecks
