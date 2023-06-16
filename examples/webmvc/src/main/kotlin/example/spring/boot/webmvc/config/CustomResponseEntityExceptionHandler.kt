package example.spring.boot.webmvc.config

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * This custom [ResponseEntityExceptionHandler] extends Spring's default behaviour regarding the handling of
 * [ProblemDetail] responses by adding the request's trace ID if one is available.
 */
@ControllerAdvice
class CustomResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    override fun createResponseEntity(
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val traceId = request.getHeader("X-Trace-ID")
        if (body is ProblemDetail && traceId != null) {
            body.setProperty("traceId", traceId)
        }
        return super.createResponseEntity(body, headers, statusCode, request)
    }
}
