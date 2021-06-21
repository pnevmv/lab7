package server.utility;

import common.utility.OutputDeliver;

import java.sql.*;
/**
 * A class for handle database.
 */
public class DatabaseHandler {
    //Table names
    public static final String BAND_TABLE = "music_band";
    public static final String USER_TABLE = "my_user";
    public static final String COORDINATES_TABLE = "coordinates";
    //BAND_TABLE column names
    public static final String BAND_TABLE_ID_COLUMN = "id";
    public static final String BAND_TABLE_NAME_COLUMN = "name";
    public static final String BAND_TABLE_CREATION_DATE_COLUMN = "creation_date";
    public static final String BAND_TABLE_NUMBER_OF_PARTICIPANT_COLUMN = "number_of_participant";
    public static final String BAND_TABLE_DESCRIPTION_COLUMN = "description";
    public static final String BAND_TABLE_GENRE_COLUMN = "music_genre";
    public static final String BAND_TABLE_STUDIO_COLUMN = "studio";
    public static final String BAND_TABLE_USER_ID_COLUMN = "user_id";
    //USER_TABLE column names
    public static final String USER_TABLE_ID_COLUMN = "id";
    public static final String USER_TABLE_NAME_COLUMN = "username";
    public static final String USER_TABLE_PASSWORD_COLUMN = "password";
    //COORDINATES_TABLE column names
    public static final String COORDINATES_TABLE_BAND_ID_COLUMN = "band_id";
    public static final String COORDINATES_TABLE_X_COLUMN = "x";
    public static final String COORDINATES_TABLE_Y_COLUMN = "y";

    private final String JDBC_DRIVER = "org.postgresql.Driver";

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseHandler(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        connectToDatabase();
    }

    /**
     * A class for connect to database.
     */
    private void connectToDatabase() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
            OutputDeliver.println("Соединение с базой данных установлено.");
        } catch (ClassNotFoundException exception) {
            OutputDeliver.printError("Драйвер не найден!");
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при подключении к базе данных!");
        }
    }

    /**
     * @param sqlStatement SQL statement to be prepared.
     * @param generateKeys Is keys needed to be generated.
     * @return Prepared statement.
     * @throws SQLException When there's exception inside.
     */
    public PreparedStatement getPreparedStatement(String sqlStatement, boolean generateKeys) throws SQLException {
        PreparedStatement preparedStatement;
        try {
            if (connection == null) throw new SQLException();
            int autoKey = generateKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
            preparedStatement = connection.prepareStatement(sqlStatement, autoKey);
            return preparedStatement;
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    /**
     * Close prepared statement.
     * @param sqlStatement SQL statement to be closed.
     */
    public void closePreparedStatement(PreparedStatement sqlStatement) {
        if (sqlStatement == null) return;
        try {
            sqlStatement.close();
        } catch (SQLException exception) {
            OutputDeliver.printError("Экзепшын выпал... ЭсКуЭль Экзепшн");
        }
    }

    /**
     * Close connection to database.
     */
    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
            OutputDeliver.println("Соединение с базой данных разорвано.");
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при разрыве соединения с базой данных!");
        }
    }

    /**
     * Set commit mode of database.
     */
    public void setCommitMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при установлении режима транзакции базы данных!");
        }
    }

    /**
     * Set normal mode of database.
     */
    public void setNormalMode() {
        try {
            if (connection == null) throw new SQLException();
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при установлении нормального режима базы данных!");
        }
    }

    /**
     * Commit database status.
     */
    public void commit() {
        try {
            if (connection == null) throw new SQLException();
            connection.commit();
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при подтверждении нового состояния базы данных!");
        }
    }

    /**
     * Roll back database status.
     */
    public void rollback() {
        try {
            if (connection == null) throw new SQLException();
            connection.rollback();
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при возврате исходного состояния базы данных!");
        }
    }

    /**
     * Set save point of database.
     */
    public void setSavepoint() {
        try {
            if (connection == null) throw new SQLException();
            connection.setSavepoint();
        } catch (SQLException exception) {
            OutputDeliver.printError("Ошибка сохранения базы данных!");
        }
    }
}
