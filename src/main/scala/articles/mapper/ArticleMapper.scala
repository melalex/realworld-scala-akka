package com.melalex.realworld
package articles.mapper

import articles.dto._
import articles.model.{CreateArticleParams, SavedArticle, UpdateArticleParams}
import profiles.mapper.ProfileMapper

import java.time.ZoneId

class ArticleMapper(
    profileMapper: ProfileMapper,
    zoneId: ZoneId
) {

  def map(source: SavedArticle): SingleArticleDto = SingleArticleDto(mapArticleInternal(source))

  def map(source: Seq[SavedArticle]): MultipleArticlesDto = MultipleArticlesDto(source.map(mapArticleInternal))

  def map(source: CreateArticleParamsDto): CreateArticleParams = CreateArticleParams(
    tittle = source.tittle,
    description = source.description,
    body = source.body,
    tags = source.tags.getOrElse(Seq.empty).toSet
  )

  def map(source: UpdateArticleParamsDto): UpdateArticleParams = UpdateArticleParams(
    tittle = source.tittle,
    description = source.description,
    body = source.body,
    tags = source.tags.map(_.toSet)
  )

  private def mapArticleInternal(source: SavedArticle): ArticleDto = ArticleDto(
    slug = source.slug.value,
    tittle = source.tittle,
    description = source.description,
    body = source.body,
    tagList = source.tags.map(_.value).toList,
    createdAt = source.createdAt.atZone(zoneId),
    updatedAt = source.updatedAt.atZone(zoneId),
    favorite = source.favorite,
    favoritesCount = source.favoritesCount,
    author = profileMapper.map(source.author)
  )
}
