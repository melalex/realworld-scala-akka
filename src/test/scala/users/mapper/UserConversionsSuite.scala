package com.melalex.realworld
package users.mapper

import test.fixture.UserFixture
import test.spec.UnitTestSpec

class UserConversionsSuite extends UnitTestSpec with UserFixture {

  "UserConversions" should "map UserRegistrationDto" in {
    UserConversions.toNewUser(userRegistrationDto) shouldEqual newUser
  }

  it should "map UserUpdateDto" in {
    UserConversions.toUserUpdate(userUpdateDto) shouldEqual updateUser
  }

  it should "map UserWithToken" in {
    UserConversions.toUserDto(userWithToken) shouldEqual savedUserDto
  }
}
