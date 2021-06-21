package server.utility;

import common.model.*;
import common.exceptions.DatabaseHandlingException;
import common.interaction.BandRaw;
import common.interaction.User;
import common.utility.OutputDeliver;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

/**
 * Operates the database collection itself.
 */
public class DatabaseCollectionManager {
    // BAND_TABLE
    private final String SELECT_ALL_BANDS = "SELECT * FROM " + DatabaseHandler.BAND_TABLE;
    private final String SELECT_BAND_BY_ID = SELECT_ALL_BANDS + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String SELECT_BAND_BY_ID_AND_USER_ID = SELECT_BAND_BY_ID + " AND " +
            DatabaseHandler.BAND_TABLE_USER_ID_COLUMN + " = ?";
    private final String INSERT_BAND = "INSERT INTO " +
            DatabaseHandler.BAND_TABLE + " (" +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_NAME_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_CREATION_DATE_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_NUMBER_OF_PARTICIPANT_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_DESCRIPTION_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_GENRE_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_STUDIO_COLUMN + ", " +
            DatabaseHandler.BAND_TABLE_USER_ID_COLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String DELETE_BAND_BY_ID = "DELETE FROM " + DatabaseHandler.BAND_TABLE +
            " WHERE " + DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String DELETE_COORDINATES_BY_ID = "DELETE FROM " + DatabaseHandler.COORDINATES_TABLE +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_BAND_ID_COLUMN + " = ?";
    private final String UPDATE_BAND_NAME_BY_ID = "UPDATE " + DatabaseHandler.BAND_TABLE + " SET " +
            DatabaseHandler.BAND_TABLE_NAME_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_BAND_NUMBER_OF_PARTICIPANT_BY_ID = "UPDATE " + DatabaseHandler.BAND_TABLE + " SET " +
            DatabaseHandler.BAND_TABLE_NUMBER_OF_PARTICIPANT_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_BAND_DESCRIPTION_BY_ID= "UPDATE " + DatabaseHandler.BAND_TABLE + " SET " +
            DatabaseHandler.BAND_TABLE_DESCRIPTION_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_BAND_GENRE_BY_ID = "UPDATE " + DatabaseHandler.BAND_TABLE + " SET " +
            DatabaseHandler.BAND_TABLE_GENRE_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    private final String UPDATE_BAND_STUDIO_BY_ID = "UPDATE " + DatabaseHandler.BAND_TABLE + " SET " +
            DatabaseHandler.BAND_TABLE_STUDIO_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.BAND_TABLE_ID_COLUMN + " = ?";
    // COORDINATES_TABLE
    private final String SELECT_ALL_COORDINATES = "SELECT * FROM " + DatabaseHandler.COORDINATES_TABLE;
    private final String SELECT_COORDINATES_BY_BAND_ID = SELECT_ALL_COORDINATES +
            " WHERE " + DatabaseHandler.COORDINATES_TABLE_BAND_ID_COLUMN + " = ?";
    private final String INSERT_COORDINATES = "INSERT INTO " +
            DatabaseHandler.COORDINATES_TABLE + " (" +
            DatabaseHandler.COORDINATES_TABLE_BAND_ID_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + ", " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + ") VALUES (?, ?, ?)";
    private final String UPDATE_COORDINATES_BY_BAND_ID = "UPDATE " + DatabaseHandler.COORDINATES_TABLE + " SET " +
            DatabaseHandler.COORDINATES_TABLE_X_COLUMN + " = ?, " +
            DatabaseHandler.COORDINATES_TABLE_Y_COLUMN + " = ?" + " WHERE " +
            DatabaseHandler.COORDINATES_TABLE_BAND_ID_COLUMN + " = ?";
    private DatabaseHandler databaseHandler;
    private DatabaseUserManager databaseUserManager;

    public DatabaseCollectionManager(DatabaseHandler databaseHandler, DatabaseUserManager databaseUserManager) {
        this.databaseHandler = databaseHandler;
        this.databaseUserManager = databaseUserManager;
    }

    /**
     * Create Band.
     * @param resultSet Result set parameters of Band.
     * @return New Band.
     * @throws SQLException When there's exception inside.
     */
    private MusicBand createBand(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(DatabaseHandler.BAND_TABLE_ID_COLUMN);
        String name = resultSet.getString(DatabaseHandler.BAND_TABLE_NAME_COLUMN);
        LocalDateTime creationDate = resultSet.getTimestamp(DatabaseHandler.BAND_TABLE_CREATION_DATE_COLUMN).toLocalDateTime();
        Long numberOfParticipant = resultSet.getLong(DatabaseHandler.BAND_TABLE_NUMBER_OF_PARTICIPANT_COLUMN);
        String description = resultSet.getString(DatabaseHandler.BAND_TABLE_DESCRIPTION_COLUMN);
        MusicGenre genre = MusicGenre.valueOf(resultSet.getString(DatabaseHandler.BAND_TABLE_GENRE_COLUMN));
        Studio studio = new Studio(resultSet.getString(DatabaseHandler.BAND_TABLE_STUDIO_COLUMN));
        Coordinates coordinates = getCoordinatesByBandId(id);
        User owner = databaseUserManager.getUserById(resultSet.getLong(DatabaseHandler.BAND_TABLE_USER_ID_COLUMN));
        return new MusicBand(
                id,
                name,
                coordinates,
                creationDate,
                numberOfParticipant,
                description,
                genre,
                studio,
                owner
        );
    }

    /**
     * @return List of Bands.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public HashMap<Integer, MusicBand> getCollection() throws DatabaseHandlingException {
        HashMap<Integer, MusicBand> bandList = new HashMap<>();
        PreparedStatement preparedSelectAllStatement = null;
        try {
            preparedSelectAllStatement = databaseHandler.getPreparedStatement(SELECT_ALL_BANDS, false);
            ResultSet resultSet = preparedSelectAllStatement.executeQuery();
            while (resultSet.next()) {
                MusicBand newBand = createBand(resultSet);
                bandList.put(newBand.getId(), newBand);
            }
        } catch (SQLException exception) {
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectAllStatement);
        }
        return bandList;
    }

    /**
     * @param bandId Id of Band.
     * @return coordinates.
     * @throws SQLException When there's exception inside.
     */
    private Coordinates getCoordinatesByBandId(Integer bandId) throws SQLException {
        Coordinates coordinates;
        PreparedStatement preparedSelectCoordinatesByBandIdStatement = null;
        try {
            preparedSelectCoordinatesByBandIdStatement =
                    databaseHandler.getPreparedStatement(SELECT_COORDINATES_BY_BAND_ID, false);
            preparedSelectCoordinatesByBandIdStatement.setLong(1, bandId);
            ResultSet resultSet = preparedSelectCoordinatesByBandIdStatement.executeQuery();
            OutputDeliver.println("Выполнен запрос SELECT_COORDINATES_BY_BAND_ID.");
            if (resultSet.next()) {
                coordinates = new Coordinates(
                        resultSet.getDouble(DatabaseHandler.COORDINATES_TABLE_X_COLUMN),
                        resultSet.getLong(DatabaseHandler.COORDINATES_TABLE_Y_COLUMN)
                );
            } else throw new SQLException();
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при выполнении запроса SELECT_COORDINATES_BY_BAND_ID!");
            throw new SQLException(exception);
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectCoordinatesByBandIdStatement);
        }
        return coordinates;
    }

    /**
     * Delete Band by id.
     * @param bandId Id of Band.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void deleteBandById(Integer bandId) throws DatabaseHandlingException {
        PreparedStatement preparedDeleteBandByIdStatement = null;
        PreparedStatement preparedDeleteCoordinatesByIdStatement = null;
        try {
            preparedDeleteBandByIdStatement = databaseHandler.getPreparedStatement(DELETE_BAND_BY_ID, false);
            preparedDeleteBandByIdStatement.setInt(1, bandId);
            preparedDeleteCoordinatesByIdStatement = databaseHandler.getPreparedStatement(DELETE_COORDINATES_BY_ID, false);
            preparedDeleteCoordinatesByIdStatement.setInt(1, bandId);
            if (preparedDeleteBandByIdStatement.executeUpdate() == 0) OutputDeliver.println(3);
            if (preparedDeleteCoordinatesByIdStatement.executeUpdate() == 0) OutputDeliver.println(3);
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при выполнении запроса DELETE_BAND_BY_ID!");
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedDeleteBandByIdStatement);
            databaseHandler.closePreparedStatement(preparedDeleteCoordinatesByIdStatement);
        }
    }

    /**
     * @param bandRaw Band raw.
     * @param user      User.
     * @return Band.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public MusicBand insertBand(BandRaw bandRaw, String bandStringId, User user) throws DatabaseHandlingException {
        MusicBand band;
        PreparedStatement preparedInsertBandStatement = null;
        PreparedStatement preparedInsertCoordinatesStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();
            LocalDateTime creationTime = LocalDateTime.now();

            int bandId = Integer.parseInt(bandStringId);

            preparedInsertBandStatement = databaseHandler.getPreparedStatement(INSERT_BAND, true);
            preparedInsertCoordinatesStatement = databaseHandler.getPreparedStatement(INSERT_COORDINATES, true);

            preparedInsertBandStatement.setInt(1, bandId);
            preparedInsertBandStatement.setString(2, bandRaw.getName());
            preparedInsertBandStatement.setTimestamp(3, Timestamp.valueOf(creationTime));
            preparedInsertBandStatement.setLong(4, bandRaw.getNumberOfParticipants());
            preparedInsertBandStatement.setString(5, bandRaw.getDescription());
            preparedInsertBandStatement.setString(6, bandRaw.getMusicGenre().toString());
            preparedInsertBandStatement.setString(7, bandRaw.getStudio().toString());
            preparedInsertBandStatement.setLong(8, databaseUserManager.getUserIdByUsername(user));
            if (preparedInsertBandStatement.executeUpdate() == 0) throw new SQLException();
            OutputDeliver.println("Выполнен запрос INSERT_BAND.");

            preparedInsertCoordinatesStatement.setLong(1, bandId);
            preparedInsertCoordinatesStatement.setDouble(2, bandRaw.getCoordinates().getX());
            preparedInsertCoordinatesStatement.setLong(3, bandRaw.getCoordinates().getY());
            if (preparedInsertCoordinatesStatement.executeUpdate() == 0) throw new SQLException();
            OutputDeliver.println("Выполнен запрос INSERT_COORDINATES.");

            band = new MusicBand(
                    bandId,
                    bandRaw.getName(),
                    bandRaw.getCoordinates(),
                    creationTime,
                    bandRaw.getNumberOfParticipants(),
                    bandRaw.getDescription(),
                    bandRaw.getMusicGenre(),
                    bandRaw.getStudio(),
                    user
            );

            databaseHandler.commit();
            return band;
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при выполнении группы запросов на добавление нового объекта!\n" + exception.getSQLState() + "\n");
            exception.printStackTrace();
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedInsertBandStatement);
            databaseHandler.closePreparedStatement(preparedInsertCoordinatesStatement);
            databaseHandler.setNormalMode();
        }
    }

    /**
     * @param bandRaw Band raw.
     * @param bandId  Id of Band.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void updateBandById(Integer bandId, BandRaw bandRaw) throws DatabaseHandlingException {
        PreparedStatement preparedUpdateBandNameByIdStatement = null;
        PreparedStatement preparedUpdateBandNumberOfParticipantByIdStatement = null;
        PreparedStatement preparedUpdateBandDescriptionByIdStatement = null;
        PreparedStatement preparedUpdateBandGenreByIdStatement = null;
        PreparedStatement preparedUpdateBandStudioByIdStatement = null;
        PreparedStatement preparedUpdateCoordinatesByBandIdStatement = null;
        try {
            databaseHandler.setCommitMode();
            databaseHandler.setSavepoint();

            preparedUpdateBandNameByIdStatement = databaseHandler.getPreparedStatement(UPDATE_BAND_NAME_BY_ID, false);
            preparedUpdateBandNumberOfParticipantByIdStatement = databaseHandler.getPreparedStatement(UPDATE_BAND_NUMBER_OF_PARTICIPANT_BY_ID, false);
            preparedUpdateBandDescriptionByIdStatement = databaseHandler.getPreparedStatement(UPDATE_BAND_DESCRIPTION_BY_ID, false);
            preparedUpdateBandGenreByIdStatement = databaseHandler.getPreparedStatement(UPDATE_BAND_GENRE_BY_ID, false);
            preparedUpdateBandStudioByIdStatement = databaseHandler.getPreparedStatement(UPDATE_BAND_STUDIO_BY_ID, false);
            preparedUpdateCoordinatesByBandIdStatement = databaseHandler.getPreparedStatement(UPDATE_COORDINATES_BY_BAND_ID, false);

            if (bandRaw.getName() != null) {
                preparedUpdateBandNameByIdStatement.setString(1, bandRaw.getName());
                preparedUpdateBandNameByIdStatement.setLong(2, bandId);
                if (preparedUpdateBandNameByIdStatement.executeUpdate() == 0) throw new SQLException();
                OutputDeliver.println("Выполнен запрос UPDATE_BAND_NAME_BY_ID.");
            }
            if (bandRaw.getCoordinates() != null) {
                preparedUpdateCoordinatesByBandIdStatement.setDouble(1, bandRaw.getCoordinates().getX());
                preparedUpdateCoordinatesByBandIdStatement.setFloat(2, bandRaw.getCoordinates().getY());
                preparedUpdateCoordinatesByBandIdStatement.setLong(3, bandId);
                if (preparedUpdateCoordinatesByBandIdStatement.executeUpdate() == 0) throw new SQLException();
                OutputDeliver.println("Выполнен запрос UPDATE_COORDINATES_BY_BAND_ID.");
            }
            if (bandRaw.getNumberOfParticipants() != -1) {
                preparedUpdateBandNumberOfParticipantByIdStatement.setLong(1, bandRaw.getNumberOfParticipants());
                preparedUpdateBandNumberOfParticipantByIdStatement.setLong(2, bandId);
                if (preparedUpdateBandNumberOfParticipantByIdStatement.executeUpdate() == 0) throw new SQLException();
                OutputDeliver.println("Выполнен запрос UPDATE_BAND_NUMBER_PARTICIPANT_BY_ID.");
            }
            if (bandRaw.getDescription() != null) {
                preparedUpdateBandDescriptionByIdStatement.setString(1, bandRaw.getDescription());
            }
            if (bandRaw.getMusicGenre() != null) {
                preparedUpdateBandGenreByIdStatement.setString(1, bandRaw.getMusicGenre().toString());
                preparedUpdateBandGenreByIdStatement.setLong(2, bandId);
                if (preparedUpdateBandGenreByIdStatement.executeUpdate() == 0) throw new SQLException();
                OutputDeliver.println("Выполнен запрос UPDATE_BAND_MUSIC_GENRE_BY_ID.");
            }
            if (bandRaw.getStudio() != null) {
                preparedUpdateBandStudioByIdStatement.setString(1, bandRaw.getStudio().toString());
                preparedUpdateBandStudioByIdStatement.setLong(2, bandId);
                if (preparedUpdateBandStudioByIdStatement.executeUpdate() == 0) throw new SQLException();
                OutputDeliver.println("Выполнен запрос UPDATE_BAND_STUDIO_BY_ID.");
            }

            databaseHandler.commit();
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при выполнении группы запросов на обновление объекта!");
            databaseHandler.rollback();
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedUpdateBandNameByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateBandNumberOfParticipantByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateBandDescriptionByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateBandGenreByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateBandStudioByIdStatement);
            databaseHandler.closePreparedStatement(preparedUpdateCoordinatesByBandIdStatement);
            databaseHandler.setNormalMode();
        }
    }

    /**
     * Checks Band user id.
     * @param bandId Id of Band.
     * @param user Owner of band.
     * @throws DatabaseHandlingException When there's exception inside.
     * @return Is everything ok.
     */
    public boolean checkBandUserId(Integer bandId, User user) throws DatabaseHandlingException {
        PreparedStatement preparedSelectBandByIdAndUserIdStatement = null;
        try {
            preparedSelectBandByIdAndUserIdStatement = databaseHandler.getPreparedStatement(SELECT_BAND_BY_ID_AND_USER_ID, false);
            preparedSelectBandByIdAndUserIdStatement.setLong(1, bandId);
            preparedSelectBandByIdAndUserIdStatement.setLong(2, databaseUserManager.getUserIdByUsername(user));
            ResultSet resultSet = preparedSelectBandByIdAndUserIdStatement.executeQuery();
            OutputDeliver.println("Выполнен запрос SELECT_BAND_BY_ID_AND_USER_ID.");
            return resultSet.next();
        } catch (SQLException exception) {
            OutputDeliver.printError("Произошла ошибка при выполнении запроса SELECT_BAND_BY_ID_AND_USER_ID!");
            throw new DatabaseHandlingException();
        } finally {
            databaseHandler.closePreparedStatement(preparedSelectBandByIdAndUserIdStatement);
        }
    }

    /**
     * Clear the collection.
     * @throws DatabaseHandlingException When there's exception inside.
     */
    public void clearCollection() throws  DatabaseHandlingException{
        Collection<MusicBand> bandList = getCollection().values();
        for (MusicBand band : bandList) {
            deleteBandById(band.getId());
        }
    }
}