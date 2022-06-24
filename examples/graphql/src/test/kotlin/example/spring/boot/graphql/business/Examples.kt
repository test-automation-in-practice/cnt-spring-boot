package example.spring.boot.graphql.business

import java.util.UUID
import java.util.UUID.fromString

object Examples {

    val id_theMartian: UUID = fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val book_theMartian = Book(Title("The Martian"), Isbn("9780804139021"))
    val record_theMartian = BookRecord(id_theMartian, book_theMartian)

    val id_projectHailMary: UUID = fromString("7d823198-2ef3-41a6-b780-29ba6723d8c9")
    val book_projectHailMary = Book(Title("Project Hail Mary"), Isbn("9780593135204"))
    val record_projectHailMary = BookRecord(id_projectHailMary, book_projectHailMary)

}
