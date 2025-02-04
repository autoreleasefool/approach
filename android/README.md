# Approach, for Android

## Maintenance

### App Size

[Ruler](https://github.com/spotify/ruler) is a tool that analyzes the final APK to determine modules and dependencies contributing the most to the size of the app.

Ruler has been set up and can be run with `gradlew analyze<Variant>Build` to generate a report for analysis.

### Dependencies

Occasionally, we should run `./gradlew dependencyUpdates` to confirm we're running the latest versions of dependencies.

This task is configured and provided by the plugin [`se.ascp.gradle.gradle-versions-filter`](https://github.com/janderssonse/gradle-versions-filter-plugin)
