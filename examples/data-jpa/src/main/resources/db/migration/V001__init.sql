CREATE TABLE books
(
    id      uuid,
    title   text,
    isbn    varchar(13),
    version bigint,
    PRIMARY KEY (id)
)
