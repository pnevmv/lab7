package server.commands;

import common.exceptions.DatabaseHandlingException;
import common.exceptions.DeclaredLimitException;
import common.interaction.BandRaw;
import common.interaction.User;
import common.model.MusicBand;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.OutputDeliver;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

import java.time.LocalDateTime;

/**
 * Command 'insert key'. Adds new element with key.
 */
public class InsertCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public InsertCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("insert <key> {element}", "Add a new item with the given key");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (stringArgument.isEmpty() || objectArgument == null) throw new WrongAmountOfElementsException();
            if (Integer.parseInt(stringArgument) <= 0) throw new DeclaredLimitException();
            BandRaw bandRaw = (BandRaw) objectArgument;
            MusicBand bandToAdd = databaseCollectionManager.insertBand(bandRaw, stringArgument, user);
            collectionManager.addToCollection(bandToAdd.getId(), bandToAdd);
            ResponseOutputDeliver.appendLn("Музыкальная группа успешно добавлена!");
            return true;
        }  catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendLn("Использование: '" + getName() + "'");
        } catch (ClassCastException exception) {
            ResponseOutputDeliver.appendError("Переданный клиентом объект неверен!");
        } catch (DatabaseHandlingException exception) {
            ResponseOutputDeliver.appendError("Произошла ошибка при обращении к базе данных!");
        } catch (DeclaredLimitException exception) {
            ResponseOutputDeliver.appendError("Ключ должен быть натуральным числом!");
        }
        return false;
    }
}
