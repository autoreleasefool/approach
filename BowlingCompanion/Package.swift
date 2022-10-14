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
		.library(name: "LeaguesDataProvider", targets: ["LeaguesDataProvider"]),
		.library(name: "LeaguesDataProviderInterface", targets: ["LeaguesDataProviderInterface"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "PersistenceModelsLibrary", targets: ["PersistenceModelsLibrary"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
		.package(url: "https://github.com/realm/realm-swift.git", from: "10.32.0"),
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
				"PersistenceModelsLibrary",
				"PersistenceServiceInterface",
			]
		),
		.target(name: "BowlersDataProviderInterface", dependencies: ["SharedModelsLibrary"]),
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
				"BowlersDataProviderInterface",
				"BowlerFormFeature",
				"LeaguesListFeature",
			]
		),
		.testTarget(name: "BowlersListFeatureTests", dependencies: ["BowlersListFeature"]),
		.target(
			name: "PersistenceModelsLibrary",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "RealmSwift", package: "realm-swift"),
			]
		),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				"LeaguesDataProviderInterface",
				"PersistenceModelsLibrary",
				"PersistenceServiceInterface",
			]
		),
		.target(name: "LeaguesDataProviderInterface", dependencies: ["SharedModelsLibrary"]),
		.testTarget(name: "LeaguesDataProviderTests", dependencies: ["LeaguesDataProvider"]),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeaguesDataProviderInterface",
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(name: "LeaguesListFeatureTests", dependencies: ["LeaguesListFeature"]),
		.testTarget(name: "PersistenceModelsLibraryTests", dependencies: ["PersistenceModelsLibrary"]),
		.target(name: "PersistenceService", dependencies: ["PersistenceServiceInterface"]),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				.product(name: "RealmSwift", package: "realm-swift"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),
		.target(name: "SharedModelsLibrary", dependencies: []),
	]
)
