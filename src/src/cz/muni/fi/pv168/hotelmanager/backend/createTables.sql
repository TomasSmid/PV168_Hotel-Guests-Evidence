CREATE TABLE GUEST (
    ID BIGINT NOT NULL CONSTRAINT GID_PK PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(70) CONSTRAINT GUEST_NAME_NOT_NULL NOT NULL CONSTRAINT GUEST_NAME_NOT_EMPTY CHECK ("NAME" <> ''),
    PHONE VARCHAR(18) CONSTRAINT PHONE_NOT_EMPTY CHECK (PHONE <> ''),
    ID_CARD_NUM VARCHAR(9) CONSTRAINT IDCARDNUM_NOT_NULL NOT NULL CONSTRAINT IDCARDNUM_NOT_EMPTY CHECK (ID_CARD_NUM <> ''),
    BORN TIMESTAMP CONSTRAINT BORN_NOT_NULL NOT NULL
);

CREATE TABLE ROOM (
    ID BIGINT NOT NULL CONSTRAINT RID_PK PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    CAPACITY INTEGER CONSTRAINT CAPACITY_NOT_ZERO_NOR_NEGATIVE CHECK (CAPACITY > 0) CONSTRAINT CAPACITY_NOT_NULL NOT NULL,
    PRICE BIGINT CONSTRAINT PRICE_NOT_NULL NOT NULL CONSTRAINT PRICE_NOT_ZERO_NOR_NEGATIVE CHECK (PRICE > 0),
    FLOOR INTEGER CONSTRAINT FLOOR_NOT_NULL NOT NULL CONSTRAINT FLOOR_NOT_ZERO_NOR_NEGATIVE CHECK (FLOOR > 0),
    "NUMBER" VARCHAR(3) CONSTRAINT NUMBER_NOT_NULL NOT NULL CONSTRAINT NUMBER_NOT_EMPTY CHECK ("NUMBER" <> ''),
    ROOM_TYPE VARCHAR(9) CONSTRAINT ROOMTYPE_NOT_NULL NOT NULL CONSTRAINT ROOMTYPE_NOT_EMPTY CHECK (ROOM_TYPE <> '')
);

CREATE TABLE RESERVATION (
    ID BIGINT NOT NULL CONSTRAINT RESID_PK PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    ROOM_ID BIGINT CONSTRAINT RESROOM_NOT_NULL NOT NULL CONSTRAINT RESROOM_FK REFERENCES ROOM,
    GUEST_ID BIGINT CONSTRAINT RESGUEST_NOT_NULL NOT NULL CONSTRAINT RESGUEST_FK REFERENCES GUEST,
    START_TIME TIMESTAMP CONSTRAINT STARTTIME_NOT_NULL NOT NULL,
    REAL_END_TIME TIMESTAMP,
    EXPECTED_END_TIME TIMESTAMP CONSTRAINT EXPECTEDENDTIME_NOT_NULL NOT NULL,
    SERV_SPENDINGS BIGINT CONSTRAINT SERVSPENDINGS_NOT_NULL NOT NULL CONSTRAINT SERVSPENDINGS_NOT_NEGATIVE CHECK (SERV_SPENDINGS >= 0),
    CONSTRAINT STARTTIME_BEFORE_EXPECTEDENDTIME CHECK (START_TIME < EXPECTED_END_TIME)
);