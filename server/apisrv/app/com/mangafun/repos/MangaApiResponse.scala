package com.mangafun.repos

import play.api.libs.json._

case class ResultSearchResponse(
    var searchTerm: String,
    var resultCount: Int,
    var resultPageCount: Int,
    var results: List[ResultSearchInfo]
)

case class ResultSearchInfo(
    var resultName: String,
    var resultUrl: String,
    var resultFullUrl: String,
    var resultThumbImageUrl: String,
    var resultChapters: String,
    var resultType: String, 
    var resultGenre: String
)

object ResultSearchInfo {
  import play.api.libs.json.Json
  implicit val resultSearchInfo = Json.format[ResultSearchInfo]
}

object ResultSearchResponse {
  import play.api.libs.json.Json
  implicit val resultSearchResponse = Json.format[ResultSearchResponse]
}

case class ResultComicResponse(
    var comicUrl: String,
    var chapterCount: Int,
    var chapters: List[ResultComicInfo]
)

case class ResultComicInfo(
    var chapterUrl: String,
    var chapterFullUrl: String,
    var chapterTitle: String,
    var chapterDescription: Option[String],
    var chapterDate: String
)

object ResultComicInfo {
  import play.api.libs.json.Json
  implicit val resultComicInfo = Json.format[ResultComicInfo]
}

object ResultComicResponse {
  import play.api.libs.json.Json
  implicit val resultComicResponse = Json.format[ResultComicResponse]
}

case class ResultChapterResponse(
    var chapterUrl: String,
    var pageCount: Int,
    var pages: List[ResultChapterInfo]
)

case class ResultChapterInfo(
    var pageNumber: String,
    var pageUrl: String,
    var pageFullUrl: String
)

object ResultChapterInfo {
  import play.api.libs.json.Json
  implicit val resultChapterInfo = Json.format[ResultChapterInfo]
}

object ResultChapterResponse {
  import play.api.libs.json.Json
  implicit val resultChapterResponse = Json.format[ResultChapterResponse]
}

case class ResultPageResponse(
    var pageUrl: String,
    var pageImage: ResultPageInfo
)

case class ResultPageInfo(
    var imageWidth: String,
    var imageHeight: String,
    var imageSource: String,
    var imageAlt: String
)

object ResultPageInfo {
  import play.api.libs.json.Json
  implicit val resultPageInfo = Json.format[ResultPageInfo]
}

object ResultPageResponse {
  import play.api.libs.json.Json
  implicit val resultPageResponse = Json.format[ResultPageResponse]
}