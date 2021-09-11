package com.melalex.realworld
package users.model

import commons.model.ModelId

case class UserToUserRelation(
    followerId: ModelId,
    followingId: ModelId
)
