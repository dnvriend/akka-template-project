/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend

import akka.actor._
import akka.event.{ Logging, LoggingAdapter }
import akka.stream.scaladsl.Source
import akka.stream.{ ActorMaterializer, Materializer }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ GivenWhenThen, BeforeAndAfterAll, FlatSpec, Matchers }

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait TestSpec extends FlatSpec with Matchers with ScalaFutures with BeforeAndAfterAll with GivenWhenThen {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val mat: Materializer = ActorMaterializer()
  implicit val log: LoggingAdapter = Logging(system, this.getClass)
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 50.seconds)

  implicit class FutureToTry[T](f: Future[T]) {
    def toTry: Try[T] = Try(f.futureValue)
  }

  /**
   * Returns a Source[Int, Unit]
   */
  def withIterator[T](start: Int = 0)(f: Source[Int, Unit] ⇒ T): T =
    f(Source.fromIterator(() ⇒ Iterator from start))

  override protected def afterAll(): Unit = {
    system.terminate()
    system.whenTerminated.toTry should be a 'success
  }
}
