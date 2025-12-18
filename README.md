### TripIt Flight Extractor (Kotlin + Selenide/Selenium)

Extract your past flight segments from TripIt into CSV using Selenide (Selenium under the hood). WebDriverManager is used to auto‑download the correct browser driver.

#### Requirements
- Java 17+
- Maven 3.9+

#### Configure credentials and runtime
Set environment variables (required + optional):
```
export TRIPIT_USERNAME="you@example.com"
export TRIPIT_PASSWORD="your_password"
# Optional: run a visible browser for debugging (default is headless)
export TRIPIT_HEADED=true   # accepts true/1/yes/on (case-insensitive)
# Optional: choose browser (default: chrome). Supported: chrome, firefox, edge
export TRIPIT_BROWSER=chrome
```

#### Build
```bash
# Build the project
mvn clean package -DskipTests

# Build a Fat JAR (executable JAR with all dependencies)
# The output will be: target/tripit-extractor-0.1.0-SNAPSHOT-fat.jar
mvn clean package -DskipTests

# Build a Native Image (requires GraalVM)
# The output will be: target/tripit-extractor
mvn clean package -Pnative -DskipTests
```

#### Run
```bash
# Run using the Fat JAR
java -jar target/tripit-extractor-0.1.0-SNAPSHOT-fat.jar --out flights.csv

# Run the Native Image
./target/tripit-extractor --out flights.csv

# Run via Maven exec plugin
mvn exec:java -Dexec.args="--out flights.csv"
```
Options:
- `--start-date YYYY-MM-DD` lower bound, exclusive. Processing stops when a flight is on or before this date. If omitted, extracts all past flights.
- `--until-date YYYY-MM-DD` upper bound, inclusive. Only include flights on or before this date. Alias: `--end-date`.
- `--out path` output CSV path. Default `./flights.csv`.
- `--headed` launch a visible browser (default is headless). Useful for completing MFA/2FA if prompted.

Headed mode precedence:
- CLI `--headed` > env `TRIPIT_HEADED` > default (false)

Browser selection:
- Env `TRIPIT_BROWSER` (default `chrome`). The following are recognized: `chrome`, `chromium`, `firefox`, `edge`.
- WebDriverManager automatically sets up the right driver; no manual downloads needed.

CSV columns:
```
flight_date,flight_time,origin,destination,flight_number
```

CSV writing behavior:
- Rows are written incrementally as they are discovered, so partial results are preserved if the run stops mid‑way.
- The file (and its parent directory) is created if missing; a header row is written when the file is empty.

#### Notes
- Selectors are centralized in `src/main/kotlin/com/example/tripit/Selectors.kt` for easy updates if TripIt’s UI changes.
- The scraper rate-limits interactions slightly to mimic human behavior.
- If login fails due to MFA/2FA, re-run with `--headed` (or set `TRIPIT_HEADED=true`) and complete authentication manually in the opened browser window, then re-run headless next time.
- Cookie consent: when a cookie banner is shown, the scraper will automatically attempt to decline all optional cookies (best-effort) using common consent-manager selectors. This runs after each main navigation and is safe if no banner appears.
