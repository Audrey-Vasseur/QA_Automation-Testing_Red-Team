# Failure investigation – UI tests (assignment section 4.3)

Two **genuine** automation failures appeared while the UI tests were being developed on the
emulator (WordPress Android 27.7, Appium UiAutomator2). Both were detected by the tests
themselves and investigated with the evidence that the framework records automatically on failure
(`EvidenceOnFailure`: a screenshot `.png` and the screen's UI hierarchy `.xml` in `evidence/`).

---

## Failure 1 – `StaleElementReferenceException` while typing in the post editor

| Item | Detail |
|---|---|
| Tests affected | UI-01 (create draft) and UI-10 (edit draft) |
| Symptom | `org.openqa.selenium.StaleElementReferenceException` thrown by `sendKeys` in `EditorPage.typeBody` |
| Detected by | JUnit report (`target/surefire-reports`) + stack trace pointing to `EditorPage.java` |
| Evidence | `evidence/FAILED_PostCreationTest_UI-01_...png/.xml` |

**Investigation.** The UI hierarchy saved at the moment of failure showed that the title field was
still **empty** (it displayed its hint `Add title`), even though the test had already "typed" the title.
So the problem started before the line that threw the exception.
`sendKeys` in Appium UiAutomator2 replaces the whole content of a field at once (Android
`ACTION_SET_TEXT`). The WordPress post editor (Gutenberg) is a React Native component: it ignores that
kind of programmatic replacement and re-creates its text views, which makes the element reference stale.
During manual exploration with `adb shell input text` (real key events) typing worked correctly,
which confirmed the hypothesis.

**Root cause.** Wrong interaction technique for this kind of field (test-side problem), **not** an
application defect: a real user types with key events, which the editor handles correctly.

**Corrective action.** `BasePage.typeWithKeyboard` / `clearWithKeyboard`: focus the field, then send real
key events (`Actions.sendKeys`, `AndroidKey.DEL`). Used by `EditorPage` only, where it is needed.

---

## Failure 2 – first characters typed into the wrong field (synchronization issue)

| Item | Detail |
|---|---|
| Test affected | UI-01 |
| Symptom | `AssertionFailedError: Le brouillon 'UI-01 Draft 2b7719d4' doit apparaitre dans l'onglet Drafts ==> expected: <true> but was: <false>` |
| Detected by | the assertion on the Drafts list (the test did its job) |
| Evidence | `evidence/FAILED_PostCreationTest_UI-01_...xml` – the list contained **`UI-01 Draft 2b7719d4Fi`** and the excerpt **`Rst line of the draftSe`** |

**Investigation.** The saved list shows that the draft *was* created, but its title ended with `Fi` and its
text started with `Rst` instead of `First`. The first letters of each line were sent while the keyboard
focus was still on the previous field: the editor needs a short moment to move the focus to the new
paragraph block after a tap or after *Enter*.

**Root cause.** Synchronization issue (the test typed before the application was ready).

**Corrective action.** No fixed `sleep`. `EditorPage.typeBody` now waits explicitly (`WebDriverWait`)
until the focused field is the **empty** new block before typing each line, and `typeTitle` waits until
the whole title is present in the field. The expected result of the test was **not** changed: the assertion
still requires the exact title in the Drafts list.

**Verification.** `PostCreationTest` (UI-01, UI-10) re-run: 2/2 passed; UI-10 also checks that the three
lines of text are stored in the right order after reopening the draft. The same editor code is reused by
UI-05, UI-06, UI-07, UI-08 and UI-11, which all pass.

**Prevention.** All typing in the editor goes through the two page-object methods above (DRY), so the fix
applies to every present and future test. Leftover data from the failed runs did not affect other tests
because every test uses a unique title (`TestData.uniqueTitle`).

---

## Failure 3 – data-driven test (UI-11): 2 rows out of 5 failed on the first run

The data-driven test reports one result per CSV row, so the report showed exactly which data sets failed:
row 2 (French accents) and row 3 (special characters). Rows 1, 4 and 5 passed.

| Row | What the evidence XML showed | Cause | Category |
|---|---|---|---|
| 2 – `Brouillon accentué : été, crème, garçon` | title field contained only `Brouillon accentu` | simulated key events can only produce ASCII characters; typing stopped at `é` | test tool limitation (automation side) |
| 3 – `Special chars & symbols #1 !? (test)` | title field contained `... !? (Test)` | after `?` the Android keyboard capitalizes the next word, like at the start of a sentence | test-data problem (the app behaved as intended) |

**Corrective actions.**
- Row 2: `BasePage.typeWithKeyboard` now pastes non-ASCII text (clipboard + `KEYCODE_PASTE`), which is also a
  normal user action. ASCII text is still typed key by key. The expected result (exact title) is unchanged.
- Row 3: sentence auto-capitalization is the intended behaviour of a title field, so the expected result was
  correct but the data was badly chosen. The row was changed to `Special chars & symbols #1 (test) @ %`,
  which still covers `&`, `#`, `@`, `%` and parentheses without a sentence break before a lowercase word.

**Verification.** UI-11 re-run with the 5 rows (see the Surefire report of `PostTitleDataDrivenTest`).

---

## How we would investigate a failure that happens only in parallel mode

1. Re-run the failing class alone (`mvn test -Dtest=ClassName`) – if it passes, suspect shared state or data.
2. Check that the two threads used different devices (`udid`) and different `systemPort`s (`DevicePool`).
3. Look for data collisions: titles/file names must be unique (`TestData`), cleanup must only delete the test's own data (`PostCleanup`).
4. Compare the evidence (screenshot + XML) of the failing thread with the timing of the other thread in the Surefire report.
