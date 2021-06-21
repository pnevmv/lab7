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
 * Command 'replace_if_greater'. Replace element greater than user entered by key.
 */
public class ReplaceIfGreaterCommand extends AbstractCommand{
    private CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public ReplaceIfGreaterCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("replace_if_greater null {element}", "Replace value by key if new value is greater than old");
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
            if (arg.isEmpty() || objectArg == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            BandRaw bandRaw = (BandRaw) objectArg;
            MusicBand bandToCompare = new MusicBand(
                    collectionManager.generateNextId(),
                    bandRaw.getName(),
                    bandRaw.getCoordinates(),
                    LocalDateTime.now(),
                    bandRaw.getNumberOfParticipants(),
                    bandRaw.getDescription(),
                    bandRaw.getMusicGenre(),
                    bandRaw.getStudio(),
                    user
            );
            MusicBand oldBand = collectionManager.getById(Integer.parseInt(arg));
            if (oldBand == null) throw new BandCanNotFoundException();
            boolean isGreater = collectionManager.replaceIfGreater(Integer.parseInt(arg), bandToCompare);
            if (!oldBand.getOwner().equals(user)) throw new PermissionDeniedException();
            if (isGreater) databaseCollectionManager.updateBandById(Integer.parseInt(arg), bandRaw);
            ResponseOutputDeliver.appendLn("Band successfully replaced!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputDeliver.appendLn("Executing: '" + getName() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputDeliver.appendError("Collection is empty!");
        } catch (BandCanNotFoundException exception) {
            ResponseOutputDeliver.appendError("There is no band with such a key");
        } catch (PermissionDeniedException e) {
            ResponseOutputDeliver.appendError("Недостаточно прав для замены объекта!");
        } catch (DatabaseHandlingException exception) {
            ResponseOutputDeliver.appendError("Ошибка соединения с базой данных!");
        }
        return false;
    }
}
