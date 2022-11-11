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

		// MARK: - Services
		.library(name: "FeatureFlagService", targets: ["FeatureFlagService"]),
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),

		// MARK: - Libraries
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "FeatureFlagLibrary", targets: ["FeatureFlagLibrary"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
		.library(name: "SharedModelsLibraryMocks", targets: ["SharedModelsLibraryMocks"]),
		.library(name: "SharedPersistenceModelsLibrary", targets: ["SharedPersistenceModelsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.0.0"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AlleysListFeature",
			dependencies: [
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
				"PersistenceServiceInterface",
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
				"GameEditorFeature",
				"PersistenceServiceInterface",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),

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
	]
)
