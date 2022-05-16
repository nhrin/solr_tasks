create table TWITS (
                       ID SERIAL unique not null,
                       USER_NAME VARCHAR(36) not null,
                       CONTENT VARCHAR(256),
                       PUBLICATION_DATE TIMESTAMP
);
