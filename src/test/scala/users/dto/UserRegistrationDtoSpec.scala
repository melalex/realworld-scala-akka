package com.melalex.realworld
package users.dto

import commons.errors.model.RealWorldError.{AboveMax, BelowMin, RegExMismatch}
import commons.validation.RealWorldValidation
import fixture.{RealWorldSpec, UserFixture}
import users.model.User

import cats.data.Validated.{Valid, invalidNec}

class UserRegistrationDtoSpec extends RealWorldSpec with RealWorldValidation with UserFixture {

  "UserRegistrationDto.formValidation" should "return itself for valid UserRegistrationDto" in {
    validationResult(userRegistrationDto) shouldBe Valid(userRegistrationDto)
  }

  it should "return Invalid when username is less than 4" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        username = "a"
      )
    )

    validationResult(target) shouldBe invalidNec(BelowMin(User.Username.messageKey, 4))
  }

  it should "return Invalid when username is bigger than 40" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        username = "a" * 41
      )
    )

    validationResult(target) shouldBe invalidNec(AboveMax(User.Username.messageKey, 40))
  }

  it should "return Invalid when username is not alphanumeric" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        username = "$" * 6
      )
    )

    validationResult(target) shouldBe invalidNec(RegExMismatch(User.Username.messageKey, RealWorldValidation.AlphaNumericRegEx))
  }

  it should "return Invalid when email doesn't match pattern" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        email = "a"
      )
    )

    validationResult(target) shouldBe invalidNec(RegExMismatch(User.Email.messageKey, RealWorldValidation.EmailRegEx))
  }

  it should "return Invalid when password is less than 6" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        password = "a"
      )
    )

    validationResult(target) shouldBe invalidNec(BelowMin(User.Password.messageKey, 6))
  }

  it should "return Invalid when password is bigger than 40" in {
    val target = userRegistrationDto.copy(
      user = userRegistrationDto.user.copy(
        password = "a" * 41
      )
    )

    validationResult(target) shouldBe invalidNec(AboveMax(User.Password.messageKey, 40))
  }
}
