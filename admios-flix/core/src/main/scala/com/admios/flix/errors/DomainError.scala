package com.admios.flix.errors

import cats.data.{Validated, ValidatedNel}
import cats.syntax.validated._
import cats.{ApplicativeError, Semigroup}

abstract class DomainError(val message: MessageError) extends Exception(message.text)

object DomainError {

  implicit val deSemigroup: Semigroup[DomainError] = {
    def toList(err: DomainError): List[DomainError] = err match {
      case ErrorCollector(errors) => errors
      case other => other :: Nil
    }
    (e: DomainError, e1: DomainError) => ErrorCollector(toList(e) ::: toList(e1))
  }

  final implicit class DomainErrorOps[A](validated: ValidatedNel[DomainError, A]) {
    def domainErrorFlatten: Validated[DomainError, A] = validated.leftMap(_.reduce)
    def domainErrorLift[F[_]: ApplicativeError[*[_], Throwable]]: F[A] = domainErrorFlatten.liftTo[F]
  }
}

final case class ErrorCollector(errors: List[DomainError])
    extends DomainError(
      MessageError(errors.map(_.message.text).mkString(", "))
    )
