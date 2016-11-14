
package org.zarroboogs.weibo.adapter;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static String AUTHORITY = "org.zarroboogs.weibo.ui.search.SearchSuggestionProvider";

    static {
        AUTHORITY = "beebo_plus_org.zarroboogs.weibo.ui.search.SearchSuggestionProvider";
    }

    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
