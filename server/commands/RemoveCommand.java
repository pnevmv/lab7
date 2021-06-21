package server.commands;

import common.exceptions.*;
import common.interaction.User;
import common.model.MusicBand;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

/**
 * Command 'remove_key'. Removes the element by key.
 */
public class RemoveCommand extends AbstractCommand {
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public RemoveCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_key null", "Remove an item from the collection by its key");
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
            if (arg.isEmpty() || objArgument != null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            Integer id = Integer.parseInt(arg);
            MusicBand bandToRemove = collectionManager.getById(id);
            if (bandToRemove == null) throw new BandCanNotFoundException();
            if (!bandToRemove.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkBandUserId(bandToRemove.getId(), user)) throw new ManualDatabaseEditException();
            databaseCollectionManager.deleteBandById(id);
            collectionManager.removeFromCollection(bandToRemove.getId());
            ResponseOutputDeliver.appendLn("Музыкальная группа успешно удалена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendLn("Использование: '" + getName() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputDeliver.appendError("Коллекция пуста!");
        } catch (NumberFormatException exception) {
            ResponseOutputDeliver.appendError("ID должен быть представлен числом!");
        } catch (BandCanNotFoundException exception) {
            ResponseOutputDeliver.appendError("Группы с таким ID в коллекции нет!");
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
