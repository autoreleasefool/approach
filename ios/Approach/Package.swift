// swift-tools-version: 5.10

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
		.library(name: "PaywallFeature", targets: ["PaywallFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "SharingFeature", targets: ["SharingFeature"]),
		.library(name: "StatisticsDetailsFeature", targets: ["StatisticsDetailsFeature"]),
		.library(name: "StatisticsOverviewFeature", targets: ["StatisticsOverviewFeature"]),
		.library(name: "StatisticsWidgetEditorFeature", targets: ["StatisticsWidgetEditorFeature"]),
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
		.library(name: "AvatarService", targets: ["AvatarService"]),
		.library(name: "AvatarServiceInterface", targets: ["AvatarServiceInterface"]),
		.library(name: "DatabaseMockingService", targets: ["DatabaseMockingService"]),
		.library(name: "DatabaseMockingServiceInterface", targets: ["DatabaseMockingServiceInterface"]),
		.library(name: "DatabaseService", targets: ["DatabaseService"]),
		.library(name: "DatabaseServiceInterface", targets: ["DatabaseServiceInterface"]),
		.library(name: "EmailService", targets: ["EmailService"]),
		.library(name: "EmailServiceInterface", targets: ["EmailServiceInterface"]),
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
		.library(name: "ProductsService", targets: ["ProductsService"]),
		.library(name: "ProductsServiceInterface", targets: ["ProductsServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),
		.library(name: "TipsService", targets: ["TipsService"]),
		.library(name: "TipsServiceInterface", targets: ["TipsServiceInterface"]),
		.library(name: "ZIPService", targets: ["ZIPService"]),
		.library(name: "ZIPServiceInterface", targets: ["ZIPServiceInterface"]),

		// MARK: - Libraries
		.library(name: "AnnouncementsLibrary", targets: ["AnnouncementsLibrary"]),
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
		.library(name: "TestDatabaseUtilitiesLibrary", targets: ["TestDatabaseUtilitiesLibrary"]),
		.library(name: "TipsLibrary", targets: ["TipsLibrary"]),
		.library(name: "ToastLibrary", targets: ["ToastLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),
	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-algorithms.git", from: "1.2.0"),
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "1.0.0"),
		.package(url: "https://github.com/apple/swift-collections.git", from: "1.1.0"),
		.package(url: "https://github.com/autoreleasefool/swift-utilities.git", from: "2.4.1"),
		.package(url: "https://github.com/CocoaLumberjack/CocoaLumberjack.git", from: "3.8.5"),
		.package(url: "https://github.com/elai950/AlertToast.git", from: "1.3.9"),
		.package(url: "https://github.com/getsentry/sentry-cocoa.git", from: "8.30.1"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.28.0"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "1.11.2"),
		.package(url: "https://github.com/pointfreeco/swift-dependencies.git", from: "1.3.1"),
		.package(url: "https://github.com/pointfreeco/swift-identified-collections.git", from: "1.1.0"),
		.package(url: "https://github.com/pointfreeco/swift-snapshot-testing.git", from: "1.17.1"),
		.package(url: "https://github.com/pointfreeco/xctest-dynamic-overlay.git", from: "1.1.2"),
		.package(url: "https://github.com/RevenueCat/purchases-ios.git", from: "4.37.0"),
		.package(url: "https://github.com/SFSafeSymbols/SFSafeSymbols.git", from: "5.3.0"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AccessoriesOverviewFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AccessoriesOverviewFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AddressLookupFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AlleyEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleyEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"AlleyEditorFeature",
				"FeatureFlagsLibrary",
				"ResourceListLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AlleysListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AnnouncementsFeature",
			dependencies: [
				"AnalyticsServiceInterface",
				"AnnouncementsServiceInterface",
				"FeatureActionLibrary",
				"LoggingServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AnnouncementsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AppFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AppFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ArchiveListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ArchiveListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AvatarEditorFeature",
			dependencies: [
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"AnalyticsServiceInterface",
				"AvatarServiceInterface",
				"FeatureActionLibrary",
				"FeatureFlagsLibrary",
				"LoggingServiceInterface",
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AvatarEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "BowlerDetailsFeature",
			dependencies: [
				"BowlerEditorFeature",
				"LeaguesListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "BowlerDetailsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BowlersRepositoryInterface",
				"FormFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "BowlerEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlerEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"AnnouncementsFeature",
				"BowlerDetailsFeature",
				"QuickLaunchRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"BowlersListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ErrorsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ErrorsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "FeatureFlagsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FeatureFlagsListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "FormFeature",
			dependencies: [
				"ErrorsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "FormFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"FormFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "GamesEditorFeature",
			dependencies: [
				.product(name: "StoreReviewPackageServiceInterface", package: "swift-utilities"),
				"AvatarServiceInterface",
				"FramesRepositoryInterface",
				"GearRepositoryInterface",
				"LanesRepositoryInterface",
				"StatisticsDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GamesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "GamesListFeature",
			dependencies: [
				"GamesEditorFeature",
				"ResourceListLibrary",
				"SeriesEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GamesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GamesListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GearEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "GearListFeature",
			dependencies: [
				"GearEditorFeature",
				"RecentlyUsedServiceInterface",
				"ResourceListLibrary",
				"SortOrderLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GearListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"GearListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ImportExportFeature",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"DateTimeLibrary",
				"ErrorsFeature",
				"ImportExportServiceInterface",
				"PreferenceServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ImportExportFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LaneEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaneEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LeagueEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeagueEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"SeriesListFeature",
				"StatisticsWidgetsLayoutFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LeaguesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LeaguesListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "OnboardingFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OnboardingFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "OpponentDetailsFeature",
			dependencies: [
				"BowlerEditorFeature",
				"GamesRepositoryInterface",
				"ResourceListLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "OpponentDetailsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "OpponentsListFeature",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"FeatureFlagsLibrary",
				"ModelsViewsLibrary",
				"OpponentDetailsFeature",
				"RecentlyUsedServiceInterface",
				"SortOrderLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "OpponentsListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"OpponentsListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "PaywallFeature",
			dependencies: [
				"ErrorsFeature",
				"ProductsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "PaywallFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PaywallFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysRepositoryInterface",
				"DateTimeLibrary",
				"FeatureFlagsLibrary",
				"FormFeature",
				"ModelsViewsLibrary",
				"PickableModelsLibrary",
				"SeriesRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"GamesListFeature",
				"LeagueEditorFeature",
				"SortOrderLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SeriesListFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SeriesListFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SettingsFeature",
			dependencies: [
				.product(name: "BundlePackageServiceInterface", package: "swift-utilities"),
				"AppIconServiceInterface",
				"ArchiveListFeature",
				"DatabaseMockingServiceInterface",
				"FeatureFlagsListFeature",
				"ImportExportFeature",
				"OpponentsListFeature",
				"ProductsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SettingsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SettingsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SharingFeature",
			dependencies: [
				"GamesRepositoryInterface",
				"ScoreSheetLibrary",
				"ScoresRepositoryInterface",
				"SeriesRepositoryInterface",
				"StatisticsWidgetEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SharingFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SharingFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsDetailsFeature",
			dependencies: [
				"FeatureFlagsLibrary",
				"NotificationsServiceInterface",
				"PreferenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SharingFeature",
				"TipsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsDetailsFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsOverviewFeature",
			dependencies: [
				"StatisticsDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsOverviewFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsOverviewFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsWidgetEditorFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetEditorFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsWidgetsLayoutFeature",
			dependencies: [
				"ReorderingLibrary",
				"StatisticsDetailsFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsWidgetsLayoutFeatureTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetsLayoutFeature",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AlleysRepositoryInterface",
			dependencies: [
				"LanesRepositoryInterface",
				"LocationsRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AlleysRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"AlleysRepository",
				"DatabaseService",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "BowlersRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "BowlersRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"BowlersRepository",
				"DatabaseService",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "FramesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"FramesRepositoryInterface",
				"RepositoryLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "FramesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"ModelsLibrary",
				"ScoreKeeperLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "FramesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"FramesRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "GamesRepositoryInterface",
			dependencies: [
				"MatchPlaysRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GamesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"GamesRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "GearRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "GearRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"GearRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LanesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LanesRepositoryInterface",
				"RepositoryLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LanesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LanesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"LanesRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LeaguesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LeaguesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"LeaguesRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LocationsRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"LocationsRepositoryInterface",
				"RepositoryLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LocationsRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LocationsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"LocationsRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "MatchPlaysRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"MatchPlaysRepositoryInterface",
				"RepositoryLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "MatchPlaysRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "MatchPlaysRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"MatchPlaysRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "QuickLaunchRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "QuickLaunchRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"QuickLaunchRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ScoresRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ScoresRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"FramesRepository",
				"GamesRepository",
				"ScoresRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SeriesRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RepositoryLibrary",
				"SeriesRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SeriesRepositoryInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SeriesRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"SeriesRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsRepository",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"DatabaseServiceInterface",
				"FeatureFlagsLibrary",
				"PreferenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"RepositoryLibrary",
				"StatisticsModelsLibrary",
				"StatisticsRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsRepositoryInterface",
			dependencies: [
				.product(name: "Collections", package: "swift-collections"),
				"StatisticsWidgetsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"StatisticsRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsWidgetsRepository",
			dependencies: [
				"DatabaseModelsLibrary",
				"DatabaseServiceInterface",
				"RepositoryLibrary",
				"StatisticsWidgetsRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsWidgetsRepositoryInterface",
			dependencies: [
				"StatisticsRepositoryInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsWidgetsRepositoryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				.product(name: "TestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseService",
				"StatisticsRepository",
				"StatisticsWidgetsRepository",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),

		// MARK: - Data Providers

		// MARK: - Services
		.target(
			name: "AddressLookupService",
			dependencies: [
				"AddressLookupServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AddressLookupServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "EquatablePackageLibrary", package: "swift-utilities"),
				.product(name: "XCTestDynamicOverlay", package: "xctest-dynamic-overlay"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AddressLookupServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AddressLookupService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AnalyticsService",
			dependencies: [
				.product(name: "Sentry", package: "sentry-cocoa"),
				"AnalyticsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AnalyticsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnalyticsService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AnnouncementsService",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"AnnouncementsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AnnouncementsServiceInterface",
			dependencies: [
				"AnnouncementsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AnnouncementsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AppIconService",
			dependencies: [
				"AppIconServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AppIconServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"AssetsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AvatarService",
			dependencies: [
				.product(name: "ExtensionsPackageLibrary", package: "swift-utilities"),
				"AvatarServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AvatarServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ModelsViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AvatarServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AvatarService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DatabaseMockingService",
			dependencies: [
				"DatabaseMockingServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DatabaseMockingServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DatabaseService",
			dependencies: [
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				"DatabaseMigrationsLibrary",
				"DatabaseServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DatabaseServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
				.product(name: "GRDBDatabasePackageServiceInterface", package: "swift-utilities"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "DatabaseServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "EmailService",
			dependencies: [
				"EmailServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "EmailServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "EmailServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"EmailService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ImportExportService",
			dependencies: [
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				"DatabaseServiceInterface",
				"DateTimeLibrary",
				"ImportExportServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ImportExportServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ImportExportServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ImportExportService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
				"ProductsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LaunchServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LaunchServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LaunchService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LoggingService",
			dependencies: [
				.product(name: "CocoaLumberjack", package: "CocoaLumberjack"),
				.product(name: "CocoaLumberjackSwift", package: "CocoaLumberjack"),
				"LoggingServiceInterface",
				"ZIPServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "LoggingServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "LoggingServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"LoggingService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "NotificationsService",
			dependencies: [
				"NotificationsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "NotificationsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "NotificationsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"NotificationsService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "PreferenceService",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"PreferenceServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "PreferenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "PreferenceServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PreferenceService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ProductsService",
			dependencies: [
				.product(name: "BundlePackageServiceInterface", package: "swift-utilities"),
				.product(name: "RevenueCat", package: "purchases-ios"),
				"ConstantsLibrary",
				"ProductsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ProductsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ProductsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ProductsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ProductsService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "RecentlyUsedService",
			dependencies: [
				.product(name: "Algorithms", package: "swift-algorithms"),
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"RecentlyUsedServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "RecentlyUsedServiceInterface",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "Dependencies", package: "swift-dependencies"),
				"StatisticsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "RecentlyUsedServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"RecentlyUsedService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "TipsService",
			dependencies: [
				.product(name: "UserDefaultsPackageServiceInterface", package: "swift-utilities"),
				"TipsServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "TipsServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"TipsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "TipsServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TipsService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ZIPService",
			dependencies: [
				.product(name: "FileManagerPackageServiceInterface", package: "swift-utilities"),
				.product(name: "ZIPFoundation", package: "ZIPFoundation"),
				"ZIPServiceInterface",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ZIPServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "DependenciesMacros", package: "swift-dependencies"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ZIPServiceTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ZIPService",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),

		// MARK: - Libraries
		.target(
			name: "AnnouncementsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "AnnouncementsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"AnnouncementsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "AssetsLibrary",
			dependencies: [
				.product(name: "SFSafeSymbols", package: "SFSafeSymbols"),
			],
			resources: [
				.process("Resources"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ComposableExtensionsLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DatabaseModelsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "GRDB", package: "GRDB.swift"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "DateTimeLibrary",
			dependencies: [],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "DateTimeLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DateTimeLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "FeatureActionLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "FeatureFlagsLibrary",
			dependencies: [
				.product(name: "FeatureFlagsPackageLibrary", package: "swift-utilities"),
				.product(name: "FeatureFlagsPackageServiceInterface", package: "swift-utilities"),
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ListContentLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ListContentLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ListContentLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ModelsLibrary",
			dependencies: [
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
				"ScoreKeeperModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ModelsLibraryTests",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ModelsViewsLibrary",
			dependencies: [
				"AssetsLibrary",
				"ModelsLibrary",
				"StringsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ModelsViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ModelsViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "PickableModelsLibrary",
			dependencies: [
				"ModelsLibrary",
				"ResourcePickerLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "PickableModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"PickableModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ProductsLibrary",
			dependencies: [],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ReorderingLibrary",
			dependencies: [
				.product(name: "IdentifiedCollections", package: "swift-identified-collections"),
				"FeatureActionLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ReorderingLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ReorderingLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "RepositoryLibrary",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"SortingLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "RepositoryLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"RepositoryLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
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
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ResourceListLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourceListLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ResourcePickerLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ComposableExtensionsLibrary",
				"FeatureActionLibrary",
				"ListContentLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ResourcePickerLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ResourcePickerLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ScoreKeeperLibrary",
			dependencies: [
				"ScoreKeeperModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ScoreKeeperLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreKeeperLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ScoreKeeperModelsLibrary",
			dependencies: [],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ScoreKeeperModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreKeeperModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ScoreSheetLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"ModelsLibrary",
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ScoreSheetLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ScoreSheetLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SortOrderLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"FeatureActionLibrary",
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SortOrderLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SortOrderLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "SortingLibrary",
			dependencies: [],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "SortingLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"SortingLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsChartsLibrary",
			dependencies: [
				.product(name: "SwiftUIExtensionsPackageLibrary", package: "swift-utilities"),
				"AssetsLibrary",
				"DateTimeLibrary",
				"StatisticsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsChartsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsChartsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsChartsMocksLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				"StatisticsChartsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsLibrary",
			dependencies: [
				"ModelsLibrary",
				"StringsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsLibraryTests",
			dependencies: [
				.product(name: "Dependencies", package: "swift-dependencies"),
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsModelsLibrary",
			dependencies: [
				"DatabaseModelsLibrary",
				"StatisticsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsModelsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"DatabaseService",
				"StatisticsModelsLibrary",
				"TestDatabaseUtilitiesLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StatisticsWidgetsLibrary",
			dependencies: [
				"StatisticsChartsMocksLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StatisticsWidgetsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StatisticsWidgetsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "StringsLibrary",
			dependencies: [],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "StringsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"StringsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "TestDatabaseUtilitiesLibrary",
			dependencies: [
				.product(name: "GRDBDatabaseTestUtilitiesPackageLibrary", package: "swift-utilities"),
				"DatabaseMigrationsLibrary",
				"DatabaseModelsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "TipsLibrary",
			dependencies: [
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "TipsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"TipsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ToastLibrary",
			dependencies: [
				.product(name: "AlertToast", package: "AlertToast"),
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"AssetsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ToastLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ToastLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.target(
			name: "ViewsLibrary",
			dependencies: [
				"AssetsLibrary",
				"StringsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
		.testTarget(
			name: "ViewsLibraryTests",
			dependencies: [
				.product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
				"ViewsLibrary",
			],
			swiftSettings: [
				.enableExperimentalFeature("StrictConcurrency"),
			]
		),
	]
)
