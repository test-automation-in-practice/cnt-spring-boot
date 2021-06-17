package contracts.two

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

arrayOf(
    contract {
        name = "get single book by id - strict"
        request {
            method = GET
            urlPath = path("/books/b3fc0be8-463e-4875-9629-67921a1e00f4")
        }
        response {
            status = OK
            headers {
                contentType = APPLICATION_JSON
            }
            body = body(
                """
                {
                  "isbn": "9780132350884",
                  "title": "Clean Code",
                  "numberOfPages": 464
                }
                """
            )
        }
    },
    contract {
        name = "get single book by id - relaxed"
        request {
            method = GET
            urlPath = path("/books/b3fc0be8-463e-4875-9629-67921a1e00f4")
        }
        response {
            status = OK
            headers {
                contentType = APPLICATION_JSON
            }
            body = body(
                "isbn" to v(c("9780132350884"), p(regex("""\d{10}|\d{13}"""))),
                "title" to v(c("Clean Code"), p(anyNonBlankString)),
                "numberOfPages" to v(c(464), p(anyNumber))
            )
        }
    }
)
