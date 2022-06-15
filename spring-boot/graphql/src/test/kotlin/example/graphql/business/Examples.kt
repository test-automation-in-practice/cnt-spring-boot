package example.graphql.business

import java.util.UUID

object Examples {

    val title_theMartian = Title("The Martian")
    val isbn_theMartian = Isbn("9780804139021")
    val book_theMartian = Book(title_theMartian, isbn_theMartian)

    val title_projectHailMary = Title("Project Hail Mary")
    val isbn_projectHailMary = Isbn("9780593135204")
    val book_projectHailMary = Book(title_projectHailMary, isbn_projectHailMary)

    val id_theMartion = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val record_theMartion = BookRecord(id_theMartion, book_theMartian)

    val id_projectHailMary = UUID.fromString("7d823198-2ef3-41a6-b780-29ba6723d8c9")
    val record_projectHailMary = BookRecord(id_projectHailMary, book_projectHailMary)

}
