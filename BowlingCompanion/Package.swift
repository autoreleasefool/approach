// swift-tools-version: 5.7.1
// swiftlint:disable file_length

import PackageDescription

let package = Package(
	name: "BowlingCompanion",
	platforms: [
		.iOS(.v16),
	],
	products: [
		// MARK: - Features
		.library(name: "AlleyEditorFeature", targets: ["AlleyEditorFeature"]),
		.library(name: "AlleysListFeature", targets: ["AlleysListFeature"]),
		.library(name: "AppFeature", targets: ["AppFeature"]),
		.library(name: "BaseFormFeature", targets: ["BaseFormFeature"]),
		.library(name: "BowlerEditorFeature", targets: ["BowlerEditorFeature"]),
		.library(name: "BowlersListFeature", targets: ["BowlersListFeature"]),
		.library(name: "FeatureFlagListFeature", targets: ["FeatureFlagListFeature"]),
		.library(name: "GameEditorFeature", targets: ["GameEditorFeature"]),
		.library(name: "GearEditorFeature", targets: ["GearEditorFeature"]),
		.library(name: "GearListFeature", targets: ["GearListFeature"]),
		.library(name: "LaneEditorFeature", targets: ["LaneEditorFeature"]),
		.library(name: "LeagueEditorFeature", targets: ["LeagueEditorFeature"]),
		.library(name: "LeaguesListFeature", targets: ["LeaguesListFeature"]),
		.library(name: "ResourcePickerFeature", targets: ["ResourcePickerFeature"]),
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
		.library(name: "LanesDataProvider", targets: ["LanesDataProvider"]),
		.library(name: "LanesDataProviderInterface", targets: ["LanesDataProviderInterface"]),
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
		.library(name: "SharedModelsMocksLibrary", targets: ["SharedModelsMocksLibrary"]),
		.library(name: "SharedModelsViewsLibrary", targets: ["SharedModelsViewsLibrary"]),
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "AlleysListFeature",
			dependencies: [
				"AlleyEditorFeature",
				"AlleysDataProviderInterface",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "AlleysListFeatureTests",
			dependencies: [
				"AlleysListFeature",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "BowlersListFeature",
			dependencies: [
				"BowlerEditorFeature",
				"BowlersDataProviderInterface",
				"FeatureFlagServiceInterface",
				"LeaguesListFeature",
				"StatisticsWidgetsFeature",
			]
		),
		.testTarget(
			name: "BowlersListFeatureTests",
			dependencies: [
				"BowlersListFeature",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearEditorFeature",
			dependencies: [
				"BaseFormFeature",
				"BowlersDataProviderInterface",
				"PersistenceServiceInterface",
				"ResourcePickerFeature",
			]
		),
		.testTarget(
			name: "GearEditorFeatureTests",
			dependencies: [
				"GearEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "GearListFeature",
			dependencies: [
				"GearDataProviderInterface",
				"GearEditorFeature",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "GearListFeatureTests",
			dependencies: [
				"GearListFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LaneEditorFeature",
			dependencies: [
				"LanesDataProviderInterface",
				"PersistenceServiceInterface",
				"SwiftUIExtensionsLibrary",
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "LaneEditorFeatureTests",
			dependencies: [
				"LaneEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "LeagueEditorFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"BaseFormFeature",
				"PersistenceServiceInterface",
				"ResourcePickerFeature",
				"SharedModelsViewsLibrary",
			]
		),
		.testTarget(
			name: "LeagueEditorFeatureTests",
			dependencies: [
				"LeagueEditorFeature",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "ResourcePickerFeature",
			dependencies: [
				"ViewsLibrary",
			]
		),
		.testTarget(
			name: "ResourcePickerFeatureTests",
			dependencies: [
				"ResourcePickerFeature",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesEditorFeature",
			dependencies: [
				"AlleysDataProviderInterface",
				"BaseFormFeature",
				"DateTimeLibrary",
				"PersistenceServiceInterface",
			]
		),
		.testTarget(
			name: "SeriesEditorFeatureTests",
			dependencies: [
				"SeriesEditorFeature",
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SeriesListFeature",
			dependencies: [
				"SeriesDataProviderInterface",
				"SeriesEditorFeature",
				"SeriesSidebarFeature",
			]
		),
		.testTarget(
			name: "SeriesListFeatureTests",
			dependencies: [
				"SeriesListFeature",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "FramesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "GamesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"GamesDataProvider",
				"SharedModelsMocksLibrary",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "LanesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"LanesDataProvider",
				"SharedModelsMocksLibrary",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
				"SharedModelsLibrary",
			]
		),
		.testTarget(
			name: "SeriesDataProviderTests",
			dependencies: [
				.product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
				"SeriesDataProvider",
				"SharedModelsMocksLibrary",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "FileManagerServiceTests",
			dependencies: [
				"FileManagerService",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				.product(name: "Dependencies", package: "swift-composable-architecture"),
			]
		),
		.testTarget(
			name: "RecentlyUsedServiceTests",
			dependencies: [
				"RecentlyUsedService",
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
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
				"SharedModelsMocksLibrary",
			]
		),
		.target(
			name: "SharedModelsLibrary",
			dependencies: [
				"StringsLibrary",
			]
		),
		.target(
			name: "SharedModelsMocksLibrary",
			dependencies: [
				"SharedModelsLibrary",
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
