package server.commands;

import common.exceptions.*;
import common.interaction.BandRaw;
import common.interaction.User;
import common.model.*;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

import java.time.LocalDateTime;

/**
 * Command 'update'. Updates the information about selected marine.
 */
public class UpdateCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public UpdateCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("update id {element}", "Update the value of the collection item whose id is equal to the given");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     * @return Command exit status.
     */
    @Override
    public boolean execute(String arg, Object objArgument, User user) {
        try {
            if (arg.isEmpty() || objArgument == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();

            Integer id = Integer.parseInt(arg);
            if (id <= 0) throw new NumberFormatException();
            MusicBand oldBand = collectionManager.getById(id);
            if (oldBand == null) throw new BandCanNotFoundException();
            if (!oldBand.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkBandUserId(oldBand.getId(), user)) throw new ManualDatabaseEditException();
            BandRaw bandRaw = (BandRaw) objArgument;

            databaseCollectionManager.updateBandById(id, bandRaw);

            String name = bandRaw.getName() == null ? oldBand.getName() : bandRaw.getName();
            Coordinates coordinates = bandRaw.getCoordinates() == null ? oldBand.getCoordinates() : bandRaw.getCoordinates();
            LocalDateTime creationDate = oldBand.getCreationDate();
            Long numberOfParticipant = bandRaw.getNumberOfParticipants() == -1 ? oldBand.getNumberOfParticipants() : bandRaw.getNumberOfParticipants();
            String description = bandRaw.getDescription() == null ? oldBand.getDescription() : bandRaw.getDescription();
            MusicGenre genre = bandRaw.getMusicGenre() == null ? oldBand.getGenre() : bandRaw.getMusicGenre();
            Studio studio = bandRaw.getStudio() == null ? oldBand.getStudio() : bandRaw.getStudio();

            collectionManager.removeFromCollection(oldBand.getId());
            collectionManager.addToCollection(id, new MusicBand(
                    id,
                    name,
                    coordinates,
                    creationDate,
                    numberOfParticipant,
                    description,
                    genre,
                    studio,
                    user
            ));
            ResponseOutputDeliver.appendLn("Группа успешно изменена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendLn("Использование: '" + getName() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputDeliver.appendError("Коллекция пуста!");
        } catch (NumberFormatException exception) {
            ResponseOutputDeliver.appendError("ID должен быть представлен положительным числом!");
        } catch (BandCanNotFoundException exception) {
            ResponseOutputDeliver.appendError("Группы с таким ID в коллекции нет!");
        } catch (ClassCastException exception) {
            ResponseOutputDeliver.appendError("Переданный клиентом объект неверен!");
        } catch (DatabaseHandlingException exception) {
            ResponseOutputDeliver.appendError("Произошла ошибка при обращении к базе данных!");
        } catch (PermissionDeniedException exception) {
            ResponseOutputDeliver.appendError("Недостаточно прав для выполнения данной команды!");
            ResponseOutputDeliver.appendLn("Принадлежащие другим пользователям объекты доступны только для чтения.");
        } catch (ManualDatabaseEditException exception) {
            ResponseOutputDeliver.appendError("Произошло прямое изменение базы данных!");
            ResponseOutputDeliver.appendLn("Перезапустите клиент для избежания возможных ошибок.");
        }
        return false;
    }
}
