package gg.rohan.narwhal.ui;

public class SearchManager {
    private SearchListener searchListener;

    public interface SearchListener {
        void onSearch(String query);
    }

    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    public void triggerSearch(String query) {
        if (searchListener != null) {
            searchListener.onSearch(query);
        }
    }

    public interface Searchable {
        void performSearch(String query);
    }
}
