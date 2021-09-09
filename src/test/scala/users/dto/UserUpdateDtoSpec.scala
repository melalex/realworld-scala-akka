package com.melalex.realworld
package users.dto

import commons.errors.model.RealWorldError.{AboveMax, BelowMin, RegExMismatch}
import commons.validation.RealWorldValidation
import fixture.{RealWorldSpec, UserFixture}
import users.model.User

import cats.data.Validated.{Valid, invalidNec}

class UserUpdateDtoSpec extends RealWorldSpec with RealWorldValidation with UserFixture {

  "UserUpdateDto.formValidation" should "return itself for valid UserUpdateDto" in {
    validationResult(userUpdateDto) shouldBe Valid(userUpdateDto)
  }

  it should "return Invalid when email doesn't match pattern" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        email = "a"
      )
    )

    validationResult(target) shouldBe invalidNec(RegExMismatch(User.Email.messageKey, RealWorldValidation.EmailRegEx))
  }

  it should "return Invalid when bio is bigger than 250" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        bio = Some("a" * 251)
      )
    )

    validationResult(target) shouldBe invalidNec(AboveMax(User.Bio.messageKey, 250))
  }

  it should "return Invalid when image is bigger than 250" in {
    val target = userUpdateDto.copy(
      user = userUpdateDto.user.copy(
        image = Some("a" * 401)
      )
    )

    validationResult(target) shouldBe invalidNec(AboveMax(User.Image.messageKey, 400))
  }
}
