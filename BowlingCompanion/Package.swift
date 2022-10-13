// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		.library(name: "AppFeature", targets: ["AppFeature"]),
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
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "AppFeatureTests",
			dependencies: ["AppFeature"]
		),
		.target(
			name: "PersistenceModelsLibrary",
			dependencies: [
				"SharedModelsLibrary",
				.product(name: "RealmSwift", package: "realm-swift"),
			]
		),
		.testTarget(name: "PersistenceModelsLibraryTests", dependencies: ["PersistenceModelsLibrary"]),
		.target(name: "PersistenceService", dependencies: ["PersistenceServiceInterface"]),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "RealmSwift", package: "realm-swift"),
			]
		),
		.testTarget(name: "PersistenceServiceTests", dependencies: ["PersistenceService"]),
		.target(name: "SharedModelsLibrary", dependencies: []),
	]
)
