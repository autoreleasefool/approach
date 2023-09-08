// swift-tools-version: 5.7.1

import PackageDescription

let package = Package(
	name: "Approach",
	defaultLocalization: "en",
	platforms: [
		.iOS("16.4"),
	],
	products: [
		// MARK: - Features
		.library(name: "AccessoriesOverviewFeature", targets: ["AccessoriesOverviewFeature"]),
		.library(name: "AddressLookupFeature", targets: ["AddressLookupFeature"]),
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "AvatarEditorFeature", targets: ["AvatarEditorFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "ErrorsFeature", targets: ["ErrorsFeature"]),
		.library(name: "FeatureFlagsListFeature", targets: ["FeatureFlagsListFeature"]),
		.library(name: "FormFeature", targets: ["FormFeature"]),
		.library(name: "GamesEditorFeature", targets: ["GamesEditorFeature"]),
		.library(name: "GamesListFeature", targets: ["GamesListFeature"]),
		.library(name: "GearEditorFeature", targets: ["GearEditorFeature"]),
		.library(name: "GearListFeature", targets: ["GearListFeature"]),
		.library(name: "LaneEditorFeature", targets: ["LaneEditorFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "OnboardingFeature", targets: ["OnboardingFeature"]),
		.library(name: "OpponentDetailsFeature", targets: ["OpponentDetailsFeature"]),
		.library(name: "OpponentsListFeature", targets: ["OpponentsListFeature"]),
		.library(name: "ScoreSheetFeature", targets: ["ScoreSheetFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "SharingFeature", targets: ["SharingFeature"]),
		.library(name: "StatisticsDetailsFeature", targets: ["StatisticsDetailsFeature"]),
		.library(name: "StatisticsOverviewFeature", targets: ["StatisticsOverviewFeature"]),
		.library(name: "StatisticsWidgetsLayoutFeature", targets: ["StatisticsWidgetsLayoutFeature"]),

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
		.library(name: "LocationsRepository", targets: ["LocationsRepository"]),
		.library(name: "LocationsRepositoryInterface", targets: ["LocationsRepositoryInterface"]),
		.library(name: "MatchPlaysRepository", targets: ["MatchPlaysRepository"]),
		.library(name: "MatchPlaysRepositoryInterface", targets: ["MatchPlaysRepositoryInterface"]),
		.library(name: "SeriesRepository", targets: ["SeriesRepository"]),
		.library(name: "SeriesRepositoryInterface", targets: ["SeriesRepositoryInterface"]),
		.library(name: "StatisticsRepository", targets: ["StatisticsRepository"]),
		.library(name: "StatisticsRepositoryInterface", targets: ["StatisticsRepositoryInterface"]),
		.library(name: "StatisticsWidgetsRepository", targets: ["StatisticsWidgetsRepository"]),
		.library(name: "StatisticsWidgetsRepositoryInterface", targets: ["StatisticsWidgetsRepositoryInterface"]),

		// MARK: - Data Providers

		// MARK: - Services
		.library(name: "AddressLookupService", targets: ["AddressLookupService"]),
		.library(name: "AddressLookupServiceInterface", targets: ["AddressLookupServiceInterface"]),
		.library(name: "AnalyticsService", targets: ["AnalyticsService"]),
		.library(name: "AnalyticsServiceInterface", targets: ["AnalyticsServiceInterface"]),
		.library(name: "AppIconService", targets: ["AppIconService"]),
		.library(name: "AppIconServiceInterface", targets: ["AppIconServiceInterface"]),
		.library(name: "AvatarService", targets: ["AvatarService"]),
		.library(name: "AvatarServiceInterface", targets: ["AvatarServiceInterface"]),
		.library(name: "DatabaseMockingService", targets: ["DatabaseMockingService"]),
		.library(name: "DatabaseMockingServiceInterface", targets: ["DatabaseMockingServiceInterface"]),
		.library(name: "DatabaseService", targets: ["DatabaseService"]),
		.library(name: "DatabaseServiceInterface", targets: ["DatabaseServiceInterface"]),
		.library(name: "EmailService", targets: ["EmailService"]),
		.library(name: "EmailServiceInterface", targets: ["EmailServiceInterface"]),
		.library(name: "FeatureFlagsService", targets: ["FeatureFlagsService"]),
		.library(name: "FeatureFlagsServiceInterface", targets: ["FeatureFlagsServiceInterface"]),
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "LoggingService", targets: ["LoggingService"]),
		.library(name: "LoggingServiceInterface", targets: ["LoggingServiceInterface"]),
		.library(name: "NotificationsService", targets: ["NotificationsService"]),
		.library(name: "NotificationsServiceInterface", targets: ["NotificationsServiceInterface"]),
		.library(name: "PasteboardService", targets: ["PasteboardService"]),
		.library(name: "PasteboardServiceInterface", targets: ["PasteboardServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),
		.library(name: "ScoringService", targets: ["ScoringService"]),
		.library(name: "ScoringServiceInterface", targets: ["ScoringServiceInterface"]),
		.library(name: "TipsService", targets: ["TipsService"]),
		.library(name: "TipsServiceInterface", targets: ["TipsServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AssetsLibrary", targets: ["AssetsLibrary"]),
		.library(name: "ConstantsLibrary", targets: ["ConstantsLibrary"]),
		.library(name: "DatabaseLibrary", targets: ["DatabaseLibrary"]),
		.library(name: "DatabaseModelsLibrary", targets: ["DatabaseModelsLibrary"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "EquatableLibrary", targets: ["EquatableLibrary"]),
		.library(name: "ExtensionsLibrary", targets: ["ExtensionsLibrary"]),
		.library(name: "FeatureActionLibrary", targets: ["FeatureActionLibrary"]),
		.library(name: "FeatureFlagsLibrary", targets: ["FeatureFlagsLibrary"]),
		.library(name: "ListContentLibrary", targets: ["ListContentLibrary"]),
		.library(name: "ModelsLibrary", targets: ["ModelsLibrary"]),
		.library(name: "ModelsViewsLibrary", targets: ["ModelsViewsLibrary"]),
		.library(name: "PickableModelsLibrary", targets: ["PickableModelsLibrary"]),
		.library(name: "ReorderingLibrary", targets: ["ReorderingLibrary"]),
		.library(name: "RepositoryLibrary", targets: ["RepositoryLibrary"]),
		.library(name: "ResourceListLibrary", targets: ["ResourceListLibrary"]),
		.library(name: "ResourcePickerLibrary", targets: ["ResourcePickerLibrary"]),
		.library(name: "ScoreSheetLibrary", targets: ["ScoreSheetLibrary"]),
		.library(name: "SortOrderLibrary", targets: ["SortOrderLibrary"]),
		.library(name: "SortingLibrary", targets: ["SortingLibrary"]),
		.library(name: "StatisticsChartsLibrary", targets: ["StatisticsChartsLibrary"]),
		.library(name: "StatisticsChartsMocksLibrary", targets: ["StatisticsChartsMocksLibrary"]),
		.library(name: "StatisticsLibrary", targets: ["StatisticsLibrary"]),
		.library(name: "StatisticsModelsLibrary", targets: ["StatisticsModelsLibrary"]),
		.library(name: "StatisticsWidgetsLibrary", targets: ["StatisticsWidgetsLibrary"]),
		.library(name: "StringsLibrary", targets: ["StringsLibrary"]),
		.library(name: "SwiftUIExtensionsLibrary", targets: ["SwiftUIExtensionsLibrary"]),
		.library(name: "TestDatabaseUtilitiesLibrary", targets: ["TestDatabaseUtilitiesLibrary"]),
		.library(name: "TestUtilitiesLibrary", targets: ["TestUtilitiesLibrary"]),
		.library(name: "TipsLibrary", targets: ["TipsLibrary"]),
		.library(name: "ToastLibrary", targets: ["ToastLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-algorithms.git", from: "1.0.0"),
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "0.1.0"),
		.package(url: "https://github.com/CocoaLumberjack/CocoaLumberjack.git", from: "3.8.0"),
		.package(url: "https://github.com/exyte/PopupView.git", from: "2.6.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.16.0"),
		.package(url: "https://github.com/markiv/SwiftUI-Shimmer.git", from: "1.4.0"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "1.2.0"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "1.0.0"),
		.package(url: "https://github.com/pointfreeco/swift-identified-collections.git", from: "1.0.0"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.11.1"),
		.package(url: "https://github.com/pointfreeco/xctest-dynamic-overlay.git", from: "1.0.2"),
		.package(url: "https://github.com/SFSafeSymbols/SFSafeSymbols.git", from: "4.1.1"),
		.package(url: "https://github.com/TelemetryDeck/SwiftClient.git", from: "1.4.4"),
		.package(url: "https://github.com/weichsel/ZIPFoundation.git", from: "0.9.16"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AccessoriesOverviewFeature",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				"AlleysListFeature",
				"GearListFeature",
			]
		),
		.testTarget(
			name: "AccessoriesOverviewFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AccessoriesOverviewFeature",
			]
		),
		.target(
			name: "AddressLookupFeature",
			dependencies: [
				"AddressLookupServiceInterface",
				"FeatureActionLibrary",
				"LocationsRepositoryInterface",
				"LoggingServiceInterface",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "AddressLookupFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupFeature",
			]
		),
		.target(
			name: "AlleyEditorFeature",
			dependencies: [
				"AddressLookupFeature",
				"AlleysRepositoryInterface",
				"AnalyticsServiceInterface",
				"FeatureFlagsServiceInterface",
				"FormFeature",
				"LaneEditorFeature",
				"ModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "AlleyEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleyEditorFeature",
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				"AlleyEditorFeature",
				"ResourceListLibrary",
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysListFeature",
			]
		),
		.target(
			name: "AppFeature",
			dependencies: [
				"AccessoriesOverviewFeature",
				"BowlersListFeature",
				"OnboardingFeature",
				"SettingsFeature",
				"StatisticsOverviewFeature",
			]
		),
		.testTarget(
			name: "AppFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AppFeature",
			]
		),
		.target(
			name: "AvatarEditorFeature",
			dependencies: [
				"AvatarServiceInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "AvatarEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarEditorFeature",
			]
		),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"BowlersRepositoryInterface",
				"FormFeature",
			]
		),
		.testTarget(
			name: "BowlerEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerEditorFeature",
			]
		),
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
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersListFeature",
			]
		),
		.target(
			name: "ErrorsFeature",
			dependencies: [
				"ConstantsLibrary",
				"EmailServiceInterface",
				"EquatableLibrary",
				"FeatureActionLibrary",
				"FileManagerServiceInterface",
				"LoggingServiceInterface",
				"PasteboardServiceInterface",
				"ToastLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ErrorsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ErrorsFeature",
			]
		),
		.target(
			name: "FeatureFlagsListFeature",
			dependencies: [
				"FeatureActionLibrary",
				"FeatureFlagsServiceInterface",
				"LoggingServiceInterface",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "FeatureFlagsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsListFeature",
			]
		),
		.target(
			name: "FormFeature",
			dependencies: [
				"ErrorsFeature",
			]
		),
		.testTarget(
			name: "FormFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FormFeature",
			]
		),
		.target(
			name: "GamesEditorFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"BowlersRepositoryInterface",
				"FeatureFlagsServiceInterface",
				"FramesRepositoryInterface",
				"GearRepositoryInterface",
				"LanesRepositoryInterface",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SharingFeature",
			]
		),
		.testTarget(
			name: "GamesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesEditorFeature",
			]
		),
		.target(
			name: "GamesListFeature",
			dependencies: [
				"GamesEditorFeature",
				"ResourceListLibrary",
			]
		),
		.testTarget(
			name: "GamesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesListFeature",
			]
		),
		.target(
			name: "GearEditorFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"AvatarServiceInterface",
				"BowlersRepositoryInterface",
				"FeatureFlagsServiceInterface",
				"FormFeature",
				"GearRepositoryInterface",
				"PickableModelsLibrary",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "GearEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearEditorFeature",
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
			]
		),
		.target(
			name: "LaneEditorFeature",
			dependencies: [
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ModelsLibrary",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "LaneEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaneEditorFeature",
			]
		),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"AnalyticsServiceInterface",
				"FeatureFlagsServiceInterface",
				"FormFeature",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "LeagueEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeagueEditorFeature",
			]
		),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeagueEditorFeature",
				"RecentlyUsedServiceInterface",
				"SeriesListFeature",
				"StatisticsWidgetsLayoutFeature",
			]
		),
		.testTarget(
			name: "LeaguesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeaguesListFeature",
			]
		),
		.target(
			name: "OnboardingFeature",
			dependencies: [
				"BowlersRepositoryInterface",
				"ExtensionsLibrary",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "OnboardingFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OnboardingFeature",
			]
		),
		.target(
			name: "OpponentDetailsFeature",
			dependencies: [
				"BowlerEditorFeature",
				"GamesRepositoryInterface",
				"ResourceListLibrary",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "OpponentDetailsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentDetailsFeature",
			]
		),
		.target(
			name: "OpponentsListFeature",
			dependencies: [
				"FeatureFlagsServiceInterface",
				"OpponentDetailsFeature",
				"RecentlyUsedServiceInterface",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "OpponentsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentsListFeature",
			]
		),
		.target(
			name: "ScoreSheetFeature",
			dependencies: [
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ScoreSheetLibrary",
				"ScoringServiceInterface",
			]
		),
		.testTarget(
			name: "ScoreSheetFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreSheetFeature",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"AnalyticsServiceInterface",
				"DateTimeLibrary",
				"FeatureFlagsServiceInterface",
				"FormFeature",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SeriesRepositoryInterface",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesEditorFeature",
			]
		),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"GamesListFeature",
				"SeriesEditorFeature",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "SeriesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesListFeature",
			]
		),
		.target(
			name: "SettingsFeature",
			dependencies: [
				"AppIconServiceInterface",
				"DatabaseMockingServiceInterface",
				"FeatureFlagsListFeature",
				"OpponentsListFeature",
				"PreferenceServiceInterface",
			]
		),
		.testTarget(
			name: "SettingsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SettingsFeature",
			]
		),
		.target(
			name: "SharingFeature",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				"ErrorsFeature",
				"GamesRepositoryInterface",
				"ScoreSheetFeature",
			]
		),
		.testTarget(
			name: "SharingFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharingFeature",
			]
		),
		.target(
			name: "StatisticsDetailsFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"BowlersRepositoryInterface",
				"ErrorsFeature",
				"GamesRepositoryInterface",
				"LeaguesRepositoryInterface",
				"NotificationsServiceInterface",
				"PickableModelsLibrary",
				"PreferenceServiceInterface",
				"SeriesRepositoryInterface",
				"StatisticsRepositoryInterface",
				"SwiftUIExtensionsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsDetailsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsDetailsFeature",
			]
		),
		.target(
			name: "StatisticsOverviewFeature",
			dependencies: [
				"StatisticsDetailsFeature",
				"TipsServiceInterface",
			]
		),
		.testTarget(
			name: "StatisticsOverviewFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsOverviewFeature",
			]
		),
		.target(
			name: "StatisticsWidgetsLayoutFeature",
			dependencies: [
				"ReorderingLibrary",
				"StatisticsDetailsFeature",
				"StatisticsWidgetsRepositoryInterface",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsLayoutFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetsLayoutFeature",
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
				"LanesRepositoryInterface",
				"LocationsRepositoryInterface",
			]
		),
		.testTarget(
			name: "AlleysRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysRepository",
				"DatabaseService",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "BowlersRepository",
			dependencies: [
				"BowlersRepositoryInterface",
				"DatabaseServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
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
				"TestDatabaseUtilitiesLibrary",
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
				"ExtensionsLibrary",
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "FramesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"FramesRepository",
				"TestDatabaseUtilitiesLibrary",
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
				"MatchPlaysRepositoryInterface",
			]
		),
		.testTarget(
			name: "GamesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"GamesRepository",
				"TestDatabaseUtilitiesLibrary",
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
				"TestDatabaseUtilitiesLibrary",
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
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LeaguesRepository",
			dependencies: [
				"DatabaseServiceInterface",
				"LeaguesRepositoryInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
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
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LocationsRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LocationsRepositoryInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "LocationsRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "LocationsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"LocationsRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "MatchPlaysRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"MatchPlaysRepositoryInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "MatchPlaysRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "MatchPlaysRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"MatchPlaysRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
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
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsRepository",
			dependencies: [
				"DatabaseServiceInterface",
				"PreferenceServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
				"StatisticsRepositoryInterface",
			]
		),
		.target(
			name: "StatisticsRepositoryInterface",
			dependencies: [
				"StatisticsWidgetsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"StatisticsRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsWidgetsRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RepositoryLibrary",
				"StatisticsWidgetsRepositoryInterface",
			]
		),
		.target(
			name: "StatisticsWidgetsRepositoryInterface",
			dependencies: [
				"StatisticsRepositoryInterface",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"StatisticsRepository",
				"StatisticsWidgetsRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),

		// MARK: - Data Providers

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
				"EquatableLibrary",
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "AddressLookupServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupService",
			]
		),
		.target(
			name: "AnalyticsService",
			dependencies: [
				.product(name: "TelemetryClient", package: "SwiftClient"),
				"AnalyticsServiceInterface",
				"ConstantsLibrary",
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "AnalyticsServiceInterface",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "AnalyticsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnalyticsService",
			]
		),
		.target(
			name: "AppIconService",
			dependencies: [
				"AppIconServiceInterface",
			]
		),
		.target(
			name: "AppIconServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"AssetsLibrary",
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
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "AvatarServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarService",
			]
		),
		.target(
			name: "DatabaseMockingService",
			dependencies: [
				"DatabaseMockingServiceInterface",
			]
		),
		.target(
			name: "DatabaseMockingServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.target(
			name: "DatabaseService",
			dependencies: [
				"DatabaseLibrary",
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
			]
		),
		.target(
			name: "EmailService",
			dependencies: [
				"EmailServiceInterface",
			]
		),
		.target(
			name: "EmailServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "EmailServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"EmailService",
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
			]
		),
		.target(
			name: "FileManagerService",
			dependencies: [
				.product(name: "ZIPFoundation", package: "ZIPFoundation"),
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
			]
		),
		.target(
			name: "LoggingService",
			dependencies: [
				.product(name: "CocoaLumberjack", package: "CocoaLumberjack"),
				.product(name: "CocoaLumberjackSwift", package: "CocoaLumberjack"),
				"FileManagerServiceInterface",
				"LoggingServiceInterface",
			]
		),
		.target(
			name: "LoggingServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "LoggingServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LoggingService",
			]
		),
		.target(
			name: "NotificationsService",
			dependencies: [
				"NotificationsServiceInterface",
			]
		),
		.target(
			name: "NotificationsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "NotificationsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"NotificationsService",
			]
		),
		.target(
			name: "PasteboardService",
			dependencies: [
				"PasteboardServiceInterface",
			]
		),
		.target(
			name: "PasteboardServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "PasteboardServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PasteboardService",
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
			]
		),
		.target(
			name: "ScoringService",
			dependencies: [
				"ScoringServiceInterface",
			]
		),
		.target(
			name: "ScoringServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoringServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"FramesRepository",
				"ScoringService",
				"TestDatabaseUtilitiesLibrary",
			]
		),
		.target(
			name: "TipsService",
			dependencies: [
				"PreferenceServiceInterface",
				"TipsServiceInterface",
			]
		),
		.target(
			name: "TipsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"TipsLibrary",
			]
		),
		.testTarget(
			name: "TipsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TipsService",
			]
		),

		// MARK: - Libraries
		.target(
			name: "AssetsLibrary",
			dependencies: [
				.product(name: "SFSafeSymbols", package: "SFSafeSymbols"),
			],
			resources: [
				.process("Resources"),
			]
		),
		.target(
			name: "ConstantsLibrary",
			dependencies: [
				"StringsLibrary",
			],
			resources: [
				.process("Resources"),
			]
		),
		.target(
			name: "DatabaseLibrary",
			dependencies: [
				.product(name: "GRDB", package: "GRDB.swift"),
			]
		),
		.testTarget(
			name: "DatabaseLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseLibrary",
			]
		),
		.target(
			name: "DatabaseModelsLibrary",
			dependencies: [
				.product(name: "GRDB", package: "GRDB.swift"),
				"ModelsLibrary",
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
			]
		),
		.target(
			name: "ListContentLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ListContentLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ListContentLibrary",
			]
		),
		.target(
			name: "ModelsLibrary",
			dependencies: [
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
			]
		),
		.testTarget(
			name: "ModelsLibraryTests",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ModelsLibrary",
			]
		),
		.target(
			name: "ModelsViewsLibrary",
			dependencies: [
				"AssetsLibrary",
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "ModelsViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ModelsViewsLibrary",
			]
		),
		.target(
			name: "PickableModelsLibrary",
			dependencies: [
				"ModelsLibrary",
				"ResourcePickerLibrary",
			]
		),
		.testTarget(
			name: "PickableModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PickableModelsLibrary",
			]
		),
		.target(
			name: "ReorderingLibrary",
			dependencies: [
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
				"FeatureActionLibrary",
			]
		),
		.testTarget(
			name: "ReorderingLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ReorderingLibrary",
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
			]
		),
		.target(
			name: "ResourceListLibrary",
			dependencies: [
				"EquatableLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
			]
		),
		.testTarget(
			name: "ResourceListLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourceListLibrary",
			]
		),
		.target(
			name: "ResourcePickerLibrary",
			dependencies: [
				"FeatureActionLibrary",
				"ListContentLibrary",
			]
		),
		.testTarget(
			name: "ResourcePickerLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourcePickerLibrary",
			]
		),
		.target(
			name: "ScoreSheetLibrary",
			dependencies: [
				"DateTimeLibrary",
				"ModelsLibrary",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ScoreSheetLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreSheetLibrary",
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
				"SortingLibrary",
			]
		),
		.target(
			name: "StatisticsChartsLibrary",
			dependencies: [
				"AssetsLibrary",
				"DateTimeLibrary",
				"StatisticsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsChartsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsChartsLibrary",
			]
		),
		.target(
			name: "StatisticsChartsMocksLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"StatisticsChartsLibrary",
			]
		),
		.target(
			name: "StatisticsLibrary",
			dependencies: [
				"ModelsLibrary",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsLibraryTests",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsLibrary",
			]
		),
		.target(
			name: "StatisticsModelsLibrary",
			dependencies: [
				"DatabaseModelsLibrary",
				"StatisticsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"StatisticsModelsLibrary",
				"TestDatabaseUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsWidgetsLibrary",
			dependencies: [
				"StatisticsChartsMocksLibrary",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetsLibrary",
			]
		),
		.target(
			name: "StringsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "StringsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StringsLibrary",
			]
		),
		.target(
			name: "SwiftUIExtensionsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "SwiftUIExtensionsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SwiftUIExtensionsLibrary",
			]
		),
		.target(
			name: "TestDatabaseUtilitiesLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"DatabaseLibrary",
				"DatabaseModelsLibrary",
			]
		),
		.target(
			name: "TestUtilitiesLibrary",
			dependencies: []
		),
		.target(
			name: "TipsLibrary",
			dependencies: [
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "TipsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TipsLibrary",
			]
		),
		.target(
			name: "ToastLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "PopupView", package: "PopupView"),
				"AssetsLibrary",
			]
		),
		.testTarget(
			name: "ToastLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ToastLibrary",
			]
		),
		.target(
			name: "ViewsLibrary",
			dependencies: [
				"AssetsLibrary",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "ViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ViewsLibrary",
			]
		),
	]
)
