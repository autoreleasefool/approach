// swift-tools-version: 5.7.1
// swiftlint:disable file_length

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	defaultLocalization: "en",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "FeatureFlagsListFeature", targets: ["FeatureFlagsListFeature"]),
		.library(name: "GameEditorFeature", targets: ["GameEditorFeature"]),
		.library(name: "GearEditorFeature", targets: ["GearEditorFeature"]),
		.library(name: "GearListFeature", targets: ["GearListFeature"]),
		.library(name: "LaneEditorFeature", targets: ["LaneEditorFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "OpponentEditorFeature", targets: ["OpponentEditorFeature"]),
		.library(name: "OpponentsListFeature", targets: ["OpponentsListFeature"]),
		.library(name: "ScoreSheetFeature", targets: ["ScoreSheetFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SeriesSidebarFeature", targets: ["SeriesSidebarFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "StatisticsWidgetsFeature", targets: ["StatisticsWidgetsFeature"]),
		.library(name: "TeamEditorFeature", targets: ["TeamEditorFeature"]),
		.library(name: "TeamsListFeature", targets: ["TeamsListFeature"]),

		// MARK: - Data Providers
		.library(name: "AlleysDataProvider", targets: ["AlleysDataProvider"]),
		.library(name: "AlleysDataProviderInterface", targets: ["AlleysDataProviderInterface"]),
		.library(name: "AveragesDataProvider", targets: ["AveragesDataProvider"]),
		.library(name: "AveragesDataProviderInterface", targets: ["AveragesDataProviderInterface"]),
		.library(name: "BowlersDataProvider", targets: ["BowlersDataProvider"]),
		.library(name: "BowlersDataProviderInterface", targets: ["BowlersDataProviderInterface"]),
		.library(name: "FramesDataProvider", targets: ["FramesDataProvider"]),
		.library(name: "FramesDataProviderInterface", targets: ["FramesDataProviderInterface"]),
		.library(name: "GamesDataProvider", targets: ["GamesDataProvider"]),
		.library(name: "GamesDataProviderInterface", targets: ["GamesDataProviderInterface"]),
		.library(name: "GearDataProvider", targets: ["GearDataProvider"]),
		.library(name: "GearDataProviderInterface", targets: ["GearDataProviderInterface"]),
		.library(name: "LanesDataProvider", targets: ["LanesDataProvider"]),
		.library(name: "LanesDataProviderInterface", targets: ["LanesDataProviderInterface"]),
		.library(name: "LeaguesDataProvider", targets: ["LeaguesDataProvider"]),
		.library(name: "LeaguesDataProviderInterface", targets: ["LeaguesDataProviderInterface"]),
		.library(name: "OpponentsDataProvider", targets: ["OpponentsDataProvider"]),
		.library(name: "OpponentsDataProviderInterface", targets: ["OpponentsDataProviderInterface"]),
		.library(name: "SeriesDataProvider", targets: ["SeriesDataProvider"]),
		.library(name: "SeriesDataProviderInterface", targets: ["SeriesDataProviderInterface"]),
		.library(name: "TeamsDataProvider", targets: ["TeamsDataProvider"]),
		.library(name: "TeamsDataProviderInterface", targets: ["TeamsDataProviderInterface"]),

		// MARK: - Services
		.library(name: "AnalyticsService", targets: ["AnalyticsService"]),
		.library(name: "AnalyticsServiceInterface", targets: ["AnalyticsServiceInterface"]),
		.library(name: "FeatureFlagsService", targets: ["FeatureFlagsService"]),
		.library(name: "FeatureFlagsServiceInterface", targets: ["FeatureFlagsServiceInterface"]),
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AssetsLibrary", targets: ["AssetsLibrary"]),
		.library(name: "BaseFormLibrary", targets: ["BaseFormLibrary"]),
		.library(name: "ConstantsLibrary", targets: ["ConstantsLibrary"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "EquatableLibrary", targets: ["EquatableLibrary"]),
		.library(name: "FeatureActionLibrary", targets: ["FeatureActionLibrary"]),
		.library(name: "FeatureFlagsLibrary", targets: ["FeatureFlagsLibrary"]),
		.library(name: "FoundationExtensionsLibrary", targets: ["FoundationExtensionsLibrary"]),
		.library(name: "ResourceListLibrary", targets: ["ResourceListLibrary"]),
		.library(name: "ResourcePickerLibrary", targets: ["ResourcePickerLibrary"]),
		.library(name: "SharedModelsFetchableLibrary", targets: ["SharedModelsFetchableLibrary"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
		.library(name: "SharedModelsMocksLibrary", targets: ["SharedModelsMocksLibrary"]),
		.library(name: "SharedModelsPersistableLibrary", targets: ["SharedModelsPersistableLibrary"]),
		.library(name: "SharedModelsViewsLibrary", targets: ["SharedModelsViewsLibrary"]),
		.library(name: "SortOrderLibrary", targets: ["SortOrderLibrary"]),
		.library(name: "SortingLibrary", targets: ["SortingLibrary"]),
		.library(name: "StringsLibrary", targets: ["StringsLibrary"]),
		.library(name: "SwiftUIExtensionsLibrary", targets: ["SwiftUIExtensionsLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),

	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "0.0.4"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "0.1.2"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.49.2"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.10.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.6.0"),
		.package(url: "https://github.com/TelemetryDeck/SwiftClient.git", from: "1.4.2"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AlleyEditorFeature",
			dependencies: [
				"BaseFormLibrary",
				"LaneEditorFeature",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "AlleyEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleyEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				"AlleyEditorFeature",
				"AlleysDataProviderInterface",
				"ResourceListLibrary",
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "AppFeature",
			dependencies: [
				"AlleysListFeature",
				"AnalyticsServiceInterface",
				"BowlersListFeature",
				"GearListFeature",
				"SettingsFeature",
			]
		),
		.testTarget(
			name: "AppFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AppFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BaseFormLibrary",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "BowlerEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"AveragesDataProviderInterface",
				"BowlerEditorFeature",
				"BowlersDataProviderInterface",
				"FeatureFlagsServiceInterface",
				"LeaguesListFeature",
				"SortOrderLibrary",
				"StatisticsWidgetsFeature",
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FeatureFlagsListFeature",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"FeatureActionLibrary",
				"FeatureFlagsServiceInterface",
			]
		),
		.testTarget(
			name: "FeatureFlagsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GameEditorFeature",
			dependencies: [
				"FeatureActionLibrary",
				"SharedModelsLibrary",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "GameEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GameEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearEditorFeature",
			dependencies: [
				"BaseFormLibrary",
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"ResourcePickerLibrary",
			]
		),
		.testTarget(
			name: "GearEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearListFeature",
			dependencies: [
				"GearDataProviderInterface",
				"GearEditorFeature",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "GearListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LaneEditorFeature",
			dependencies: [
				"FeatureActionLibrary",
				"LanesDataProviderInterface",
				"PersistenceServiceInterface",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "LaneEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaneEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"BaseFormLibrary",
				"PersistenceServiceInterface",
				"ResourcePickerLibrary",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "LeagueEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeagueEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeagueEditorFeature",
				"LeaguesDataProviderInterface",
				"RecentlyUsedServiceInterface",
				"SeriesListFeature",
			]
		),
		.testTarget(
			name: "LeaguesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeaguesListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "OpponentEditorFeature",
			dependencies: [
				"BaseFormLibrary",
				"OpponentsDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "OpponentEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "OpponentsListFeature",
			dependencies: [
				"OpponentEditorFeature",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "OpponentsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentsListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "ScoreSheetFeature",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"FeatureActionLibrary",
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoreSheetFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreSheetFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"BaseFormLibrary",
				"PersistenceServiceInterface",
				"ResourcePickerLibrary",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"ResourceListLibrary",
				"SeriesDataProviderInterface",
				"SeriesEditorFeature",
				"SeriesSidebarFeature",
			]
		),
		.testTarget(
			name: "SeriesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesSidebarFeature",
			dependencies: [
				"GameEditorFeature",
				"GamesDataProviderInterface",
			]
		),
		.testTarget(
			name: "SeriesSidebarFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesSidebarFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SettingsFeature",
			dependencies: [
				"ConstantsLibrary",
				"FeatureFlagsListFeature",
				"FoundationExtensionsLibrary",
				"OpponentsListFeature",
			]
		),
		.testTarget(
			name: "SettingsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SettingsFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "StatisticsWidgetsFeature",
			dependencies: [
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"StatisticsWidgetsFeature",
			]
		),
		.target(
			name: "TeamEditorFeature",
			dependencies: [
				"BaseFormLibrary",
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"ResourcePickerLibrary",
			]
		),
		.testTarget(
			name: "TeamEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"TeamEditorFeature",
			]
		),
		.target(
			name: "TeamsListFeature",
			dependencies: [
				"SortOrderLibrary",
				"TeamEditorFeature",
				"TeamsDataProviderInterface",
			]
		),
		.testTarget(
			name: "TeamsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"TeamsListFeature",
			]
		),

		// MARK: - Data Providers
		.target(
			name: "AlleysDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"AlleysDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "AlleysDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "AlleysDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "AveragesDataProvider",
			dependencies: [
				"AveragesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "AveragesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "AveragesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AveragesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "BowlersDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "BowlersDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "BowlersDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FramesDataProvider",
			dependencies: [
				"FramesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "FramesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "FramesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FramesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GamesDataProvider",
			dependencies: [
				"GamesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "GamesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "GamesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"GearDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "GearDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "GearDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LanesDataProvider",
			dependencies: [
				"LanesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "LanesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "LanesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LanesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"LeaguesDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "LeaguesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "LeaguesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeaguesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "OpponentsDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"OpponentsDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "OpponentsDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "OpponentsDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentsDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesDataProvider",
			dependencies: [
				"PersistenceServiceInterface",
				"SeriesDataProviderInterface",
			]
		),
		.target(
			name: "SeriesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "SeriesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "TeamsDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
				"TeamsDataProviderInterface",
			]
		),
		.target(
			name: "TeamsDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "TeamsDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"TeamsDataProvider",
			]
		),

		// MARK: - Services
		.target(
			name: "AnalyticsService",
			dependencies: [
				.product(name: "TelemetryClient", package: "SwiftClient"),
				"AnalyticsServiceInterface",
				"ConstantsLibrary",
			]
		),
		.target(
			name: "AnalyticsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "AnalyticsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnalyticsService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FeatureFlagsService",
			dependencies: [
				"FeatureFlagsServiceInterface",
			]
		),
		.target(
			name: "FeatureFlagsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"FeatureFlagsLibrary",
			]
		),
		.testTarget(
			name: "FeatureFlagsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FileManagerService",
			dependencies: [
				"FileManagerServiceInterface",
			]
		),
		.target(
			name: "FileManagerServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "FileManagerServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FileManagerService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "PersistenceService",
			dependencies: [
				"FileManagerServiceInterface",
				"PersistenceServiceInterface",
				"SharedModelsPersistableLibrary",
			]
		),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsFetchableLibrary",
			]
		),
		.testTarget(
			name: "PersistenceServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PersistenceService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "PreferenceService",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "PreferenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "PreferenceServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PreferenceService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "RecentlyUsedService",
			dependencies: [
				"PreferenceServiceInterface",
				"RecentlyUsedServiceInterface",
			]
		),
		.target(
			name: "RecentlyUsedServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "RecentlyUsedServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"RecentlyUsedService",
				"SharedModelsMocksLibrary",
			]
		),

		// MARK: - Libraries
		.target(
			name: "AssetsLibrary",
			dependencies: []
		),
		.target(
			name: "BaseFormLibrary",
			dependencies: [
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.target(
			name: "ConstantsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "DateTimeLibrary",
			dependencies: []
		),
		.testTarget(
			name: "DateTimeLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DateTimeLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "EquatableLibrary",
			dependencies: []
		),
		.testTarget(
			name: "EquatableLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"EquatableLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FeatureActionLibrary",
			dependencies: []
		),
		.target(
			name: "FeatureFlagsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "FeatureFlagsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FoundationExtensionsLibrary",
			dependencies: []
		),
		.target(
			name: "ResourceListLibrary",
			dependencies: [
				"EquatableLibrary",
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ResourceListLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourceListLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "ResourcePickerLibrary",
			dependencies: [
				"ViewsLibrary",
			]
		),
		.target(
			name: "SharedModelsFetchableLibrary",
			dependencies: [
				"SharedModelsLibrary",
			]
		),
		.target(
			name: "SharedModelsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "SharedModelsMocksLibrary",
			dependencies: [
				"SharedModelsLibrary",
			]
		),
		.target(
			name: "SharedModelsPersistableLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
				"SharedModelsLibrary",
			]
		),
		.target(
			name: "SharedModelsViewsLibrary",
			dependencies: [
				"DateTimeLibrary",
				"SharedModelsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "SharedModelsViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"SharedModelsViewsLibrary",
			]
		),
		.target(
			name: "SortOrderLibrary",
			dependencies: [
				"FeatureActionLibrary",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "SortOrderLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"SortOrderLibrary",
			]
		),
		.target(
			name: "SortingLibrary",
			dependencies: []
		),
		.testTarget(
			name: "SortingLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"SortingLibrary",
			]
		),
		.target(
			name: "StringsLibrary",
			dependencies: []
		),
		.target(
			name: "SwiftUIExtensionsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "SwiftUIExtensionsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"SwiftUIExtensionsLibrary",
			]
		),
		.target(
			name: "ViewsLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"AssetsLibrary",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "ViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"ViewsLibrary",
			]
		),

	]
)
