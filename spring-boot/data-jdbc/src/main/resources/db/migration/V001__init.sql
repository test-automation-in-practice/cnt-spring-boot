CREATE TABLE book_records
(
    id      uuid,
    title   text,
    isbn    varchar(13),
    version bigint,
    PRIMARY KEY (id)
)
