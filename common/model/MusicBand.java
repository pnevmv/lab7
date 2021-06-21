package common.model;

import common.interaction.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main character. Is stored in the collection.
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static Integer count = 0;
    private Integer id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long numberOfParticipants; //Поле не может быть null, Значение поля должно быть больше 0
    private String description; //Поле не может быть null
    private MusicGenre genre; //Поле не может быть null
    private Studio studio; //Поле может быть null
    private User owner;

    public MusicBand(int id, String name, Coordinates coordinates, LocalDateTime creationDate, Long numberOfParticipants, String description, MusicGenre genre, Studio studio, User owner) {
        count++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.numberOfParticipants = numberOfParticipants;
        this.description = description;
        this.genre = genre;
        this.studio = studio;
        this.id = id;
        this.owner = owner;
    }

    /**
     * @return ID of the band.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return Owner of the band.
     */
    public User getOwner() {
        return this.owner;
    }
    /**
     * @return Name of the band.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Coordinates of the band.
     */
    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    /**
     * @return Creation date of the band.
     */
    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * @return Number of participants of the band.
     */
    public Long getNumberOfParticipants() {
        return this.numberOfParticipants;
    }

    /**
     * @return Description of the band.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return Genre of the band.
     */
    public MusicGenre getGenre() {
        return this.genre;
    }

    /**
     * @return Studio of the band.
     */
    public Studio getStudio() {
        return this.studio;
    }

    public static Integer getKey() {
        return count;
    }

    @Override
    public int compareTo(MusicBand musicBand) {
        if (this.numberOfParticipants > musicBand.numberOfParticipants) return -1;
        else if (this.numberOfParticipants.equals(musicBand.numberOfParticipants)) return 0;
        else return 1;
    }

    @Override
    public String toString() {
        String info = "";
        info += "Music band №" + id;
        info += " [" + owner.getUsername() + " " + creationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                " в " + creationDate.format(DateTimeFormatter.ofPattern("HH:mm")) + "]";
        info += "\n Name: " + name;
        info += "\n Coordinates: " + coordinates;
        info += "\n Number of participants: " + numberOfParticipants;
        info += "\n Description: " + description;
        info += "\n Music Genre: " + genre;
        if (studio != null) {
            info += "\n Studio: " + studio;
        } else info += "\n Studio: null";
        return info;
    }

    @Override
    public int hashCode() {
        return (name.hashCode() + coordinates.hashCode() + numberOfParticipants.hashCode() + description.hashCode() + genre.hashCode() + studio.hashCode());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof MusicBand) {
            MusicBand bObj = (MusicBand) obj;
            return name.equals(bObj.getName()) && coordinates.equals(bObj.getCoordinates()) &&
                    (numberOfParticipants == bObj.getNumberOfParticipants()) && (genre == bObj.getGenre()) &&
                    (studio == bObj.getStudio());
        }
        return false;
    }
}
