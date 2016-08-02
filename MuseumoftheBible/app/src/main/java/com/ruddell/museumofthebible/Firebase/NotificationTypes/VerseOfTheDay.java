package com.ruddell.museumofthebible.Firebase.NotificationTypes;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 7/28/16.
 */
public class VerseOfTheDay {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "VerseOfTheDay";


    private String book;
    private int chapter;
    private int verse;
    private String text;

    public VerseOfTheDay(final String book, final int chapter, final int verse, final String text) {
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
        this.text = text;
    }

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(final int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(final int verse) {
        this.verse = verse;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
