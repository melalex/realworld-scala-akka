package com.melalex.realworld
package users.mapper

import users.dto.{UserDto, UserRegistrationDto, UserUpdateDto}
import users.model.{NewUser, UpdateUser, UserWithToken}

class UserMapper {

  def map(source: UserRegistrationDto): NewUser = NewUser(
    username = source.user.username,
    email = source.user.email,
    password = source.user.password
  )

  def map(source: UserUpdateDto): UpdateUser = UpdateUser(
    email = source.user.email,
    bio = source.user.bio,
    image = source.user.image
  )

  def map(source: UserWithToken): UserDto = UserDto(
    UserDto.Body(
      email = source.user.email.value,
      token = source.token.value,
      username = source.user.username.value,
      bio = source.user.bio,
      image = source.user.image
    )
  )
}
