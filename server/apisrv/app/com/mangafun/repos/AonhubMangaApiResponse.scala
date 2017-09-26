package com.mangafun.repos

import play.api.libs.json._

case class AonhubSearchData(
    var id: Int,
    var rank: String,
    var name: String,
    var categories: String,
    var author: String,
    var image: String,      // image [image-url]
    var status: String
) {
}

case class AonhubMangaEntry(
    var rank: String,        // rank
    var name: String,        // name
    var categories: String,  // categories
    var author: String,      // author
    var image: String,       // image [image-url]
    var status: String,      // status
    var des: String,          // des [description text]
    var chapters: List[AonhubChapterEntry]  // chapters
) {
  /** eg: request: http://mr.aonhub.com/apiv1/1
   *  
   		{
				"id": 2940,
				"rank": "2940",
				"name": "I\"S",
      	"categories": "",
      	"author": "",
      	"image": "http:\/\/thumb.imdbtop.in\/2,52d28db3c906",
      	"status": "1"
			}, { ... }, { ... }, ...
			
			then request: http://mr.aonhub.com/apiv1/1/2940
			
			{
      	"rank": "2940",
      	"categories": "",
      	"author": "",
      	"name": "I\"S",
      	"image": "http:\/\/thumb.imdbtop.in\/2,52d28db3c906",
      	"des": "Shy Ichitaka has a crush on his high school classmate Iori, but ever since she posed for semi-provocative swimsuit photos in a magazine, she's had a lot of sleazy guys hitting on her. Ichitaka's afraid to make his feelings known for fear Iori will think he's just another creep.",
      	"status": "1",
      	"chapters": [{
      		"name": "143",
      		"id": "http:\/\/mr.aonhub.com\/apiv1\/1\/2940?cid=143"
      	}, {		
      			"name": "1",
						"id": "http:\/\/mr.aonhub.com\/apiv1\/1\/2940?cid=1"
      	}...]
      	
      	then request: http://mr.aonhub.com/apiv1/1/2940?cid=1
      	result: ["http:\/\/i2.mangapanda.com\/is\/113\/is-1262168.jpg","http:\/\/i2.mangapanda.com\/is\/113\/is-1262169.jpg","http:\/\/i2.mangapanda.com\/is\/113\/is-1262170.jpg","http:\/\/i4.mangapanda.com\/is\/113\/is-1262171.jpg","http:\/\/i4.mangapanda.com\/is\/113\/is-1262172.jpg","http:\/\/i8.mangapanda.com\/is\/113\/is-1262173.jpg","http:\/\/i10.mangapanda.com\/is\/113\/is-1262174.jpg","http:\/\/i10.mangapanda.com\/is\/113\/is-1262175.jpg","http:\/\/i4.mangapanda.com\/is\/113\/is-1262176.jpg","http:\/\/i10.mangapanda.com\/is\/113\/is-1262177.jpg","http:\/\/i10.mangapanda.com\/is\/113\/is-1262178.jpg","http:\/\/i8.mangapanda.com\/is\/113\/is-1262179.jpg","http:\/\/i10.mangapanda.com\/is\/113\/is-1262180.jpg","http:\/\/i6.mangapanda.com\/is\/113\/is-1262181.jpg","http:\/\/i4.mangapanda.com\/is\/113\/is-1262182.jpg"]
   */
}

case class AonhubChapterEntry(
    var name: String,         // name
    var id: String            // id [chapter-url]
) {
}

object AonhubSearchData {
  import play.api.libs.json.Json
  implicit val aonhubSearchDataJson = Json.format[AonhubSearchData]
}

object AonhubChapterEntry {
  import play.api.libs.json.Json
  implicit val aonhubChapterEntryJson = Json.format[AonhubChapterEntry]
}

object AonhubMangaEntry {
  import play.api.libs.json.Json
  implicit val aonhubMangaEntryJson = Json.format[AonhubMangaEntry]
}