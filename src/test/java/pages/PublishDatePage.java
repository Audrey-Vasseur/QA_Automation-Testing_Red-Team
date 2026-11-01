package pages;

import org.openqa.selenium.By;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

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

    public String dateLabel() {
        return textOf(DATE_VALUE);
    }

    public PublishDatePage pickDate(LocalDate date) {
        click(DATE_ROW);
        visible(CALENDAR);
        long monthsToMove = ChronoUnit.MONTHS.between(displayedMonth(), YearMonth.from(date));
        By arrow = monthsToMove >= 0 ? NEXT_MONTH : PREVIOUS_MONTH;
        for (long i = 0; i < Math.abs(monthsToMove); i++) {
            click(arrow);
        }
        click(a11y(date.format(DAY_LABEL)));
        click(DIALOG_OK);
        visible(TIME_PICKER);
        click(DIALOG_OK);
        waitUntilLoaded();
        return this;
    }

    private YearMonth displayedMonth() {
        String anyDay = visible(DAY_CELL).getAttribute("content-desc");
        return YearMonth.from(LocalDate.parse(anyDay, DAY_LABEL));
    }

    public PostSettingsPage backToSettings() {
        pressBack();
        return new PostSettingsPage().waitUntilLoaded();
    }
}
