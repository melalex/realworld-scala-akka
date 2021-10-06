package com.melalex.realworld
package comments.mapper

import comments.dto.{CommentDto, MultipleCommentsDto, SingleCommentDto}
import comments.model.SavedComment
import profiles.mapper.ProfileMapper

import java.time.ZoneId

class CommentMapper(
    zoneId: ZoneId,
    profileMapper: ProfileMapper
) {

  def map(source: SavedComment): SingleCommentDto = SingleCommentDto(mapInternal(source))

  def map(source: Seq[SavedComment]): MultipleCommentsDto = MultipleCommentsDto(source.map(mapInternal))

  private def mapInternal(source: SavedComment) = CommentDto(
    id = source.id.value,
    createdAt = source.createdAt.atZone(zoneId),
    updatedAt = source.updatedAt.atZone(zoneId),
    body = source.body,
    author = profileMapper.map(source.author)
  )
}
