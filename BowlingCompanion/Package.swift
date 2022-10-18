// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BowlersDataProvider", targets: ["BowlersDataProvider"]),
		.library(name: "BowlersDataProviderInterface", targets: ["BowlersDataProviderInterface"]),
		.library(name: "BowlerFormFeature", targets: ["BowlerFormFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "ExtensionsLibrary", targets: ["ExtensionsLibrary"]),
		.library(name: "GamesDataProvider", targets: ["GamesDataProvider"]),
		.library(name: "GamesDataProviderInterface", targets: ["GamesDataProviderInterface"]),
		.library(name: "GamesListFeature", targets: ["GamesListFeature"]),
		.library(name: "LeaguesDataProvider", targets: ["LeaguesDataProvider"]),
		.library(name: "LeaguesDataProviderInterface", targets: ["LeaguesDataProviderInterface"]),
		.library(name: "LeagueFormFeature", targets: ["LeagueFormFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "PersistenceModelsLibrary", targets: ["PersistenceModelsLibrary"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),
		.library(name: "SeriesDataProvider", targets: ["SeriesDataProvider"]),
		.library(name: "SeriesDataProviderInterface", targets: ["SeriesDataProviderInterface"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
	],
	targets: [
		.target(
			name: "AppFeature",
			dependencies: ["BowlersListFeature"]
		),
		.testTarget(name: "AppFeatureTests", dependencies: ["AppFeature"]),
		.target(
			name: "BowlersDataProvider",
			dependencies: [
				"BowlersDataProviderInterface",
				"ExtensionsLibrary",
				"PersistenceModelsLibrary",
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
			name: "BowlerFormFeature",
			dependencies: [
				"BowlersDataProviderInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "BowlerFormFeatureTests", dependencies: ["BowlerFormFeature"]),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"BowlerFormFeature",
				"LeaguesListFeature",
			]
		),
		.testTarget(name: "BowlersListFeatureTests", dependencies: ["BowlersListFeature"]),
		.target(
			name: "PersistenceModelsLibrary",
			dependencies: [
				"SharedModelsLibrary",
			]
		),
		.target(name: "DateTimeLibrary", dependencies: []),
		.testTarget(name: "DateTimeLibraryTests", dependencies: ["DateTimeLibrary"]),
		.target(name: "ExtensionsLibrary", dependencies: []),
		.testTarget(name: "ExtensionsLibraryTests", dependencies: ["ExtensionsLibrary"]),
		.target(
			name: "GamesDataProvider",
			dependencies: [
				"GamesDataProviderInterface",
				"ExtensionsLibrary",
				"PersistenceModelsLibrary",
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
			name: "GamesListFeature",
			dependencies: [
				"DateTimeLibrary",
				"GamesDataProviderInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "GamesListFeatureTests", dependencies: ["GamesListFeature"]),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				"LeaguesDataProviderInterface",
				"ExtensionsLibrary",
				"PersistenceModelsLibrary",
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
			name: "LeagueFormFeature",
			dependencies: [
				"LeaguesDataProviderInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "LeagueFormFeatureTests", dependencies: ["LeagueFormFeature"]),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeagueFormFeature",
				"SeriesListFeature",
			]
		),
		.testTarget(name: "LeaguesListFeatureTests", dependencies: ["LeaguesListFeature"]),
		.testTarget(name: "PersistenceModelsLibraryTests", dependencies: ["PersistenceModelsLibrary"]),
		.target(name: "PersistenceService", dependencies: ["PersistenceServiceInterface"]),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),
		.target(
			name: "SeriesDataProvider",
			dependencies: [
				"ExtensionsLibrary",
				"PersistenceModelsLibrary",
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
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"GamesListFeature",
				"SeriesDataProviderInterface",
			]
		),
		.testTarget(name: "SeriesListFeatureTests", dependencies: ["SeriesListFeature"]),
		.target(name: "SharedModelsLibrary", dependencies: []),
	]
)
