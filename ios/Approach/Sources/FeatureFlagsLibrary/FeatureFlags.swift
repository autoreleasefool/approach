// swiftlint:disable line_length

extension FeatureFlag {
	public static let alleys = Self(name: "alleys", introduced: "2022-11-09", stage: .development)
	public static let gear = Self(name: "gear", introduced: "2022-11-09", stage: .development)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .development, isOverridable: false)
	public static let lanes = Self(name: "lanes", introduced: "2022-12-09", stage: .development)
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teams = Self(name: "teams", introduced: "2023-01-03", stage: .development)
	public static let opponents = Self(name: "opponents", introduced: "2023-01-08", stage: .development)
	public static let avatars = Self(name: "avatars", introduced: "2023-03-11", stage: .disabled)
	public static let overviewTab = Self(name: "overviewTab", introduced: "2023-05-18", stage: .development)
	public static let statisticsTab = Self(name: "statisticsTab", introduced: "2023-05-18", stage: .development)
	public static let accessoriesTab = Self(name: "accessoriesTab", introduced: "2023-05-18", stage: .development)
	public static let appIconConfig = Self(name: "appIconConfig", introduced: "2023-07-07", stage: .development)

	public static let allFlags: [Self] = [
		.accessoriesTab,
		.alleys,
		.appIconConfig,
		.avatars,
		.developerOptions,
		.gear,
		.lanes,
		.opponents,
		.overviewTab,
		.settingsTab,
		.statisticsTab,
		.teams,
	]
}

// swiftlint:enable line_length
