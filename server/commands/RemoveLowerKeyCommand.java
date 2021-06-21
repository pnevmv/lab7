package server.commands;

import common.exceptions.*;
import common.interaction.BandRaw;
import common.interaction.User;
import common.model.MusicBand;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

import java.time.LocalDateTime;

/**
 * Command 'remove_lower_key'. Removes all element if it's key is lower'.
 */
public class RemoveLowerKeyCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public RemoveLowerKeyCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_lower_key null", "Remove from the collection all elements whose key is less than the specified one");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     * @return Command exit status.
     */
    @Override
    public boolean execute(String arg, Object objectArg, User user) {
        try {
            if (arg.isEmpty() || objectArg != null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            Integer key = Integer.parseInt(arg);
            for (MusicBand band : collectionManager.getLowerKeyBands(key)) {
                if (!band.getOwner().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionManager.checkBandUserId(band.getId(), user)) throw new ManualDatabaseEditException();
            }
            for (MusicBand band : collectionManager.getLowerKeyBands(key)) {
                databaseCollectionManager.deleteBandById(band.getId());
                collectionManager.removeFromCollection(band.getId());
            }
            ResponseOutputDeliver.appendLn("Операция удаления завершена успешно!");
            return true;
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputDeliver.appendError("Collection is empty!");
        } catch (IllegalArgumentException exc) {
            ResponseOutputDeliver.appendError("Invalid command argument!");
        } catch (DatabaseHandlingException exception) {
            ResponseOutputDeliver.appendError("Database handling error!");
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendError("Получены не все параметры!");
        } catch (PermissionDeniedException e) {
            ResponseOutputDeliver.appendError("Нет прав на данную операцию!");
        } catch (ManualDatabaseEditException e) {
            ResponseOutputDeliver.appendError("Прямое изменение базы данных");
        }
        return false;
    }
}
