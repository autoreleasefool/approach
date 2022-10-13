// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		.library(name: "AppFeature", targets: ["AppFeature"]),
	],
	dependencies: [
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
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
	]
)
