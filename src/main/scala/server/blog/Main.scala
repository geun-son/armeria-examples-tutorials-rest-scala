package server.blog

import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.server.Server
import com.linecorp.armeria.server.docs.DocService

import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("BlogServer")

@main def start = {
  val server = newServer(8080)
  server.start().join()
  logger.info("Server has been started. Serving DocService at http://127.0.0.1:8080/docs", server.activeLocalPort());
}

private val docService =
 DocService
   .builder()
   .exampleRequests(BlogService.getClass, "createBlogPost", "{\"title\":\"My first blog\", \"content\":\"Hello Armeria!\"}")
   .build()

private def newServer(port: Int): Server =
 Server
   .builder()
   .http(port)
   //.service("/", (ctx, req) => HttpResponse.of("Hello, Armeria!"))
   .annotatedService(BlogService())
   .serviceUnder("/docs", docService)  // Add Documentation service
   .build()
