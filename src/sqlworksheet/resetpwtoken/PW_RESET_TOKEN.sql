CREATE TABLE PW_RESET_TOKEN_TBL (
    TOKEN_NO NUMBER PRIMARY KEY,
    MEMBER_ID VARCHAR2(50) NOT NULL,
    TOKEN VARCHAR2(200) NOT NULL UNIQUE,
    CREATE_TIME TIMESTAMP DEFAULT SYSTIMESTAMP,
    EXPIRE_TIME TIMESTAMP NOT NULL,
    CONSTRAINT FK_MEMBER FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER_TBL(MEMBER_ID)
);

-- SEQUENCE 생성
CREATE SEQUENCE SEQ_TOKEN_NO
NOCACHE
NOCYCLE;

COMMIT;