package pages;

import org.openqa.selenium.By;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Ecran "Date & Time" de publication + selecteurs de date et d'heure Android.
 *
 * Le calendrier Android identifie chaque jour par un libelle d'accessibilite
 * "18 November 2026" : on cible donc le jour par sa DATE, pas par sa position a l'ecran.
 */
public class PublishDatePage extends BasePage {

    private static final By DATE_ROW = id("publish_time_and_date_container");
    private static final By DATE_VALUE = id("publish_time_and_date");
    private static final By CALENDAR = androidId("datePicker");
    private static final By NEXT_MONTH = androidId("next");
    private static final By PREVIOUS_MONTH = androidId("prev");
    private static final By TIME_PICKER = androidId("timePicker");
    private static final By DIALOG_OK = androidId("button1");

    private static final By DAY_CELL = uiSelector(
            "new UiSelector().resourceId(\"android:id/month_view\").childSelector(new UiSelector().clickable(true))");

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    public PublishDatePage waitUntilLoaded() {
        visible(DATE_VALUE);
        return this;
    }

    /** Texte affiche, ex : "Schedule for: Nov 18, 2026, 12:45 PM" ou "Backdated for: Sep 1, 2026, ..." */
    public String dateLabel() {
        return textOf(DATE_VALUE);
    }

    /**
     * Choisit une date dans le calendrier puis garde l'heure proposee.
     * Le calendrier s'ouvre sur le mois courant : on avance/recule du nombre de mois necessaire.
     */
    public PublishDatePage pickDate(LocalDate date) {
        click(DATE_ROW);
        visible(CALENDAR);
        // Le calendrier s'ouvre sur le mois de la date deja choisie (pas forcement le mois courant) :
        // on lit le mois affiche, puis on avance/recule du nombre de mois necessaire.
        long monthsToMove = ChronoUnit.MONTHS.between(displayedMonth(), YearMonth.from(date));
        By arrow = monthsToMove >= 0 ? NEXT_MONTH : PREVIOUS_MONTH;
        for (long i = 0; i < Math.abs(monthsToMove); i++) {
            click(arrow);
        }
        click(a11y(date.format(DAY_LABEL)));
        click(DIALOG_OK);          // valide la date
        visible(TIME_PICKER);
        click(DIALOG_OK);          // garde l'heure proposee
        waitUntilLoaded();
        return this;
    }

    /** Mois affiche par le calendrier, deduit du libelle d'un jour visible ("01 November 2026"). */
    private YearMonth displayedMonth() {
        String anyDay = visible(DAY_CELL).getAttribute("content-desc");
        return YearMonth.from(LocalDate.parse(anyDay, DAY_LABEL));
    }

    public PostSettingsPage backToSettings() {
        pressBack();
        return new PostSettingsPage().waitUntilLoaded();
    }
}
