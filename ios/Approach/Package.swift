// swift-tools-version: 5.7.1
// swiftlint:disable file_length line_length

import PackageDescription

let package = Package(
	name: "Approach",
	defaultLocalization: "en",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "AvatarEditorFeature", targets: ["AvatarEditorFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "FeatureFlagsListFeature", targets: ["FeatureFlagsListFeature"]),
		.library(name: "GamesEditorFeature", targets: ["GamesEditorFeature"]),
		.library(name: "GamesListFeature", targets: ["GamesListFeature"]),
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
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "StatisticsWidgetsFeature", targets: ["StatisticsWidgetsFeature"]),

		// MARK: - Repositories
		.library(name: "AlleysRepository", targets: ["AlleysRepository"]),
		.library(name: "AlleysRepositoryInterface", targets: ["AlleysRepositoryInterface"]),
		.library(name: "BowlersRepository", targets: ["BowlersRepository"]),
		.library(name: "BowlersRepositoryInterface", targets: ["BowlersRepositoryInterface"]),
		.library(name: "FramesRepository", targets: ["FramesRepository"]),
		.library(name: "FramesRepositoryInterface", targets: ["FramesRepositoryInterface"]),
		.library(name: "GamesRepository", targets: ["GamesRepository"]),
		.library(name: "GamesRepositoryInterface", targets: ["GamesRepositoryInterface"]),
		.library(name: "GearRepository", targets: ["GearRepository"]),
		.library(name: "GearRepositoryInterface", targets: ["GearRepositoryInterface"]),
		.library(name: "LanesRepository", targets: ["LanesRepository"]),
		.library(name: "LanesRepositoryInterface", targets: ["LanesRepositoryInterface"]),
		.library(name: "LeaguesRepository", targets: ["LeaguesRepository"]),
		.library(name: "LeaguesRepositoryInterface", targets: ["LeaguesRepositoryInterface"]),
		.library(name: "SeriesRepository", targets: ["SeriesRepository"]),
		.library(name: "SeriesRepositoryInterface", targets: ["SeriesRepositoryInterface"]),

		// MARK: - Data Providers
		.library(name: "AlleysDataProvider", targets: ["AlleysDataProvider"]),
		.library(name: "AlleysDataProviderInterface", targets: ["AlleysDataProviderInterface"]),
		.library(name: "FramesDataProvider", targets: ["FramesDataProvider"]),
		.library(name: "FramesDataProviderInterface", targets: ["FramesDataProviderInterface"]),
		.library(name: "GamesDataProvider", targets: ["GamesDataProvider"]),
		.library(name: "GamesDataProviderInterface", targets: ["GamesDataProviderInterface"]),
		.library(name: "LanesDataProvider", targets: ["LanesDataProvider"]),
		.library(name: "LanesDataProviderInterface", targets: ["LanesDataProviderInterface"]),
		.library(name: "SeriesDataProvider", targets: ["SeriesDataProvider"]),
		.library(name: "SeriesDataProviderInterface", targets: ["SeriesDataProviderInterface"]),

		// MARK: - Services
		.library(name: "AddressLookupService", targets: ["AddressLookupService"]),
		.library(name: "AddressLookupServiceInterface", targets: ["AddressLookupServiceInterface"]),
		.library(name: "AnalyticsService", targets: ["AnalyticsService"]),
		.library(name: "AnalyticsServiceInterface", targets: ["AnalyticsServiceInterface"]),
		.library(name: "AvatarService", targets: ["AvatarService"]),
		.library(name: "AvatarServiceInterface", targets: ["AvatarServiceInterface"]),
		.library(name: "DatabaseService", targets: ["DatabaseService"]),
		.library(name: "DatabaseServiceInterface", targets: ["DatabaseServiceInterface"]),
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
		.library(name: "ScoringService", targets: ["ScoringService"]),
		.library(name: "ScoringServiceInterface", targets: ["ScoringServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AssetsLibrary", targets: ["AssetsLibrary"]),
		.library(name: "BaseFormLibrary", targets: ["BaseFormLibrary"]),
		.library(name: "ConstantsLibrary", targets: ["ConstantsLibrary"]),
		.library(name: "DatabaseModelsLibrary", targets: ["DatabaseModelsLibrary"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "EquatableLibrary", targets: ["EquatableLibrary"]),
		.library(name: "ExtensionsLibrary", targets: ["ExtensionsLibrary"]),
		.library(name: "FeatureActionLibrary", targets: ["FeatureActionLibrary"]),
		.library(name: "FeatureFlagsLibrary", targets: ["FeatureFlagsLibrary"]),
		.library(name: "FormLibrary", targets: ["FormLibrary"]),
		.library(name: "FoundationExtensionsLibrary", targets: ["FoundationExtensionsLibrary"]),
		.library(name: "ModelsLibrary", targets: ["ModelsLibrary"]),
		.library(name: "ModelsViewsLibrary", targets: ["ModelsViewsLibrary"]),
		.library(name: "RepositoryLibrary", targets: ["RepositoryLibrary"]),
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
		.library(name: "TestUtilitiesLibrary", targets: ["TestUtilitiesLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "0.0.4"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.6.0"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", revision: "fc17996ded63b136741ab2e0c0e0d549a8486adc"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "0.1.2"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.10.0"),
		.package(url: "https://github.com/pointfreeco/xctest-dynamic-overlay.git", from: "0.8.4"),
		.package(url: "https://github.com/TelemetryDeck/SwiftClient.git", from: "1.4.2"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AlleyEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"BaseFormLibrary",
				"ExtensionsLibrary",
				"LaneEditorFeature",
				"LanesDataProviderInterface",
				"PersistenceServiceInterface",
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
				"FeatureFlagsServiceInterface",
				"ModelsViewsLibrary",
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
			name: "AvatarEditorFeature",
			dependencies: [
				"AvatarServiceInterface",
				"FeatureActionLibrary",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "AvatarEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BowlersRepositoryInterface",
				"ExtensionsLibrary",
				"FormLibrary",
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
				"BowlerEditorFeature",
				"LeaguesListFeature",
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
				"FeatureActionLibrary",
				"FeatureFlagsServiceInterface",
				"StringsLibrary",
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
			name: "GamesEditorFeature",
			dependencies: [
				"ExtensionsLibrary",
				"FramesDataProviderInterface",
				"ScoreSheetFeature",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "GamesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GamesListFeature",
			dependencies: [
				"DateTimeLibrary",
				"GamesDataProviderInterface",
				"GamesEditorFeature",
				"ResourceListLibrary",
			]
		),
		.testTarget(
			name: "GamesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearEditorFeature",
			dependencies: [
				"AvatarServiceInterface",
				"BowlersRepositoryInterface",
				"EquatableLibrary",
				"ExtensionsLibrary",
				"FeatureFlagsServiceInterface",
				"FormLibrary",
				"GearRepositoryInterface",
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
				"GearEditorFeature",
				"ModelsViewsLibrary",
				"RecentlyUsedServiceInterface",
				"ResourceListLibrary",
				"SortOrderLibrary",
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
				"SharedModelsLibrary",
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
				"AlleysRepositoryInterface",
				"BaseFormLibrary",
				"EquatableLibrary",
				"ExtensionsLibrary",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"ResourcePickerLibrary",
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
				"RecentlyUsedServiceInterface",
				"SeriesListFeature",
				"SortOrderLibrary",
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
				"ExtensionsLibrary",
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
				"BowlerEditorFeature",
				"RecentlyUsedServiceInterface",
				"ResourceListLibrary",
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
				"FeatureActionLibrary",
				"SharedModelsLibrary",
				"ViewsLibrary",
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
				"ExtensionsLibrary",
				"LanesDataProviderInterface",
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
				"FeatureFlagsServiceInterface",
				"GamesListFeature",
				"SeriesDataProviderInterface",
				"SeriesEditorFeature",
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
				"AssetsLibrary",
				"FeatureActionLibrary",
				"StringsLibrary",
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

		// MARK: - Repositories
		.target(
			name: "AlleysRepository",
			dependencies: [
				"AlleysRepositoryInterface",
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "AlleysRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "AlleysRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysRepository",
				"DatabaseService",
			]
		),
		.target(
			name: "BowlersRepository",
			dependencies: [
				"BowlersRepositoryInterface",
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "BowlersRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "BowlersRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersRepository",
				"DatabaseService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "FramesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"FramesRepositoryInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "FramesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "FramesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"FramesRepository",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "GamesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"GamesRepositoryInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "GamesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "GamesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"GamesRepository",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "GearRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"GearRepositoryInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "GearRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "GearRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"GearRepository",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LanesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LanesRepositoryInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "LanesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "LanesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"LanesRepository",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LeaguesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LeaguesRepositoryInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "LeaguesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "LeaguesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"LeaguesRepository",
			]
		),
		.target(
			name: "SeriesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RepositoryLibrary",
				"SeriesRepositoryInterface",
			]
		),
		.target(
			name: "SeriesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "SeriesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"SeriesRepository",
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
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysDataProvider",
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
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesDataProvider",
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
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LanesDataProvider",
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
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesDataProvider",
				"SharedModelsMocksLibrary",
			]
		),

		// MARK: - Services
		.target(
			name: "AddressLookupService",
			dependencies: [
				"AddressLookupServiceInterface",
			]
		),
		.target(
			name: "AddressLookupServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "XCTestDynamicOverlay", package: "xctest-dynamic-overlay"),
			]
		),
		.testTarget(
			name: "AddressLookupServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupService",
				"SharedModelsMocksLibrary",
			]
		),
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
			name: "AvatarService",
			dependencies: [
				"AvatarServiceInterface",
			]
		),
		.target(
			name: "AvatarServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"AssetsLibrary",
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "AvatarServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "DatabaseService",
			dependencies: [
				"DatabaseServiceInterface",
				"FileManagerServiceInterface",
			]
		),
		.target(
			name: "DatabaseServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
			]
		),
		.testTarget(
			name: "DatabaseServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FeatureFlagsService",
			dependencies: [
				"FeatureFlagsServiceInterface",
				"PreferenceServiceInterface",
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
		.target(
			name: "ScoringService",
			dependencies: [
				"PersistenceServiceInterface",
				"ScoringServiceInterface",
			]
		),
		.target(
			name: "ScoringServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoringServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoringService",
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
				.product(name: "Dependencies", package: "swift-dependencies"),
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "BaseFormLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BaseFormLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "ConstantsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "DatabaseModelsLibrary",
			dependencies: [
				.product(name: "GRDB", package: "GRDB.swift"),
				"ExtensionsLibrary",
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "DatabaseModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseModelsLibrary",
				"DatabaseService",
				"SharedModelsMocksLibrary",
				"TestUtilitiesLibrary",
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
			name: "ExtensionsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "ExtensionsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ExtensionsLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FeatureActionLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
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
			name: "FormLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "FormLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FormLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "FoundationExtensionsLibrary",
			dependencies: []
		),
		.target(
			name: "ModelsLibrary",
			dependencies: []
		),
		.target(
			name: "ModelsViewsLibrary",
			dependencies: [
				"ModelsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ModelsViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ModelsViewsLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "RepositoryLibrary",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"SortingLibrary",
			]
		),
		.testTarget(
			name: "RepositoryLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"RepositoryLibrary",
				"SharedModelsMocksLibrary",
			]
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
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ResourcePickerLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourcePickerLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SharedModelsFetchableLibrary",
			dependencies: [
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "SharedModelsFetchableLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsFetchableLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SharedModelsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "SharedModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsLibrary",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SharedModelsMocksLibrary",
			dependencies: [
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "SharedModelsMocksLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SharedModelsPersistableLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
				"ExtensionsLibrary",
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "SharedModelsPersistableLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharedModelsMocksLibrary",
				"SharedModelsPersistableLibrary",
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
			name: "TestUtilitiesLibrary",
			dependencies: []
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
