package com.melalex.realworld
package profiles.mapper

import profiles.dto.ProfileDto
import profiles.model.Profile

object ProfileConversions {

  def toDto(source: Profile): ProfileDto = ProfileDto(
    ProfileDto.Body(
      username = source.username,
      bio = source.bio,
      image = source.image,
      following = source.following
    )
  )
}
