### TripIt Flight Extractor (Kotlin + Playwright)

Extract your past flight segments from TripIt into CSV using Playwright.

#### Requirements
- Java 17+
- Maven 3.9+

#### Configure credentials
Set environment variables:
```
export TRIPIT_USERNAME="you@example.com"
export TRIPIT_PASSWORD="your_password"
# Optional: run a visible browser for debugging (default is headless)
export TRIPIT_HEADED=true   # accepts true/1/yes/on (case-insensitive)
```

#### Build
```
mvn -q -DskipTests package
```

#### Run
```
mvn -q exec:java -Dexec.args="--start-date 2023-01-01 --out flights.csv"
```
Options:
- `--start-date YYYY-MM-DD` filter segments from this date (inclusive). If omitted, extracts all past flights.
- `--out path` output CSV path. Default `./flights.csv`.
- `--headed` launch a visible browser (default is headless). Useful for completing MFA/2FA if prompted.

Headed mode precedence:
- CLI `--headed` > env `TRIPIT_HEADED` > default (false)

CSV columns:
```
flight_date,flight_time,origin,destination,flight_number
```

#### Notes
- Selectors are centralized in `src/main/kotlin/com/example/tripit/Selectors.kt` for easy updates if TripIt’s UI changes.
- The scraper rate-limits interactions slightly to mimic human behavior.
- If login fails due to MFA/2FA, re-run with `--headed` (or set `TRIPIT_HEADED=true`) and complete authentication manually in the opened browser window, then re-run headless next time.
 - Cookie consent: when a cookie banner is shown, the scraper will automatically attempt to decline all optional cookies (best-effort) using common consent-manager selectors. This runs after each main navigation and is safe if no banner appears.
