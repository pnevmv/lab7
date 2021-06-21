package server.utility;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import common.exceptions.DatabaseHandlingException;
import common.model.MusicBand;
import common.utility.OutputDeliver;

/**
 * Operates the collection itself.
 */
public class CollectionManager {
    private HashMap<Integer, MusicBand> bandsCollection;
    private LocalDateTime lastInitTime;
    private DatabaseCollectionManager databaseCollectionManager;

    public CollectionManager(DatabaseCollectionManager databaseCollectionManager) {
        this.databaseCollectionManager = databaseCollectionManager;
        loadCollection();
    }

    /**
     * @return Bands collection.
     */
    public HashMap<Integer, MusicBand> getCollection() {
        return bandsCollection;
    }

    /**
     * @return Last initialization time or null if there wasn't initialization.
     */
    public LocalDateTime getLastInitTime() {
        return lastInitTime;
    }

    /**
     * Loads the collection.
     */
    private void loadCollection() {
        try {
            bandsCollection = databaseCollectionManager.getCollection();
            lastInitTime = LocalDateTime.now();
            OutputDeliver.println("Коллекция загружена.");
        } catch (DatabaseHandlingException exception) {
            bandsCollection = new HashMap<>();
            OutputDeliver.printError("Коллекция не может быть загружена!");
        }
    }
    /**
     * @return Name of the collection's type.
     */
    public String collectionType() {
        return bandsCollection.getClass().getName();
    }

    /**
     * @return Size of the collection.
     */
    public int collectionSize() {
        return bandsCollection.size();
    }

    /**
     * @param id ID of the band.
     * @return A band by his ID or null if band isn't found.
     */
    public MusicBand getById(int id) {
        for (Map.Entry entry: bandsCollection.entrySet()) {
            MusicBand value = (MusicBand) entry.getValue();
            int sId = value.getId();
            if (sId == id) {
                return value;
            }
        }
        return null;
    }

    /**
     * @return Average if number of participants or 0 if collection is empty.
     */
    public double getAverageOfNumberOfParticipants() {
        double sum = 0;
        int count = 0;
        for (Map.Entry entry: bandsCollection.entrySet()) {
            MusicBand value = (MusicBand) entry.getValue();
            sum += value.getNumberOfParticipants();
            count++;
        }
        return sum/count;
    }

    /**
     * @param description Start of description of element to find.
     * @return Descriptions.
     */
    public String descriptionFilter(String description) {
        String results = "";
        for (Map.Entry entry: bandsCollection.entrySet()) {
            MusicBand value = (MusicBand) entry.getValue();
            if (value.getDescription().indexOf(description) != -1) {
                results += value;
            }
        }
        return results;
    }

    /**
     * @return All descriptions.
     */
    public String getAllDescriptions() {
        String results = "";
        for (Map.Entry entry: bandsCollection.entrySet()) {
            MusicBand value = (MusicBand) entry.getValue();
            results += value.getDescription() + "\n";
        }
        return results;
    }

    /**
     * @param number of element
     * @param band
     * adds element to collection.
     */
    public void addToCollection(Integer number, MusicBand band) {
        bandsCollection.put(number ,band);
    }

    /**
     * @param number of element
     * removes element from collection.
     */
    public void removeFromCollection(Integer number) {
        bandsCollection.remove(number);
    }

    /**
     * Remove bands greater than the selected one.
     * @param bandToCompare A band to compare with.
     * @return Greater bands list.
     */
    public NavigableSet<MusicBand> getLower(MusicBand bandToCompare) {
        return bandsCollection.values().stream().filter(band -> band.compareTo(bandToCompare) > 0).collect(
                TreeSet::new,
                TreeSet::add,
                TreeSet::addAll
        );
    }

    /**
     * Remove bands whose key greater than the selected one.
     * @param key A band keys to compare with.
     * @return Greater bands list.
     */
    public NavigableSet<MusicBand> getLowerKeyBands(Integer key) {
        return bandsCollection.values().stream().filter(band -> band.getId() < key).collect(
                TreeSet::new,
                TreeSet::add,
                TreeSet::addAll
        );
    }

    /**
     * clears collection.
     */
    public void clearCollection() {
        bandsCollection.clear();
        try {
            databaseCollectionManager.clearCollection();
        } catch (DatabaseHandlingException exception) {
            OutputDeliver.printError("Ошибка доступа к базе данных!");
        }
    }

    /**
     * @return id for next element.
     */
    public Integer generateNextId() {
        if (bandsCollection.isEmpty()) return 1;
        return (bandsCollection.size()) + 1;
    }

    /**
     * @param number of element
     * @param band our band
     * replaces element if it's greater.
     */
    public boolean replaceIfGreater(Integer number, MusicBand band) {
        if (bandsCollection.get(number).compareTo(band) > 0) {
            bandsCollection.put(number, band);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        if (bandsCollection.isEmpty()) return "Collection is empty.";
        String info = "";
        for (Map.Entry entry: bandsCollection.entrySet()) {
            info += "\n" +entry.getValue();
        }
        return info;
    }

}
