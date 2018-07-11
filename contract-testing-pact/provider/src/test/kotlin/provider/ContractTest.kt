package provider

import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.willReturn
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import provider.core.Movie
import provider.core.MovieDataStore
import provider.core.MovieRecord
import java.util.*


@Provider("provider")
@PactFolder("src/test/pacts")
@VerificationReports("console")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension::class, PactVerificationInvocationContextProvider::class)
internal class ContractTest {

    @MockBean lateinit var dataStore: MovieDataStore

    @BeforeEach fun setTarget(context: PactVerificationContext, @LocalServerPort port: Int) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate fun `consumer contract tests`(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("Getting movie with any ID returns Iron Man")
    fun anyMovieByIdRequestReturnsIronMan() {
        val ironManRecord = MovieRecord(UUID.randomUUID(), Movie(
                title = "Iron Man",
                description = "Lorem Ipsum ...",
                releaseYear = 2008,
                imdbScore = 7.9f,
                metacriticScore = 0.79f
        ))
        given { dataStore.getById(any()) } willReturn { ironManRecord }
    }

}