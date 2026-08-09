package com.core.os.search.service;

import com.core.os.search.model.SearchResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UniversalSearchEngine {

    private static final Logger log = LoggerFactory.getLogger(UniversalSearchEngine.class);

    public interface SearchProvider {
        List<SearchResultItem> search(String query);
    }

    private final List<SearchProvider> providers = new ArrayList<>();

    public void registerProvider(SearchProvider provider) {
        this.providers.add(provider);
        log.info("Registered search provider: {}", provider.getClass().getSimpleName());
    }

    public List<SearchResultItem> searchAll(String query) {
        List<SearchResultItem> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }

        log.debug("Executing universal search for query: '{}'", query);
        for (SearchProvider provider : providers) {
            try {
                List<SearchResultItem> items = provider.search(query.trim().toLowerCase());
                results.addAll(items);
            } catch (Exception ex) {
                log.error("Search provider {} error: {}", provider.getClass().getSimpleName(), ex.getMessage(), ex);
            }
        }
        log.debug("Universal search for '{}' completed with {} total results", query, results.size());
        return results;
    }
}
