package contracts

import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

arrayOf(
    contract {
        name = "publish_clean_code"
        label = "a_create_event_for_Clean_Code_is_published"

        input {
            triggeredBy = "publishCleanCode()"
        }

        outputMessage {
            sentTo = sentTo("book-created")
            body = body(
                """
                {
                    "type": "book-created",
                    "book": {
                        "title": "Clean Code",
                        "isbn": "9780132350884"
                    }
                }
                """.trimIndent()
            )
        }
    }
)
