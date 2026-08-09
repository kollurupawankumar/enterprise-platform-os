package com.core.os.search.service;

import com.core.os.search.model.SearchResultItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UniversalSearchEngine {

    public interface SearchProvider {
        List<SearchResultItem> search(String query);
    }

    private final List<SearchProvider> providers = new ArrayList<>();

    public void registerProvider(SearchProvider provider) {
        this.providers.add(provider);
    }

    public List<SearchResultItem> searchAll(String query) {
        List<SearchResultItem> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }

        for (SearchProvider provider : providers) {
            try {
                results.addAll(provider.search(query.trim().toLowerCase()));
            } catch (Exception ex) {
                System.err.println("Search provider error: " + ex.getMessage());
            }
        }
        return results;
    }
}
