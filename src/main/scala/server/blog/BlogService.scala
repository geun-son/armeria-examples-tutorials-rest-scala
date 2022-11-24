package server.blog

import scala.collection.mutable.Map as MMap

import com.linecorp.armeria.server.annotation.Post
import com.linecorp.armeria.server.annotation.RequestConverter
import com.linecorp.armeria.server.annotation.Get
import com.linecorp.armeria.server.annotation.Param
import com.linecorp.armeria.server.annotation.Default
import com.linecorp.armeria.server.annotation.ProducesJson
import com.linecorp.armeria.server.annotation.Put
import com.linecorp.armeria.server.annotation.RequestObject
import com.linecorp.armeria.server.annotation.Delete
import com.linecorp.armeria.server.annotation.ExceptionHandler
import com.linecorp.armeria.server.annotation.Blocking
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus

/**
 * Need to use CollectionConverters to convert Java ConcurrentHashMap to mutable.Map
 *
 * @param blogPosts
 */
case class BlogService(blogPosts: MMap[Int, BlogPost] = scala.collection.concurrent.TrieMap[Int, BlogPost]()):

   @Post("/blogs")
   @RequestConverter(classOf[BlogPostRequestConverter])
   def createBlogPost(blogPost: BlogPost): HttpResponse =
      blogPosts.put(blogPost.id, blogPost)
      HttpResponse.ofJson(blogPost)

   @Get("/blogs/:id")
   def getBlogPost(@Param id: Int): HttpResponse =
      val blogPost = blogPosts.get(id)
      HttpResponse.ofJson(blogPost)

   @Get("/blogs")
   @ProducesJson
   def getBlogPosts(@Param @Default("true") descending: Boolean): Iterable[BlogPost] =
      if descending then
      blogPosts.toList.sortBy(_._1).reverse.map(_._2)
      else
      blogPosts.values

   @Put("/blogs/:id")
   def updateBlogPost(@Param id: Int, @RequestObject blogPost: BlogPost) =
      val oldBlogPost = blogPosts.get(id).orNull
      if oldBlogPost == null then
         HttpResponse.of(HttpStatus.NOT_FOUND)
      else
         val newBlogPost = blogPost.copy(id = id, createAt = oldBlogPost.createAt)
         blogPosts.put(id, newBlogPost)
         HttpResponse.ofJson(newBlogPost)

   @Blocking
   @Delete("/blogs/:id")
   @ExceptionHandler(classOf[BadRequestExceptionHandler])
   def deleteBlogPost(@Param id: Int): HttpResponse =
      val removed = blogPosts.remove(id).orNull
      HttpResponse.of(HttpStatus.NO_CONTENT)