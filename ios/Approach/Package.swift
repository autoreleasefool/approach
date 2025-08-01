// swift-tools-version: 6.1

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
		.library(name: "AchievementsFeature", targets: ["AchievementsFeature"]),
		.library(name: "AddressLookupFeature", targets: ["AddressLookupFeature"]),
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AnnouncementsFeature", targets: ["AnnouncementsFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "ArchiveListFeature", targets: ["ArchiveListFeature"]),
		.library(name: "AutomaticBackupsFeature", targets: ["AutomaticBackupsFeature"]),
		.library(name: "AvatarEditorFeature", targets: ["AvatarEditorFeature"]),
		.library(name: "BowlerDetailsFeature", targets: ["BowlerDetailsFeature"]),
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
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "SharingFeature", targets: ["SharingFeature"]),
		.library(name: "StatisticsDetailsFeature", targets: ["StatisticsDetailsFeature"]),
		.library(name: "StatisticsOverviewFeature", targets: ["StatisticsOverviewFeature"]),
		.library(name: "StatisticsWidgetEditorFeature", targets: ["StatisticsWidgetEditorFeature"]),
		.library(name: "StatisticsWidgetsLayoutFeature", targets: ["StatisticsWidgetsLayoutFeature"]),

		// MARK: - Repositories
		.library(name: "AchievementsRepository", targets: ["AchievementsRepository"]),
		.library(name: "AchievementsRepositoryInterface", targets: ["AchievementsRepositoryInterface"]),
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
		.library(name: "AchievementsService", targets: ["AchievementsService"]),
		.library(name: "AchievementsServiceInterface", targets: ["AchievementsServiceInterface"]),
		.library(name: "AddressLookupService", targets: ["AddressLookupService"]),
		.library(name: "AddressLookupServiceInterface", targets: ["AddressLookupServiceInterface"]),
		.library(name: "AnalyticsService", targets: ["AnalyticsService"]),
		.library(name: "AnalyticsServiceInterface", targets: ["AnalyticsServiceInterface"]),
		.library(name: "AppIconService", targets: ["AppIconService"]),
		.library(name: "AppIconServiceInterface", targets: ["AppIconServiceInterface"]),
		.library(name: "AvatarService", targets: ["AvatarService"]),
		.library(name: "AvatarServiceInterface", targets: ["AvatarServiceInterface"]),
		.library(name: "CodableService", targets: ["CodableService"]),
		.library(name: "CodableServiceInterface", targets: ["CodableServiceInterface"]),
		.library(name: "DatabaseMockingService", targets: ["DatabaseMockingService"]),
		.library(name: "DatabaseMockingServiceInterface", targets: ["DatabaseMockingServiceInterface"]),
		.library(name: "DatabaseService", targets: ["DatabaseService"]),
		.library(name: "DatabaseServiceInterface", targets: ["DatabaseServiceInterface"]),
		.library(name: "EmailService", targets: ["EmailService"]),
		.library(name: "EmailServiceInterface", targets: ["EmailServiceInterface"]),
		.library(name: "HUDService", targets: ["HUDService"]),
		.library(name: "HUDServiceInterface", targets: ["HUDServiceInterface"]),
		.library(name: "ImportExportService", targets: ["ImportExportService"]),
		.library(name: "ImportExportServiceInterface", targets: ["ImportExportServiceInterface"]),
		.library(name: "LaunchService", targets: ["LaunchService"]),
		.library(name: "LaunchServiceInterface", targets: ["LaunchServiceInterface"]),
		.library(name: "LoggingService", targets: ["LoggingService"]),
		.library(name: "LoggingServiceInterface", targets: ["LoggingServiceInterface"]),
		.library(name: "NotificationsService", targets: ["NotificationsService"]),
		.library(name: "NotificationsServiceInterface", targets: ["NotificationsServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),
		.library(name: "TipsService", targets: ["TipsService"]),
		.library(name: "TipsServiceInterface", targets: ["TipsServiceInterface"]),
		.library(name: "ZIPService", targets: ["ZIPService"]),
		.library(name: "ZIPServiceInterface", targets: ["ZIPServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AchievementsLibrary", targets: ["AchievementsLibrary"]),
		.library(name: "AssetsLibrary", targets: ["AssetsLibrary"]),
		.library(name: "ComposableExtensionsLibrary", targets: ["ComposableExtensionsLibrary"]),
		.library(name: "ConstantsLibrary", targets: ["ConstantsLibrary"]),
		.library(name: "DatabaseMigrationsLibrary", targets: ["DatabaseMigrationsLibrary"]),
		.library(name: "DatabaseModelsLibrary", targets: ["DatabaseModelsLibrary"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
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
		.library(name: "TestDatabaseUtilitiesLibrary", targets: ["TestDatabaseUtilitiesLibrary"]),
		.library(name: "TestUtilitiesLibrary", targets: ["TestUtilitiesLibrary"]),
		.library(name: "TipsLibrary", targets: ["TipsLibrary"]),
		.library(name: "ToastLibrary", targets: ["ToastLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-algorithms.git", from: "1.2.1"),
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "1.0.3"),
		.package(url: "https://github.com/apple/swift-collections.git", from: "1.1.4"),
		.package(url: "https://github.com/autoreleasefool/swift-utilities.git", from: "3.1.0"),
		.package(url: "https://github.com/getsentry/sentry-cocoa.git", from: "8.50.1"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "7.5.0"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "1.19.1"),
		.package(url: "https://github.com/pointfreeco/swift-concurrency-extras.git", from: "1.3.1"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "1.9.2"),
		.package(url: "https://github.com/pointfreeco/swift-identified-collections.git", from: "1.1.1"),
		.package(url: "https://github.com/pointfreeco/swift-sharing.git", from: "2.5.2"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.18.3"),
		.package(url: "https://github.com/pointfreeco/xctest-dynamic-overlay.git", from: "1.5.2"),
		.package(url: "https://github.com/quanshousio/ToastUI.git", from: "4.0.0"),
		.package(url: "https://github.com/weichsel/ZIPFoundation.git", from: "0.9.19"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AccessoriesOverviewFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AchievementsFeature",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				"AchievementsRepositoryInterface",
				"AchievementsServiceInterface",
				"ErrorsFeature",
				"FeatureFlagsLibrary",
			]
		),
		.testTarget(
			name: "AchievementsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AchievementsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AddressLookupFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"AddressLookupServiceInterface",
				"AnalyticsServiceInterface",
				"FeatureActionLibrary",
				"LocationsRepositoryInterface",
				"LoggingServiceInterface",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "AddressLookupFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleyEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				.product(name: "Sharing", package: "swift-sharing"),
				"AlleyEditorFeature",
				"FeatureFlagsLibrary",
				"ResourceListLibrary",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AnnouncementsFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"AchievementsFeature",
			]
		),
		.testTarget(
			name: "AnnouncementsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AppFeature",
			dependencies: [
				.product(name: "AppInfoPackageService", package: "swift-utilities"),
				.product(name: "BundlePackageService", package: "swift-utilities"),
				.product(name: "FeatureFlagsPackageService", package: "swift-utilities"),
				.product(name: "FileManagerPackageService", package: "swift-utilities"),
				.product(name: "GRDBDatabasePackageService", package: "swift-utilities"),
				.product(name: "PasteboardPackageService", package: "swift-utilities"),
				.product(name: "SentryErrorReportingPackageService", package: "swift-utilities"),
				.product(name: "StoreReviewPackageService", package: "swift-utilities"),
				.product(name: "TelemetryDeckAnalyticsPackageService", package: "swift-utilities"),
				.product(name: "UserDefaultsPackageService", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AppFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ArchiveListFeature",
			dependencies: [
				"BowlersRepositoryInterface",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ArchiveListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AutomaticBackupsFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ErrorsFeature",
				"FeatureFlagsLibrary",
				"ImportExportServiceInterface",
				"PreferenceServiceInterface",
			]
		),
		.testTarget(
			name: "AutomaticBackupsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AutomaticBackupsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AvatarEditorFeature",
			dependencies: [
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"AvatarServiceInterface",
				"ComposableExtensionsLibrary",
				"FeatureActionLibrary",
				"FeatureFlagsLibrary",
				"LoggingServiceInterface",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "AvatarEditorFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "BowlerDetailsFeature",
			dependencies: [
				"BowlerEditorFeature",
				"LeaguesListFeature",
			]
		),
		.testTarget(
			name: "BowlerDetailsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerDetailsFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"AnnouncementsFeature",
				"BowlerDetailsFeature",
				"QuickLaunchRepositoryInterface",
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ErrorsFeature",
			dependencies: [
				.product(name: "AppInfoPackageServiceInterface", package: "swift-utilities"),
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				.product(name: "PasteboardPackageServiceInterface", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"ConstantsLibrary",
				"EmailServiceInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ToastLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ErrorsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ErrorsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "FeatureFlagsListFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"FeatureActionLibrary",
				"FeatureFlagsLibrary",
				"LoggingServiceInterface",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "FeatureFlagsListFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsListFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FormFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "GamesEditorFeature",
			dependencies: [
				.product(name: "StoreReviewPackageServiceInterface", package: "swift-utilities"),
				"AvatarServiceInterface",
				"FeatureFlagsLibrary",
				"FramesRepositoryInterface",
				"GearRepositoryInterface",
				"LanesRepositoryInterface",
				"StatisticsDetailsFeature",
			]
		),
		.testTarget(
			name: "GamesEditorFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesEditorFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesListFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "GearListFeature",
			dependencies: [
				.product(name: "Sharing", package: "swift-sharing"),
				"GearEditorFeature",
				"RecentlyUsedServiceInterface",
				"ResourceListLibrary",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "GearListFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ImportExportFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ErrorsFeature",
				"HUDServiceInterface",
				"ImportExportServiceInterface",
				"PreferenceServiceInterface",
			]
		),
		.testTarget(
			name: "ImportExportFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LaneEditorFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ModelsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "LaneEditorFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaneEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				.product(name: "Sharing", package: "swift-sharing"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeagueEditorFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeaguesListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "OnboardingFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"BowlersRepositoryInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "OnboardingFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OnboardingFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentDetailsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "OpponentsListFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				.product(name: "Sharing", package: "swift-sharing"),
				"FeatureFlagsLibrary",
				"ModelsViewsLibrary",
				"OpponentDetailsFeature",
				"RecentlyUsedServiceInterface",
				"SortOrderLibrary",
			]
		),
		.testTarget(
			name: "OpponentsListFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentsListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"FeatureFlagsLibrary",
				"FormFeature",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SeriesRepositoryInterface",
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesEditorFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesListFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "SettingsFeature",
			dependencies: [
				.product(name: "BundlePackageServiceInterface", package: "swift-utilities"),
				"AchievementsFeature",
				"AppIconServiceInterface",
				"ArchiveListFeature",
				"AutomaticBackupsFeature",
				"DatabaseMockingServiceInterface",
				"FeatureFlagsListFeature",
				"ImportExportFeature",
				"OpponentsListFeature",
			]
		),
		.testTarget(
			name: "SettingsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SettingsFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "SharingFeature",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				"GamesRepositoryInterface",
				"ScoreSheetLibrary",
				"ScoresRepositoryInterface",
				"SeriesRepositoryInterface",
				"StatisticsWidgetEditorFeature",
			]
		),
		.testTarget(
			name: "SharingFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharingFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsDetailsFeature",
			dependencies: [
				"NotificationsServiceInterface",
				"PreferenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SharingFeature",
				"TipsServiceInterface",
			]
		),
		.testTarget(
			name: "StatisticsDetailsFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsDetailsFeature",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsOverviewFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsWidgetEditorFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"BowlersRepositoryInterface",
				"ErrorsFeature",
				"LeaguesRepositoryInterface",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"StatisticsWidgetsRepositoryInterface",
			]
		),
		.testTarget(
			name: "StatisticsWidgetEditorFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetEditorFeature",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsWidgetsLayoutFeature",
			dependencies: [
				"ReorderingLibrary",
				"StatisticsDetailsFeature",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsLayoutFeatureTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetsLayoutFeature",
				"TestUtilitiesLibrary",
			]
		),

		// MARK: - Repositories
		.target(
			name: "AchievementsRepository",
			dependencies: [
				"AchievementsRepositoryInterface",
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"FeatureFlagsLibrary",
				"RepositoryLibrary",
			]
		),
		.target(
			name: "AchievementsRepositoryInterface",
			dependencies: [
				.product(name: "Sharing", package: "swift-sharing"),
				"AchievementsLibrary",
				"ModelsLibrary",
				"PreferenceServiceInterface",
			]
		),
		.testTarget(
			name: "AchievementsRepositoryTests",
			dependencies: [
				.product(name: "ConcurrencyExtras", package: "swift-concurrency-extras"),
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"AchievementsRepository",
				"DatabaseService",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"ModelsLibrary",
				"ScoreKeeperLibrary",
			]
		),
		.testTarget(
			name: "FramesRepositoryTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"FramesRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "GamesRepository",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"SeriesRepository",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsRepository",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"CodableServiceInterface",
				"DatabaseServiceInterface",
				"FeatureFlagsLibrary",
				"PreferenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
				"StatisticsRepositoryInterface",
			]
		),
		.target(
			name: "StatisticsRepositoryInterface",
			dependencies: [
				.product(name: "Collections", package: "swift-collections"),
				"StatisticsWidgetsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsRepositoryTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
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
			name: "AchievementsService",
			dependencies: [
				.product(name: "ErrorReportingClientPackageLibrary", package: "swift-utilities"),
				"AchievementsServiceInterface",
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"FeatureFlagsLibrary",
			]
		),
		.target(
			name: "AchievementsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
				"AchievementsLibrary",
			]
		),
		.testTarget(
			name: "AchievementsServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AchievementsService",
				"TestDatabaseUtilitiesLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AddressLookupService",
			dependencies: [
				"AddressLookupServiceInterface",
			]
		),
		.target(
			name: "AddressLookupServiceInterface",
			dependencies: [
				.product(name: "ConcurrencyExtras", package: "swift-concurrency-extras"),
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "XCTestDynamicOverlay", package: "xctest-dynamic-overlay"),
				"ModelsLibrary",
			]
		),
		.testTarget(
			name: "AddressLookupServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AnalyticsService",
			dependencies: [
				.product(name: "Sentry", package: "sentry-cocoa"),
				"AnalyticsServiceInterface",
			]
		),
		.target(
			name: "AnalyticsServiceInterface",
			dependencies: [
				.product(name: "AnalyticsPackageServiceInterface", package: "swift-utilities"),
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
				.product(name: "ErrorReportingClientPackageLibrary", package: "swift-utilities"),
			]
		),
		.testTarget(
			name: "AnalyticsServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnalyticsService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AppIconService",
			dependencies: [
				"AchievementsServiceInterface",
				"AppIconServiceInterface",
				"FeatureFlagsLibrary",
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
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"AvatarServiceInterface",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "CodableService",
			dependencies: [
				"CodableServiceInterface",
			]
		),
		.target(
			name: "CodableServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "CodableServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"CodableService",
				"TestUtilitiesLibrary",
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
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				"DatabaseMigrationsLibrary",
				"DatabaseServiceInterface",
			]
		),
		.target(
			name: "DatabaseServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
				.product(name: "GRDBDatabasePackageServiceInterface", package: "swift-utilities"),
			]
		),
		.testTarget(
			name: "DatabaseServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"EmailService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "HUDService",
			dependencies: [
				"HUDServiceInterface",
			]
		),
		.target(
			name: "HUDServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "HUDServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"HUDService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ImportExportService",
			dependencies: [
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				"DatabaseMigrationsLibrary",
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"DateTimeLibrary",
				"FeatureFlagsLibrary",
				"ImportExportServiceInterface",
				"PreferenceServiceInterface",
				"StatisticsLibrary",
				"ZIPServiceInterface",
			]
		),
		.target(
			name: "ImportExportServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "ImportExportServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportService",
				"TestUtilitiesLibrary",
			],
			resources: [
				.process("Resources"),
			]
		),
		.target(
			name: "LaunchService",
			dependencies: [
				.product(name: "AppInfoPackageServiceInterface", package: "swift-utilities"),
				.product(name: "StoreReviewPackageServiceInterface", package: "swift-utilities"),
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"FeatureFlagsLibrary",
				"LaunchServiceInterface",
				"PreferenceServiceInterface",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaunchService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "LoggingService",
			dependencies: [
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LoggingService",
				"TestUtilitiesLibrary",
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"NotificationsService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "PreferenceService",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "PreferenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "PreferenceServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PreferenceService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "RecentlyUsedService",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"CodableServiceInterface",
				"RecentlyUsedServiceInterface",
			]
		),
		.target(
			name: "RecentlyUsedServiceInterface",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "Dependencies", package: "swift-dependencies"),
				"StatisticsLibrary",
			]
		),
		.testTarget(
			name: "RecentlyUsedServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"RecentlyUsedService",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "TipsService",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
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
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TestUtilitiesLibrary",
				"TipsService",
			]
		),
		.target(
			name: "ZIPService",
			dependencies: [
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				.product(name: "ZIPFoundation", package: "ZIPFoundation"),
				"ZIPServiceInterface",
			]
		),
		.target(
			name: "ZIPServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			]
		),
		.testTarget(
			name: "ZIPServiceTests",
			dependencies: [
				.product(name: "DependenciesTestSupport", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TestUtilitiesLibrary",
				"ZIPService",
			]
		),

		// MARK: - Libraries
		.target(
			name: "AchievementsLibrary",
			dependencies: [
				"AssetsLibrary",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "AchievementsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AchievementsLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "AssetsLibrary",
			dependencies: [],
			resources: [
				.process("Resources"),
			]
		),
		.target(
			name: "ComposableExtensionsLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
			]
		),
		.target(
			name: "ConstantsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"StringsLibrary",
			],
			resources: [
				.process("Resources"),
			]
		),
		.target(
			name: "DatabaseMigrationsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "ErrorReportingClientPackageLibrary", package: "swift-utilities"),
				.product(name: "GRDB", package: "GRDB.swift"),
				.product(name: "GRDBDatabasePackageLibrary", package: "swift-utilities"),
				"ScoreKeeperLibrary",
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
				"TestUtilitiesLibrary",
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
			dependencies: [
				.product(name: "FeatureFlagsPackageLibrary", package: "swift-utilities"),
				.product(name: "FeatureFlagsPackageServiceInterface", package: "swift-utilities"),
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ResourceListLibrary",
			dependencies: [
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ComposableExtensionsLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
			]
		),
		.testTarget(
			name: "ResourceListLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourceListLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ResourcePickerLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ComposableExtensionsLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
			]
		),
		.testTarget(
			name: "ResourcePickerLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourcePickerLibrary",
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "ScoreSheetLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ModelsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ScoreSheetLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreSheetLibrary",
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "SortOrderLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"FeatureActionLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "SortOrderLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SortOrderLibrary",
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "StatisticsChartsLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
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
				"TestUtilitiesLibrary",
			]
		),
		.target(
			name: "TestDatabaseUtilitiesLibrary",
			dependencies: [
				.product(name: "GRDBDatabaseTestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseMigrationsLibrary",
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
				"TestUtilitiesLibrary",
				"TipsLibrary",
			]
		),
		.target(
			name: "ToastLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "ToastUI", package: "ToastUI"),
				"AssetsLibrary",
			]
		),
		.testTarget(
			name: "ToastLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TestUtilitiesLibrary",
				"ToastLibrary",
			]
		),
		.target(
			name: "ViewsLibrary",
			dependencies: [
				"AssetsLibrary",
				"DateTimeLibrary",
				"StringsLibrary",
			]
		),
		.testTarget(
			name: "ViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TestUtilitiesLibrary",
				"ViewsLibrary",
			]
		),
	]
)
