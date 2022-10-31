// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BaseFormFeature", targets: ["BaseFormFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "GameEditorFeature", targets: ["GameEditorFeature"]),
		.library(name: "GamesListFeature", targets: ["GamesListFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SeriesSidebarFeature", targets: ["SeriesSidebarFeature"]),

		// MARK: - Data Providers
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
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),

		// MARK: - Libraries
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "PersistenceModelsLibrary", targets: ["PersistenceModelsLibrary"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
		.library(name: "SharedModelsLibraryMocks", targets: ["SharedModelsLibraryMocks"]),
	],
	dependencies: [
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.0.0"),
	],
	targets: [
		// MARK: - Features
		.target(name: "AppFeature", dependencies: ["BowlersListFeature"]),
		.testTarget(name: "AppFeatureTests", dependencies: ["AppFeature"]),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"BowlersDataProviderInterface",
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
				"LeaguesDataProviderInterface",
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
				"SeriesDataProviderInterface",
			]
		),
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
				"GamesDataProviderInterface",
			]
		),
		.testTarget(name: "SeriesSidebarFeatureTests", dependencies: ["SeriesSidebarFeature"]),

		// MARK: - Data Providers
		.target(
			name: "BowlersDataProvider",
			dependencies: [
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "BowlersDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "BowlersDataProviderTests", dependencies: ["BowlersDataProvider"]),
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
		.testTarget(name: "FramesDataProviderTests", dependencies: ["FramesDataProvider"]),
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
		.testTarget(name: "GamesDataProviderTests", dependencies: ["GamesDataProvider"]),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				"LeaguesDataProviderInterface",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "LeaguesDataProviderInterface",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "LeaguesDataProviderTests", dependencies: ["LeaguesDataProvider"]),
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
				"SharedModelsLibrary",
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "SeriesDataProviderTests", dependencies: ["SeriesDataProvider"]),

		// MARK: - Services
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
				"PersistenceModelsLibrary",
				"PersistenceServiceInterface",
			]
		),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				.product(name: "GRDB", package: "grdb.swift"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),

		// MARK: - Libraries
		.target(name: "DateTimeLibrary", dependencies: []),
		.testTarget(name: "DateTimeLibraryTests", dependencies: ["DateTimeLibrary"]),
		.target(
			name: "PersistenceModelsLibrary",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "GRDB", package: "grdb.swift"),
			]
		),
		.target(name: "SharedModelsLibrary", dependencies: []),
		.target(
			name: "SharedModelsLibraryMocks",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
	]
)
