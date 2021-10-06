package com.melalex.realworld
package users.mapper

import test.fixture.UserFixture
import test.spec.UnitTestSpec

class UserMapperSuite extends UnitTestSpec with UserFixture {

  private val userMapper = new UserMapper

  "UserConversions" should "map UserRegistrationDto" in {
    userMapper.map(userRegistrationDto) shouldEqual newUser
  }

  it should "map UserUpdateDto" in {
    userMapper.map(userUpdateDto) shouldEqual updateUser
  }

  it should "map UserWithToken" in {
    userMapper.map(userWithToken) shouldEqual savedUserDto
  }
}
