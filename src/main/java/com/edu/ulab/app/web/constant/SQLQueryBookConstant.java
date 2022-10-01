package com.edu.ulab.app.web.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SQLQueryBookConstant {
    public static final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";

    public static final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";

    public static final String SELECT_BY_ID_SQL = "SELECT * FROM BOOK WHERE ID = ?";

    public static final String DELETE_BY_ID_SQL = "DELETE FROM BOOK WHERE ID = ?";

    public static final String DELETE_BY_USER_ID_SQL = "DELETE FROM BOOK WHERE USER_ID = ?";

    public static final String SELECT_BY_USER_ID_SQL = "SELECT * FROM BOOK WHERE USER_ID = ?";

}
