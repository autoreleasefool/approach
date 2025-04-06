# Approach, for Android

## Building

### API Keys

Rename:

- `secrets.default.properties` to `secrets.properties`
- `sentry.default.properties` to `sentry.properties`
- `keystore.default.properties` to `keystore.properties`

For debugging, you can leave the values blank. To create a release build, you will need real values.

## Maintenance

### App Size

[Ruler](https://github.com/spotify/ruler) is a tool that analyzes the final APK to determine modules and dependencies contributing the most to the size of the app.

Ruler has been set up and can be run with `gradlew analyze<Variant>Build` to generate a report for analysis.

### Dependencies

Occasionally, we should run `./gradlew dependencyUpdates` to confirm we're running the latest versions of dependencies.

This task is configured and provided by the plugin [`se.ascp.gradle.gradle-versions-filter`](https://github.com/janderssonse/gradle-versions-filter-plugin)
