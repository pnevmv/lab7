package server.commands;

import common.exceptions.DatabaseHandlingException;
import common.exceptions.ManualDatabaseEditException;
import common.exceptions.PermissionDeniedException;
import common.exceptions.WrongAmountOfElementsException;
import common.interaction.User;
import common.model.MusicBand;
import common.utility.OutputDeliver;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

import java.util.Collection;
import java.util.List;

/**
 * Command 'clear'. Cleans the collection.
 */
public class ClearCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public ClearCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("clear","Clear collection");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArg, Object objectArg, User user) {
        try {
            if (!stringArg.isEmpty() || objectArg != null) throw new WrongAmountOfElementsException();
            Collection<MusicBand> bands = collectionManager.getCollection().values();
            for (MusicBand band : bands) {
                if (!band.getOwner().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionManager.checkBandUserId(band.getId(), user)) throw new ManualDatabaseEditException();
            }
            databaseCollectionManager.clearCollection();
            collectionManager.clearCollection();
            ResponseOutputDeliver.appendLn("Коллекция очищена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendLn("Использование: '" + getName() + "'");
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
