CREATE TABLE SEQ_STORE
(
  SEQ_NAME VARCHAR(50) PRIMARY KEY,
  SEQ_VALUE BIGINT NOT NULL
);

INSERT INTO SEQ_STORE VALUES ('USER.ID.PK', 3);
INSERT INTO SEQ_STORE VALUES ('ROLE.ID.PK', 2);