package com.edu.ulab.app.web.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SQLQueryUserConstant {
    public static final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";

    public static final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";

    public static final String SELECT_BY_ID_SQL = "SELECT * FROM PERSON WHERE ID = ?";

    public static final String DELETE_BY_ID_SQL = "DELETE FROM PERSON WHERE ID = ?";

    public static final String SELECT_ALL_SQL = "SELECT * FROM PERSON";
}
