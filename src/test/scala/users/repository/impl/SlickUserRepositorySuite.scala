package com.melalex.realworld
package users.repository.impl

import commons.model.ModelId
import test.fixture.UserFixture
import test.spec.RepositorySpec

class SlickUserRepositorySuite extends RepositorySpec with UserFixture {

  private val slickUserRepository = new SlickUserRepository()

  override protected def beforeEach(): Unit = setUp(slickUserRepository)
  override protected def afterEach(): Unit  = tearDown(slickUserRepository)

  "save" should "create new user when UnSavedUser is provided" in {
    val saveAndFind = slickUserRepository
      .save(unsavedUser)
      .map(_.id)
      .flatMap(slickUserRepository.findById)
      .map(_.value)

    whenReady(dbInterpreter.executeTransitionally(saveAndFind)) { it =>
      it.id should not be ModelId.UnSaved
      it shouldEqual unsavedUser.asSaved(it.id)
    }
  }

  it should "update existing user when SavedUser is provided" in {
    val newBio = "New test bio"

    val saveUpdateAndFind = slickUserRepository
      .save(unsavedUser)
      .map(_.copy(bio = Some(newBio)))
      .flatMap(slickUserRepository.save)
      .map(_.id)
      .flatMap(slickUserRepository.findById)
      .map(_.value)

    whenReady(dbInterpreter.executeTransitionally(saveUpdateAndFind)) { it =>
      it.bio should not be UserFixture.Bio
      it.bio shouldEqual Some(newBio)
    }
  }

  "findById" should "return None when invalid user id is provided" in {
    whenReady(dbInterpreter.execute(slickUserRepository.findById(ModelId.UnSaved))) {
      _ shouldEqual None
    }
  }

  "findByEmail" should "return user" in {
    val saveAndFind = slickUserRepository
      .save(unsavedUser)
      .map(_.email)
      .flatMap(slickUserRepository.findByEmail)
      .map(_.value)

    whenReady(dbInterpreter.executeTransitionally(saveAndFind)) { it =>
      it shouldEqual unsavedUser.asSaved(it.id)
    }
  }

  it should "return None when no user with given email is present" in {
    whenReady(dbInterpreter.execute(slickUserRepository.findByEmail("invalid@invalid.com"))) {
      _ shouldEqual None
    }
  }
}
