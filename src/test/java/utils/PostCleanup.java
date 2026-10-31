package utils;

import core.DriverFactory;
import pages.MySitePage;
import pages.PostTab;
import pages.PostsListPage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Memorise les articles crees par UN test, puis les supprime definitivement a la fin du test.
 *
 * Une instance par test (champ de la classe de test, recree par JUnit a chaque test) :
 * aucun etat partage entre tests paralleles.
 *
 * Le nettoyage est "au mieux" : s'il echoue, on l'ecrit dans la console mais on ne fait pas
 * echouer le test (le resultat du test doit refleter le scenario teste, pas le menage).
 * Les titres etant uniques, un reste eventuel ne peut pas perturber un autre test.
 */
public class PostCleanup {

    private final Map<String, PostTab> createdPosts = new LinkedHashMap<>();

    /** A appeler des qu'un article est cree, avec l'onglet ou il se trouvera. */
    public void register(String title, PostTab tab) {
        createdPosts.put(title, tab);
    }

    /** Si l'article a change d'onglet pendant le test (ex : mis a la corbeille). */
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
            // On repart de l'ecran d'accueil pour ne pas dependre de l'ecran ou le test s'est arrete.
            DriverFactory.get().terminateApp("org.wordpress.android");
            DriverFactory.get().activateApp("org.wordpress.android");
            PostsListPage list = new MySitePage().waitUntilLoaded().openPosts();
            createdPosts.forEach((title, tab) -> {
                try {
                    list.deletePermanently(title, tab);
                } catch (RuntimeException e) {
                    System.err.println("[Cleanup] Article non supprime '" + title + "' : " + e.getMessage());
                }
            });
        } catch (RuntimeException e) {
            System.err.println("[Cleanup] Nettoyage impossible : " + e.getMessage());
        } finally {
            createdPosts.clear();
        }
    }
}
