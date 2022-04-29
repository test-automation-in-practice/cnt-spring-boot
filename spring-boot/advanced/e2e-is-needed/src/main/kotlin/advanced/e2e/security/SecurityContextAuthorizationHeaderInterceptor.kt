package advanced.e2e.security

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication

class SecurityContextAuthorizationHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val token = getTokenFromSecurityContext()
        if (token != null && isNotAlreadyAuthorized(request)) {
            request = setAuthorizationHeader(request, token)
        }

        return chain.proceed(request)
    }

    private fun getTokenFromSecurityContext(): String? {
        val authentication = SecurityContextHolder.getContext().authentication as? BearerTokenAuthentication
        return authentication?.token?.tokenValue
    }

    private fun isNotAlreadyAuthorized(request: Request): Boolean =
        request.header(HttpHeaders.AUTHORIZATION) == null

    private fun setAuthorizationHeader(request: Request, token: String): Request =
        request.newBuilder().addHeader(HttpHeaders.AUTHORIZATION, "Bearer $token").build()

}
