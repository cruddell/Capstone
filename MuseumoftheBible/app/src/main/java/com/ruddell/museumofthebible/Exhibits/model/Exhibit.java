package com.ruddell.museumofthebible.Exhibits.model;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/27/16.
 */

public class Exhibit {
    public int id;
    public String title;
    public String audioFile;
    public String imageName;

    public Exhibit(final int id, final String title, final String audioFile, final String imageName) {
        this.id = id;
        this.title = title;
        this.audioFile = audioFile;
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "Exhibit{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", audioFile='" + audioFile + '\'' +
                ", imageName='" + imageName + '\'' +
                '}';
    }
}
