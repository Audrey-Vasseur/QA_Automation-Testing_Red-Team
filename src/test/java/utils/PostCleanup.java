package utils;

import core.DriverFactory;
import pages.MySitePage;
import pages.PostTab;
import pages.PostsListPage;

import java.util.LinkedHashMap;
import java.util.Map;

public class PostCleanup {
    private final Map<String, PostTab> createdPosts = new LinkedHashMap<>();

    public void register(String title, PostTab tab) {
        createdPosts.put(title, tab);
    }

    public void moved(String title, PostTab newTab) {
        createdPosts.replace(title, newTab);
    }

    public void forget(String title) {
        createdPosts.remove(title);
    }

    public void deleteAll() {
        if (createdPosts.isEmpty() || !DriverFactory.isStarted()) {
            return;
        }
        try {
            DriverFactory.get().terminateApp("org.wordpress.android");
            DriverFactory.get().activateApp("org.wordpress.android");
            PostsListPage list = new MySitePage().waitUntilLoaded().openPosts();
            createdPosts.forEach((title, tab) -> {
                try {
                    list.deletePermanently(title, tab);
                } catch (RuntimeException e) {
                    System.err.println("[Cleanup] Post not deleted '" + title + "' : " + e.getMessage());
                }
            });
        } catch (RuntimeException e) {
            System.err.println("[Cleanup] Cleanup failed: " + e.getMessage());
        } finally {
            createdPosts.clear();
        }
    }
}
