// swift-tools-version: 5.7.1

import PackageDescription

let package = Package(
	name: "Approach",
	defaultLocalization: "en",
	platforms: [
		.iOS("17.0"),
	],
	products: [
		// MARK: - Features
		.library(name: "AccessoriesOverviewFeature", targets: ["AccessoriesOverviewFeature"]),
		.library(name: "AddressLookupFeature", targets: ["AddressLookupFeature"]),
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AnnouncementsFeature", targets: ["AnnouncementsFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "ArchiveListFeature", targets: ["ArchiveListFeature"]),
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
		.library(name: "ImportExportFeature", targets: ["ImportExportFeature"]),
		.library(name: "LaneEditorFeature", targets: ["LaneEditorFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "OnboardingFeature", targets: ["OnboardingFeature"]),
		.library(name: "OpponentDetailsFeature", targets: ["OpponentDetailsFeature"]),
		.library(name: "OpponentsListFeature", targets: ["OpponentsListFeature"]),
		.library(name: "PaywallFeature", targets: ["PaywallFeature"]),
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
		.library(name: "QuickLaunchRepository", targets: ["QuickLaunchRepository"]),
		.library(name: "QuickLaunchRepositoryInterface", targets: ["QuickLaunchRepositoryInterface"]),
		.library(name: "ScoresRepository", targets: ["ScoresRepository"]),
		.library(name: "ScoresRepositoryInterface", targets: ["ScoresRepositoryInterface"]),
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
		.library(name: "AnnouncementsService", targets: ["AnnouncementsService"]),
		.library(name: "AnnouncementsServiceInterface", targets: ["AnnouncementsServiceInterface"]),
		.library(name: "AppIconService", targets: ["AppIconService"]),
		.library(name: "AppIconServiceInterface", targets: ["AppIconServiceInterface"]),
		.library(name: "AppInfoService", targets: ["AppInfoService"]),
		.library(name: "AppInfoServiceInterface", targets: ["AppInfoServiceInterface"]),
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
		.library(name: "ImportExportService", targets: ["ImportExportService"]),
		.library(name: "ImportExportServiceInterface", targets: ["ImportExportServiceInterface"]),
		.library(name: "LaunchService", targets: ["LaunchService"]),
		.library(name: "LaunchServiceInterface", targets: ["LaunchServiceInterface"]),
		.library(name: "LoggingService", targets: ["LoggingService"]),
		.library(name: "LoggingServiceInterface", targets: ["LoggingServiceInterface"]),
		.library(name: "NotificationsService", targets: ["NotificationsService"]),
		.library(name: "NotificationsServiceInterface", targets: ["NotificationsServiceInterface"]),
		.library(name: "PasteboardService", targets: ["PasteboardService"]),
		.library(name: "PasteboardServiceInterface", targets: ["PasteboardServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "ProductsService", targets: ["ProductsService"]),
		.library(name: "ProductsServiceInterface", targets: ["ProductsServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),
		.library(name: "StoreReviewService", targets: ["StoreReviewService"]),
		.library(name: "StoreReviewServiceInterface", targets: ["StoreReviewServiceInterface"]),
		.library(name: "TipsService", targets: ["TipsService"]),
		.library(name: "TipsServiceInterface", targets: ["TipsServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AnnouncementsLibrary", targets: ["AnnouncementsLibrary"]),
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
		.library(name: "ProductsLibrary", targets: ["ProductsLibrary"]),
		.library(name: "ReorderingLibrary", targets: ["ReorderingLibrary"]),
		.library(name: "RepositoryLibrary", targets: ["RepositoryLibrary"]),
		.library(name: "ResourceListLibrary", targets: ["ResourceListLibrary"]),
		.library(name: "ResourcePickerLibrary", targets: ["ResourcePickerLibrary"]),
		.library(name: "ScoreKeeperLibrary", targets: ["ScoreKeeperLibrary"]),
		.library(name: "ScoreKeeperModelsLibrary", targets: ["ScoreKeeperModelsLibrary"]),
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
		.package(url: "https://github.com/apple/swift-algorithms.git", from: "1.2.0"),
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "1.0.0"),
		.package(url: "https://github.com/CocoaLumberjack/CocoaLumberjack.git", from: "3.8.4"),
		.package(url: "https://github.com/elai950/AlertToast.git", from: "1.3.9"),
		.package(url: "https://github.com/getsentry/sentry-cocoa.git", from: "8.21.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.25.0"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "1.9.2"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "1.2.2"),
		.package(url: "https://github.com/pointfreeco/swift-identified-collections.git", from: "1.0.0"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.15.4"),
		.package(url: "https://github.com/pointfreeco/xctest-dynamic-overlay.git", from: "1.1.0"),
		.package(url: "https://github.com/RevenueCat/purchases-ios.git", from: "4.37.0"),
		.package(url: "https://github.com/SFSafeSymbols/SFSafeSymbols.git", from: "5.2.0"),
		.package(url: "https://github.com/TelemetryDeck/SwiftClient.git", from: "1.5.1"),
		.package(url: "https://github.com/weichsel/ZIPFoundation.git", from: "0.9.18"),
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
				"AnalyticsServiceInterface",
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
				"FeatureFlagsServiceInterface",
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
			name: "AnnouncementsFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"AnnouncementsServiceInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
			]
		),
		.testTarget(
			name: "AnnouncementsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsFeature",
			]
		),
		.target(
			name: "AppFeature",
			dependencies: [
				"AccessoriesOverviewFeature",
				"BowlersListFeature",
				"LaunchServiceInterface",
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
			name: "ArchiveListFeature",
			dependencies: [
				"BowlersRepositoryInterface",
				"DateTimeLibrary",
				"ErrorsFeature",
				"GamesRepositoryInterface",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"SeriesRepositoryInterface",
			]
		),
		.testTarget(
			name: "ArchiveListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ArchiveListFeature",
			]
		),
		.target(
			name: "AvatarEditorFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"AvatarServiceInterface",
				"ExtensionsLibrary",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ViewsLibrary",
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
				"AnnouncementsFeature",
				"BowlerEditorFeature",
				"LeaguesListFeature",
				"QuickLaunchRepositoryInterface",
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
				"AnalyticsServiceInterface",
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
				"AnalyticsServiceInterface",
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
				"AvatarServiceInterface",
				"FeatureFlagsServiceInterface",
				"FramesRepositoryInterface",
				"GearRepositoryInterface",
				"LanesRepositoryInterface",
				"RecentlyUsedServiceInterface",
				"SharingFeature",
				"StatisticsDetailsFeature",
				"StoreReviewServiceInterface",
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
				"SeriesEditorFeature",
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
				"AvatarEditorFeature",
				"BowlersRepositoryInterface",
				"FormFeature",
				"GearRepositoryInterface",
				"PickableModelsLibrary",
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
			name: "ImportExportFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"DateTimeLibrary",
				"FeatureActionLibrary",
				"ImportExportServiceInterface",
				"LoggingServiceInterface",
				"PreferenceServiceInterface",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ImportExportFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportFeature",
			]
		),
		.target(
			name: "LaneEditorFeature",
			dependencies: [
				"AnalyticsServiceInterface",
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
				"FormFeature",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
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
				"AnalyticsServiceInterface",
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
				"ModelsViewsLibrary",
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
			name: "PaywallFeature",
			dependencies: [
				"ErrorsFeature",
				"ProductsServiceInterface",
			]
		),
		.testTarget(
			name: "PaywallFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PaywallFeature",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"DateTimeLibrary",
				"FormFeature",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SeriesRepositoryInterface",
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
				"LeagueEditorFeature",
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
				"ArchiveListFeature",
				"DatabaseMockingServiceInterface",
				"FeatureFlagsListFeature",
				"ImportExportFeature",
				"OpponentsListFeature",
				"ProductsServiceInterface",
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
				"ScoreSheetLibrary",
				"ScoresRepositoryInterface",
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
				"BowlersRepositoryInterface",
				"ErrorsFeature",
				"GamesRepositoryInterface",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"NotificationsServiceInterface",
				"PickableModelsLibrary",
				"PreferenceServiceInterface",
				"SeriesRepositoryInterface",
				"StatisticsRepositoryInterface",
				"TipsServiceInterface",
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
				"DatabaseServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
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
				"ScoreKeeperLibrary",
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
				"ExtensionsLibrary",
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
			name: "QuickLaunchRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LeaguesRepositoryInterface",
				"QuickLaunchRepositoryInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "QuickLaunchRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "QuickLaunchRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"QuickLaunchRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ScoresRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"FramesRepositoryInterface",
				"GamesRepositoryInterface",
				"RepositoryLibrary",
				"ScoresRepositoryInterface",
			]
		),
		.target(
			name: "ScoresRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoresRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"FramesRepository",
				"GamesRepository",
				"ScoresRepository",
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
				"FeatureFlagsServiceInterface",
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
				.product(name: "Sentry", package: "sentry-cocoa"),
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
			name: "AnnouncementsService",
			dependencies: [
				"AnnouncementsServiceInterface",
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "AnnouncementsServiceInterface",
			dependencies: [
				"AnnouncementsLibrary",
			]
		),
		.testTarget(
			name: "AnnouncementsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsService",
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
			name: "AppInfoService",
			dependencies: [
				"AppInfoServiceInterface",
				"ConstantsLibrary",
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "AppInfoServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "AppInfoServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AppInfoService",
			]
		),
		.target(
			name: "AvatarService",
			dependencies: [
				"AvatarServiceInterface",
				"ExtensionsLibrary",
			]
		),
		.target(
			name: "AvatarServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsViewsLibrary",
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
			name: "ImportExportService",
			dependencies: [
				"DatabaseServiceInterface",
				"DateTimeLibrary",
				"FileManagerServiceInterface",
				"ImportExportServiceInterface",
			]
		),
		.target(
			name: "ImportExportServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "ImportExportServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportService",
			]
		),
		.target(
			name: "LaunchService",
			dependencies: [
				"AnalyticsServiceInterface",
				"AppInfoServiceInterface",
				"FeatureFlagsServiceInterface",
				"LaunchServiceInterface",
				"ProductsServiceInterface",
			]
		),
		.target(
			name: "LaunchServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "LaunchServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaunchService",
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
			name: "ProductsService",
			dependencies: [
				.product(name: "RevenueCat", package: "purchases-ios"),
				"ConstantsLibrary",
				"ProductsServiceInterface",
			]
		),
		.target(
			name: "ProductsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ProductsLibrary",
			]
		),
		.testTarget(
			name: "ProductsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ProductsService",
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
			name: "StoreReviewService",
			dependencies: [
				"AppInfoServiceInterface",
				"PreferenceServiceInterface",
				"StoreReviewServiceInterface",
			]
		),
		.target(
			name: "StoreReviewServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "StoreReviewServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StoreReviewService",
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
			name: "AnnouncementsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "AnnouncementsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsLibrary",
			]
		),
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
				"ScoreKeeperLibrary",
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
				.product(name: "Dependencies", package: "swift-dependencies"),
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
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			]
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
				"ScoreKeeperModelsLibrary",
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
				"StringsLibrary",
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
			name: "ProductsLibrary",
			dependencies: []
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
				"ExtensionsLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
				"SwiftUIExtensionsLibrary",
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
				"ExtensionsLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
				"SwiftUIExtensionsLibrary",
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
			name: "ScoreKeeperLibrary",
			dependencies: [
				"ScoreKeeperModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoreKeeperLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreKeeperLibrary",
			]
		),
		.target(
			name: "ScoreKeeperModelsLibrary",
			dependencies: []
		),
		.testTarget(
			name: "ScoreKeeperModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreKeeperModelsLibrary",
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
				"DateTimeLibrary",
				"StatisticsLibrary",
				"SwiftUIExtensionsLibrary",
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
			dependencies: [
				"AssetsLibrary",
			]
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
				.product(name: "AlertToast", package: "AlertToast"),
				"AssetsLibrary",
				"ExtensionsLibrary",
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
