package com.melalex.realworld
package users.repository.impl

import commons.model.ModelId
import fixture.{DatabaseTestKit, UserFixture}

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class SlickUserRepositorySpec
    extends AnyWordSpec
    with ScalaFutures
    with Matchers
    with OptionValues
    with DatabaseTestKit
    with BeforeAndAfterEach {

  implicit val customPatience: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))

  private val slickUserRepository = new SlickUserRepository()

  override protected def beforeEach(): Unit = {
    Await.ready(dbBootstrap.init(slickUserRepository), 5.seconds)
    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    Await.ready(dbBootstrap.drop(slickUserRepository), 5.seconds)
  }

  "save" when {

    "is called on UnSavedUser" should {

      "create new user" in new UserFixture {
        val saveAndFind = slickUserRepository
          .save(unSaved)
          .map(_.id)
          .flatMap(slickUserRepository.findById)
          .map(_.value)

        whenReady(dbInterpreter.executeTransitionally(saveAndFind)) { it =>
          it.id should not be ModelId.UnSaved
          it shouldBe unSaved.asSaved(it.id)
        }
      }
    }

    "is called on SavedUser" should {

      "update existing user" in new UserFixture {
        val newBio = "New test bio"

        val saveUpdateAndFind = slickUserRepository
          .save(unSaved)
          .map(_.copy(bio = Some(newBio)))
          .flatMap(slickUserRepository.save)
          .map(_.id)
          .flatMap(slickUserRepository.findById)
          .map(_.value)

        whenReady(dbInterpreter.executeTransitionally(saveUpdateAndFind)) { it =>
          it.bio should not be UserFixture.Bio
          it.bio shouldBe Some(newBio)
        }
      }
    }
  }

  "findById" when {

    "search by invalid id" should {

      "return None" in {
        whenReady(dbInterpreter.execute(slickUserRepository.findById(ModelId.UnSaved))) {
          _ shouldBe None
        }
      }
    }
  }

  "findByEmail" when {

    "when search by valid email" should {

      "return user" in new UserFixture {
        val saveAndFind = slickUserRepository
          .save(unSaved)
          .map(_.email)
          .flatMap(slickUserRepository.findByEmail)
          .map(_.value)

        whenReady(dbInterpreter.executeTransitionally(saveAndFind)) { it =>
          it shouldBe unSaved.asSaved(it.id)
        }
      }
    }

    "search by invalid email" should {

      "return None" in {
        whenReady(dbInterpreter.execute(slickUserRepository.findByEmail("invalid@invalid.com"))) {
          _ shouldBe None
        }
      }
    }
  }
}
