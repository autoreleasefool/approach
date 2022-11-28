// swift-tools-version: 5.7

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleyPickerFeature", targets: ["AlleyPickerFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BaseFormFeature", targets: ["BaseFormFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "FeatureFlagListFeature", targets: ["FeatureFlagListFeature"]),
		.library(name: "GameEditorFeature", targets: ["GameEditorFeature"]),
		.library(name: "GearListFeature", targets: ["GearListFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "ScoreSheetFeature", targets: ["ScoreSheetFeature"]),
		.library(name: "SeriesEditorFeature", targets: ["SeriesEditorFeature"]),
		.library(name: "SeriesListFeature", targets: ["SeriesListFeature"]),
		.library(name: "SeriesSidebarFeature", targets: ["SeriesSidebarFeature"]),
		.library(name: "SettingsFeature", targets: ["SettingsFeature"]),
		.library(name: "StatisticsWidgetsFeature", targets: ["StatisticsWidgetsFeature"]),

		// MARK: - Data Providers
		.library(name: "AlleysDataProvider", targets: ["AlleysDataProvider"]),
		.library(name: "AlleysDataProviderInterface", targets: ["AlleysDataProviderInterface"]),
		.library(name: "BowlersDataProvider", targets: ["BowlersDataProvider"]),
		.library(name: "BowlersDataProviderInterface", targets: ["BowlersDataProviderInterface"]),
		.library(name: "FramesDataProvider", targets: ["FramesDataProvider"]),
		.library(name: "FramesDataProviderInterface", targets: ["FramesDataProviderInterface"]),
		.library(name: "GamesDataProvider", targets: ["GamesDataProvider"]),
		.library(name: "GamesDataProviderInterface", targets: ["GamesDataProviderInterface"]),
		.library(name: "GearDataProvider", targets: ["GearDataProvider"]),
		.library(name: "GearDataProviderInterface", targets: ["GearDataProviderInterface"]),
		.library(name: "LeaguesDataProvider", targets: ["LeaguesDataProvider"]),
		.library(name: "LeaguesDataProviderInterface", targets: ["LeaguesDataProviderInterface"]),
		.library(name: "SeriesDataProvider", targets: ["SeriesDataProvider"]),
		.library(name: "SeriesDataProviderInterface", targets: ["SeriesDataProviderInterface"]),

		// MARK: - Services
		.library(name: "FeatureFlagService", targets: ["FeatureFlagService"]),
		.library(name: "FeatureFlagServiceInterface", targets: ["FeatureFlagServiceInterface"]),
		.library(name: "FileManagerService", targets: ["FileManagerService"]),
		.library(name: "FileManagerServiceInterface", targets: ["FileManagerServiceInterface"]),
		.library(name: "PersistenceService", targets: ["PersistenceService"]),
		.library(name: "PersistenceServiceInterface", targets: ["PersistenceServiceInterface"]),
		.library(name: "PreferenceService", targets: ["PreferenceService"]),
		.library(name: "PreferenceServiceInterface", targets: ["PreferenceServiceInterface"]),
		.library(name: "RecentlyUsedService", targets: ["RecentlyUsedService"]),
		.library(name: "RecentlyUsedServiceInterface", targets: ["RecentlyUsedServiceInterface"]),

		// MARK: - Libraries
		.library(name: "ConstantsLibrary", targets: ["ConstantsLibrary"]),
		.library(name: "DateTimeLibrary", targets: ["DateTimeLibrary"]),
		.library(name: "FeatureFlagLibrary", targets: ["FeatureFlagLibrary"]),
		.library(name: "SharedModelsLibrary", targets: ["SharedModelsLibrary"]),
		.library(name: "SharedPersistenceModelsLibrary", targets: ["SharedPersistenceModelsLibrary"]),
		.library(name: "SortingLibrary", targets: ["SortingLibrary"]),
		.library(name: "StringsLibrary", targets: ["StringsLibrary"]),
		.library(name: "SwiftUIExtensionsLibrary", targets: ["SwiftUIExtensionsLibrary"]),
		.library(name: "ThemesLibrary", targets: ["ThemesLibrary"]),
		.library(name: "ViewsLibrary", targets: ["ViewsLibrary"]),

	],
	dependencies: [
		.package(url: "https://github.com/apple/swift-async-algorithms.git", from: "0.0.3"),
		.package(url: "https://github.com/pointfreeco/swift-composable-architecture.git", from: "0.42.0"),
		.package(url: "https://github.com/groue/GRDB.swift.git", from: "6.0.0"),
	],
	targets: [
		// MARK: - Features
		.target(
			name: "AlleyEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "AlleyEditorFeatureTests",
			dependencies: [
				"AlleyEditorFeature",
			]
		),
		.target(
			name: "AlleyPickerFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "AlleyPickerFeatureTests",
			dependencies: [
				"AlleyPickerFeature",
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				"AlleyEditorFeature",
				"AlleysDataProviderInterface",
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				"AlleysListFeature",
			]
		),
		.target(
			name: "AppFeature",
			dependencies: [
				"AlleysListFeature",
				"BowlersListFeature",
				"GearListFeature",
				"SettingsFeature",
			]
		),
		.testTarget(
			name: "AppFeatureTests",
			dependencies: [
				"AppFeature",
			]
		),
		.target(
			name: "BaseFormFeature",
			dependencies: [
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "BaseFormFeatureTests",
			dependencies: [
				"BaseFormFeature",
			]
		),
		.target(
			name: "BowlerEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "BowlerEditorFeatureTests",
			dependencies: [
				"BowlerEditorFeature",
			]
		),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"BowlerEditorFeature",
				"BowlersDataProviderInterface",
				"LeaguesListFeature",
				"StatisticsWidgetsFeature",
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				"BowlersListFeature",
			]
		),
		.target(
			name: "FeatureFlagListFeature",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"FeatureFlagServiceInterface",
			]
		),
		.testTarget(
			name: "FeatureFlagListFeatureTests",
			dependencies: [
				"FeatureFlagListFeature",
			]
		),
		.target(
			name: "GameEditorFeature",
			dependencies: [
				"SharedModelsLibrary",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "GameEditorFeatureTests",
			dependencies: [
				"GameEditorFeature",
			]
		),
		.target(
			name: "GearListFeature",
			dependencies: [
				"GearDataProviderInterface",
				"PersistenceServiceInterface",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "GearListFeatureTests",
			dependencies: [
				"GearListFeature",
			]
		),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				"AlleyPickerFeature",
				"BaseFormFeature",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "LeagueEditorFeatureTests",
			dependencies: [
				"LeagueEditorFeature",
			]
		),
		.target(
			name: "LeaguesListFeature",
			dependencies: [
				"LeagueEditorFeature",
				"LeaguesDataProviderInterface",
				"RecentlyUsedServiceInterface",
				"SeriesListFeature",
			]
		),
		.testTarget(
			name: "LeaguesListFeatureTests",
			dependencies: [
				"LeaguesListFeature",
			]
		),
		.target(
			name: "ScoreSheetFeature",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "ScoreSheetFeatureTests",
			dependencies: [
				"ScoreSheetFeature",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleyPickerFeature",
				"BaseFormFeature",
				"DateTimeLibrary",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				"SeriesEditorFeature",
			]
		),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"SeriesEditorFeature",
				"SeriesSidebarFeature",
			]
		),
		.testTarget(
			name: "SeriesListFeatureTests",
			dependencies: [
				"SeriesListFeature",
			]
		),
		.target(
			name: "SeriesSidebarFeature",
			dependencies: [
				"GameEditorFeature",
				"GamesDataProviderInterface",
			]
		),
		.testTarget(
			name: "SeriesSidebarFeatureTests",
			dependencies: [
				"SeriesSidebarFeature",
			]
		),
		.target(
			name: "SettingsFeature",
			dependencies: [
				"ConstantsLibrary",
				"FeatureFlagListFeature",
			]
		),
		.testTarget(
			name: "SettingsFeatureTests",
			dependencies: [
				"SettingsFeature",
			]
		),
		.target(
			name: "StatisticsWidgetsFeature",
			dependencies: [
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "StatisticsWidgetsFeatureTests",
			dependencies: [
				"StatisticsWidgetsFeature",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "AlleysDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"AlleysDataProvider",
			]
		),
		.target(
			name: "BowlersDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "BowlersDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "BowlersDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"BowlersDataProvider",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "FramesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"FramesDataProvider",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "GamesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"GamesDataProvider",
			]
		),
		.target(
			name: "GearDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"GearDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "GearDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "GearDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"GearDataProvider",
			]
		),
		.target(
			name: "LeaguesDataProvider",
			dependencies: [
				.product(name: "AsyncAlgorithms", package: "swift-async-algorithms"),
				"LeaguesDataProviderInterface",
				"PersistenceServiceInterface",
				"RecentlyUsedServiceInterface",
				"SortingLibrary",
			]
		),
		.target(
			name: "LeaguesDataProviderInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "LeaguesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"LeaguesDataProvider",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "SeriesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"SeriesDataProvider",
			]
		),

		// MARK: - Services
		.target(
			name: "FeatureFlagService",
			dependencies: [
				"FeatureFlagServiceInterface",
			]
		),
		.target(
			name: "FeatureFlagServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"FeatureFlagLibrary",
			]
		),
		.testTarget(
			name: "FeatureFlagServiceTests",
			dependencies: [
				"FeatureFlagService",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "FileManagerServiceTests",
			dependencies: [
				"FileManagerService",
			]
		),
		.target(
			name: "PersistenceService",
			dependencies: [
				"FileManagerServiceInterface",
				"PersistenceServiceInterface",
				"SharedPersistenceModelsLibrary",
			]
		),
		.target(
			name: "PersistenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "PersistenceServiceTests",
			dependencies: [
				"PersistenceService",
			]
		),
		.target(
			name: "PreferenceService",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"PreferenceServiceInterface",
			]
		),
		.target(
			name: "PreferenceServiceInterface",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "PreferenceServiceTests",
			dependencies: [
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "RecentlyUsedServiceTests",
			dependencies: [
				"RecentlyUsedService",
			]
		),

		// MARK: - Libraries
		.target(
			name: "ConstantsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "DateTimeLibrary",
			dependencies: []
		),
		.testTarget(
			name: "DateTimeLibraryTests",
			dependencies: [
				"DateTimeLibrary",
			]
		),
		.target(
			name: "FeatureFlagLibrary",
			dependencies: []
		),
		.testTarget(
			name: "FeatureFlagLibraryTests",
			dependencies: [
				"FeatureFlagLibrary",
			]
		),
		.target(
			name: "SharedModelsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "SharedPersistenceModelsLibrary",
			dependencies: [
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				.product(name: "GRDB", package: "GRDB.swift"),
				"SharedModelsLibrary",
			]
		),
		.target(
			name: "SortingLibrary",
			dependencies: []
		),
		.target(
			name: "StringsLibrary",
			dependencies: []
		),
		.target(
			name: "SwiftUIExtensionsLibrary",
			dependencies: []
		),
		.target(
			name: "ThemesLibrary",
			dependencies: []
		),
		.target(
			name: "ViewsLibrary",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"StringsLibrary",
				"ThemesLibrary",
			]
		),

	]
)
