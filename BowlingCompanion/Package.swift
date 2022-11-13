// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BaseFormFeature", targets: ["BaseFormFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "FeatureFlagListFeature", targets: ["FeatureFlagListFeature"]),
		.library(name: "GameEditorFeature", targets: ["GameEditorFeature"]),
		.library(name: "GamesListFeature", targets: ["GamesListFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "ScoreSheetFeature", targets: ["ScoreSheetFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SeriesSidebarFeature", targets: ["SeriesSidebarFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),

		// MARK: - DataProviders
		.library(name: "AlleysDataProvider", targets: ["AlleysDataProvider"]),
		.library(name: "AlleysDataProviderInterface", targets: ["AlleysDataProviderInterface"]),
		.library(name: "BowlersDataProvider", targets: ["BowlersDataProvider"]),
		.library(name: "BowlersDataProviderInterface", targets: ["BowlersDataProviderInterface"]),
		.library(name: "FramesDataProvider", targets: ["FramesDataProvider"]),
		.library(name: "FramesDataProviderInterface", targets: ["FramesDataProviderInterface"]),
		.library(name: "GamesDataProvider", targets: ["GamesDataProvider"]),
		.library(name: "GamesDataProviderInterface", targets: ["GamesDataProviderInterface"]),
		.library(name: "LeaguesDataProvider", targets: ["LeaguesDataProvider"]),
		.library(name: "LeaguesDataProviderInterface", targets: ["LeaguesDataProviderInterface"]),
		.library(name: "SeriesDataProvider", targets: ["SeriesDataProvider"]),
		.library(name: "SeriesDataProviderInterface", targets: ["SeriesDataProviderInterface"]),

		// MARK: - Services
		.library(name: "FeatureFlagService", targets: ["FeatureFlagService"]),
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),

		// MARK: - Libraries
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "FeatureFlagLibrary", targets: ["FeatureFlagLibrary"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
		.library(name: "SharedModelsLibraryMocks", targets: ["SharedModelsLibraryMocks"]),
		.library(name: "SharedPersistenceModelsLibrary", targets: ["SharedPersistenceModelsLibrary"]),
		.library(name: "SortingLibrary", targets: ["SortingLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-async-algorithms", from: "0.0.3"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.0.0"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AlleysListFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"PersistenceServiceInterface",
				"SharedModelsLibrary",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "AlleysListFeatureTests", dependencies: ["AlleysListFeature"]),
		.target(
			name: "AppFeature",
			dependencies: [
				"AlleysListFeature",
				"BowlersListFeature",
				"FeatureFlagServiceInterface",
				"SettingsFeature",
			]
		),
		.testTarget(name: "AppFeatureTests", dependencies: ["AppFeature"]),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"PersistenceServiceInterface",
				"SharedModelsLibrary",
			]
		),
		.target(
			name: "BaseFormFeature",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "BaseFormFeatureTests", dependencies: ["BaseFormFeature"]),
		.testTarget(name: "BowlerEditorFeatureTests", dependencies: ["BowlerEditorFeature"]),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"BowlersDataProviderInterface",
				"BowlerEditorFeature",
				"LeaguesListFeature",
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				"BowlersListFeature",
				"SharedModelsLibraryMocks",
			]
		),
		.target(
			name: "FeatureFlagListFeature",
			dependencies: [
				"FeatureFlagServiceInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "FeatureFlagListFeatureTests", dependencies: ["FeatureFlagListFeature"]),
		.target(
			name: "GameEditorFeature",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "GameEditorFeatureTests", dependencies: ["GameEditorFeature"]),
		.target(
			name: "GamesListFeature",
			dependencies: [
				"DateTimeLibrary",
				"GamesDataProviderInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "GamesListFeatureTests", dependencies: ["GamesListFeature"]),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(name: "LeagueEditorFeatureTests", dependencies: ["LeagueEditorFeature"]),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeaguesDataProviderInterface",
				"LeagueEditorFeature",
				"SeriesListFeature",
			]
		),
		.testTarget(name: "LeaguesListFeatureTests", dependencies: ["LeaguesListFeature"]),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "ScoreSheetFeature",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "ScoreSheetFeatureTests", dependencies: ["ScoreSheetFeature"]),
		.testTarget(name: "SeriesEditorFeatureTests", dependencies: ["SeriesEditorFeature"]),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"DateTimeLibrary",
				"SeriesEditorFeature",
				"SeriesSidebarFeature",
			]
		),
		.testTarget(name: "SeriesListFeatureTests", dependencies: ["SeriesListFeature"]),
		.target(
			name: "SeriesSidebarFeature",
			dependencies: [
				"GamesDataProviderInterface",
				"GameEditorFeature",
			]
		),
		.testTarget(name: "SeriesSidebarFeatureTests", dependencies: ["SeriesSidebarFeature"]),
		.target(
			name: "SettingsFeature",
			dependencies: [
				"FeatureFlagListFeature",
				"FeatureFlagServiceInterface",
			]
		),
		.testTarget(name: "SettingsFeatureTests", dependencies: ["SettingsFeature"]),

		// MARK: - DataProviders
		.target(
			name: "AlleysDataProvider",
			dependencies: [
				"AlleysDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
			]
		),
		.target(
			name: "AlleysDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "AlleysDataProviderTests",
			dependencies: [
				"AlleysDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.target(
			name: "BowlersDataProvider",
			dependencies: [
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
			]
		),
		.target(
			name: "BowlersDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "BowlersDataProviderTests",
			dependencies: [
				"BowlersDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
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
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "FramesDataProviderTests",
			dependencies: [
				"FramesDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
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
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "GamesDataProviderTests",
			dependencies: [
				"GamesDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				"LeaguesDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
			]
		),
		.target(
			name: "LeaguesDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "LeaguesDataProviderTests",
			dependencies: [
				"LeaguesDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.target(
			name: "SeriesDataProvider",
			dependencies: [
				"SeriesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "SeriesDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "SeriesDataProviderTests",
			dependencies: [
				"SeriesDataProvider",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),

		// MARK: - Services
		.target(name: "FeatureFlagService", dependencies: ["FeatureFlagServiceInterface"]),
		.target(
			name: "FeatureFlagServiceInterface",
			dependencies: [
				"FeatureFlagLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "FeatureFlagServiceTests", dependencies: ["FeatureFlagService"]),
		.target(name: "FileManagerService", dependencies: ["FileManagerServiceInterface"]),
		.target(
			name: "FileManagerServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "FileManagerServiceTests", dependencies: ["FileManagerService"]),
		.target(
			name: "PersistenceService",
			dependencies: [
				"FileManagerServiceInterface",
				"PersistenceServiceInterface",
				"SharedPersistenceModelsLibrary",
			]
		),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),
		.target(
			name: "PreferenceService",
			dependencies: [
				"PreferenceServiceInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.target(
			name: "PreferenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "PreferenceServiceTests", dependencies: ["PreferenceService"]),
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "RecentlyUsedServiceTests", dependencies: ["RecentlyUsedService"]),

		// MARK: - Libraries
		.target(name: "DateTimeLibrary", dependencies: []),
		.testTarget(name: "DateTimeLibraryTests", dependencies: ["DateTimeLibrary"]),
		.target(name: "FeatureFlagLibrary", dependencies: []),
		.testTarget(name: "FeatureFlagLibraryTests", dependencies: ["FeatureFlagLibrary"]),
		.target(name: "SharedModelsLibrary", dependencies: []),
		.target(
			name: "SharedModelsLibraryMocks",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.target(
			name: "SharedPersistenceModelsLibrary",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				.product(name: "GRDB", package: "grdb.swift"),
			]
		),
		.target(name: "SortingLibrary", dependencies: []),
		.testTarget(name: "SortingLibraryTests", dependencies: ["SortingLibrary"]),
	]
)
