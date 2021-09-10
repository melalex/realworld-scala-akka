package com.melalex.realworld
package users.dto

import commons.errors.model.RealWorldError.{AboveMax, RegExMismatch}
import commons.validation.RealWorldValidation
import test.fixture.UserFixture
import test.spec.UnitTestSpec
import users.model.User

import cats.data.Validated.{Valid, invalidNec}

class UserUpdateDtoSuite extends UnitTestSpec with RealWorldValidation with UserFixture {

  "UserUpdateDto.formValidation" should "return itself for valid UserUpdateDto" in {
    validationResult(userUpdateDto) shouldEqual Valid(userUpdateDto)
  }

  it should "return Invalid when email doesn't match pattern" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        email = "a"
      )
    )

    validationResult(target) shouldEqual invalidNec(RegExMismatch(User.Email.messageKey, RealWorldValidation.EmailRegEx))
  }

  it should "return Invalid when bio is bigger than 250" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        bio = Some("a" * 251)
      )
    )

    validationResult(target) shouldEqual invalidNec(AboveMax(User.Bio.messageKey, 250))
  }

  it should "return Invalid when image is bigger than 250" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        image = Some("a" * 401)
      )
    )

    validationResult(target) shouldEqual invalidNec(AboveMax(User.Image.messageKey, 400))
  }
}
