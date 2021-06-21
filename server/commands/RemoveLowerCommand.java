package server.commands;

import common.exceptions.*;
import common.interaction.BandRaw;
import common.interaction.User;
import common.model.Coordinates;
import common.model.MusicBand;
import common.model.MusicGenre;
import common.model.Studio;
import server.utility.CollectionManager;
import server.utility.DatabaseCollectionManager;
import server.utility.ResponseOutputDeliver;

import java.time.LocalDateTime;

/**
 * Command 'remove_lower'. Removes all elements it's lower.
 */
public class RemoveLowerCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public RemoveLowerCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_lower {element}", "Remove all items from the collection that are less than the specified one");
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
            if (!stringArgument.isEmpty() || objectArgument == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            BandRaw bandRaw = (BandRaw) objectArgument;
            MusicBand bandToFind = new MusicBand(
                    0,
                    bandRaw.getName(),
                    bandRaw.getCoordinates(),
                    LocalDateTime.now(),
                    bandRaw.getNumberOfParticipants(),
                    bandRaw.getDescription(),
                    bandRaw.getMusicGenre(),
                    bandRaw.getStudio(),
                    user
            );
            if (bandToFind == null) throw new BandCanNotFoundException();
            for (MusicBand band : collectionManager.getLower(bandToFind)) {
                if (!band.getOwner().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionManager.checkBandUserId(band.getId(), user)) throw new ManualDatabaseEditException();
            }
            for (MusicBand band : collectionManager.getLower(bandToFind)) {
                databaseCollectionManager.deleteBandById(band.getId());
                collectionManager.removeFromCollection(band.getId());
            }
            ResponseOutputDeliver.appendLn("Музыкальные группы успешно удалены!");
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
        } catch (BandCanNotFoundException e) {
            ResponseOutputDeliver.appendError("Подходящих групп не найдено");
        }
        return false;
    }
}
