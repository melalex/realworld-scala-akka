package com.melalex.realworld
package users.mapper

import fixture.{RealWorldSpec, UserFixture}

class UserConversionsSpec extends RealWorldSpec with UserFixture {

  "UserConversions" should "map UserRegistrationDto" in {
    UserConversions.toNewUser(userRegistrationDto) shouldBe newUser
  }

  it should "map UserUpdateDto" in {
    UserConversions.toUserUpdate(userUpdateDto) shouldBe updateUser
  }

  it should "map UserWithToken" in {
    UserConversions.toUserDto(userWithToken) shouldBe savedUserDto
  }
}
