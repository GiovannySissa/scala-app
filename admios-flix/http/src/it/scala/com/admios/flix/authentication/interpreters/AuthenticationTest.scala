package com.admios.flix.authentication.interpreters

import cats.Eq
import cats.effect.{IO, Resource}
import com.admios.flix.authentication._
import com.admios.flix.config.AppConfig
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.log4cats._
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import io.chrisdavenport.fuuid.FUUID
import io.circe.config.parser
import mouse.boolean._
import pdi.jwt.JwtClaim
import suites.ResourceSuite

class AuthenticationTest extends ResourceSuite[RedisCommands[IO, String, String]] {

  import com.admios.flix.authentication.gen.UsersGens._

  val configIO = parser.decodePathF[IO, AppConfig]("app")

  val Max: PropertyCheckConfigParam = minSuccessful(1)

  override def resources: Resource[IO, RedisCommands[IO, String, String]] =
    Redis[IO].utf8("redis://localhost")

  val jwtClaim = JwtClaim("test")

  withResource { redis =>
    test("Authentication flow") {
      forAll(Max) { (userName: UserName, userName2: UserName, password: Password) =>
        val check = for {
          conf                          <- configIO
          implicit0(tokens: Tokens[IO]) <- LiveTokens.make[IO](conf.http.authentication)
          implicit0(users: Users[IO])   <- TestUsers.apply(userName2)
          auth                          <- LiveAuthentication.make[IO](redis)(conf.http.authentication).use(IO.pure)
          usersAuth                     <- LiveUsersAuth.make[IO](redis).use(IO.pure)
          notFound                      <- usersAuth.find(JwtToken("failure"))(jwtClaim)
          tokenUser                     <- auth.addUser(userName, password)
          login                         <- auth.login(userName2, password).attempt
          _                             <- auth.logout(tokenUser, userName)
        } yield {
          assert(notFound.isEmpty)
          assert(login.isRight)
        }

        check.void.unsafeRunSync()
      }
    }
  }

}

final class TestUsers private (un: UserName) extends Users[IO] {
  implicit val eq: Eq[UserName] = Eq.fromUniversalEquals

  override def find(userName: UserName, password: Password): IO[Option[User]] =
    IO(Eq[UserName].eqv(userName, un).fold(User(FUUID.randomFUUID[IO].unsafeRunSync(), userName.value).toOption, None))

  override def create(userName: UserName, password: Password): IO[UserId] =
    FUUID.randomFUUID[IO].map {
      UserId
    }
}

object TestUsers {

  def apply(un: UserName): IO[Users[IO]] =
    IO.pure(new TestUsers(un))
}
