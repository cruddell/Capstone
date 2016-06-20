package com.ruddell.museumofthebible.Bible;

import android.database.Cursor;

import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.BibleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BibleChapterHelper {

    /**
     * An item representing a book of the Bible.
     */
    public static class BibleChapterItem {
        public final String chapterName;

        public BibleChapterItem(String chapterName) {
            this.chapterName = chapterName;
        }

        @Override
        public String toString() {
            return chapterName;
        }
    }
}
