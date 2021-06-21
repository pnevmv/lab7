package server.commands;

import common.exceptions.WrongAmountOfElementsException;
import common.interaction.User;
import server.utility.CollectionManager;
import server.utility.ResponseOutputDeliver;

import java.time.LocalDateTime;

/**
 * Command 'info'. Prints information about the collection.
 */
public class InfoCommand extends AbstractCommand{
    private CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        super("info", "Displaying information about a collection");
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command.
     * @return Command exit status.
     */
    @Override
    public boolean execute(String argument, Object objArgument, User user) {
        try {
            if (!argument.isEmpty() || objArgument != null) throw new WrongAmountOfElementsException();
            LocalDateTime lastInitTime = collectionManager.getLastInitTime();
            String lastInitTimeString = (lastInitTime == null) ? "no initialization has occurred in this session yet" :
                    lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();
            ResponseOutputDeliver.appendLn("Collection information:\n Type:" + collectionManager.collectionType()
                    + "\n Amount of elements: " + collectionManager.collectionSize()
                    + "\n Last initialization date: " + lastInitTimeString);
            return true;
        } catch (WrongAmountOfElementsException exc) {
            ResponseOutputDeliver.appendError("Executing: '" + getName() + "'");
        }
        return false;
    }
}
