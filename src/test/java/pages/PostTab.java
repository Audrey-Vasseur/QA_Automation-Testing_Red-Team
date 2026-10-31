package pages;

/** Onglets de la liste des articles. Le libelle est aussi l'accessibilityId de l'onglet. */
public enum PostTab {
    PUBLISHED("Published"),
    DRAFTS("Drafts"),
    SCHEDULED("Scheduled"),
    TRASHED("Trashed");

    private final String label;

    PostTab(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
