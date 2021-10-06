package com.melalex.realworld
package tags.mapper

import tags.dto.MultipleTagsDto
import tags.model.ArticleTag

class TagMapper {

  def map(source: Seq[ArticleTag]): MultipleTagsDto = MultipleTagsDto(source.map(_.value))
}
