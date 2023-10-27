// swiftlint:disable line_length

extension FeatureFlag {
	public static let alleys = Self(name: "alleys", introduced: "2022-11-09", stage: .release)
	public static let gear = Self(name: "gear", introduced: "2022-11-09", stage: .release)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .release, isOverridable: false)
	public static let lanes = Self(name: "lanes", introduced: "2022-12-09", stage: .release)
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teams = Self(name: "teams", introduced: "2023-01-03", stage: .development)
	public static let opponents = Self(name: "opponents", introduced: "2023-01-08", stage: .release)
	public static let avatars = Self(name: "avatars", introduced: "2023-03-11", stage: .release)
	public static let overviewTab = Self(name: "overviewTab", introduced: "2023-05-18", stage: .release)
	public static let statisticsTab = Self(name: "statisticsTab", introduced: "2023-05-18", stage: .release)
	public static let accessoriesTab = Self(name: "accessoriesTab", introduced: "2023-05-18", stage: .release)
	public static let appIconConfig = Self(name: "appIconConfig", introduced: "2023-07-07", stage: .release)
	public static let opponentDetails = Self(name: "opponentDetails", introduced: "2023-08-18", stage: .development)
	public static let sharingGame = Self(name: "sharingGame", introduced: "2023-08-29", stage: .development)
	public static let sharingSeries = Self(name: "sharingSeries", introduced: "2023-08-29", stage: .development)
	public static let alleyAndGearAverages = Self(name: "alleyAndGearAverages", introduced: "2023-09-12", stage: .development)
	public static let proSubscription = Self(name: "proSubscription", introduced: "2023-08-08", stage: .development)
	public static let purchases = Self(name: "purchase", introduced: "2023-09-15", stage: .disabled)
	public static let dataExport = Self(name: "dataExport", introduced: "2023-09-17", stage: .release)
	public static let dataImport = Self(name: "dataImport", introduced: "2023-09-17", stage: .development)
	public static let statisticsDescriptions = Self(name: "statisticsDescriptions", introduced: "2023-10-06", stage: .release)
	public static let statisticsIssueReports = Self(name: "statisticsIssueReports", introduced: "2023-10-26", stage: .development)

	public static let allFlags: [Self] = [
		.accessoriesTab,
		.alleys,
		.alleyAndGearAverages,
		.appIconConfig,
		.avatars,
		.dataExport,
		.dataImport,
		.developerOptions,
		.gear,
		.lanes,
		.opponents,
		.opponentDetails,
		.overviewTab,
		.proSubscription,
		.purchases,
		.settingsTab,
		.sharingGame,
		.sharingSeries,
		.statisticsDescriptions,
		.statisticsIssueReports,
		.statisticsTab,
		.teams,
	]
}

// swiftlint:enable line_length
