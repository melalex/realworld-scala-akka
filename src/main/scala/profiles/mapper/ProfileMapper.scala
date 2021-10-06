package com.melalex.realworld
package profiles.mapper

import profiles.dto.ProfileDto
import profiles.model.Profile

class ProfileMapper {

  def map(source: Profile): ProfileDto = ProfileDto(
    ProfileDto.Body(
      username = source.username.value,
      bio = source.bio,
      image = source.image,
      following = source.following
    )
  )
}
