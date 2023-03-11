// swiftlint:disable line_length

extension FeatureFlag {
	public static let scoreSheetTab = Self(name: "scoreSheetTab", introduced: "2022-11-09", stage: .development)
	public static let alleys = Self(name: "alleys", introduced: "2022-11-09", stage: .development)
	public static let gear = Self(name: "gear", introduced: "2022-11-09", stage: .development)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .development, isOverridable: false)
	public static let lanes = Self(name: "lanes", introduced: "2022-12-09", stage: .development)
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teams = Self(name: "teams", introduced: "2023-01-03", stage: .development)
	public static let opponents = Self(name: "opponents", introduced: "2023-01-08", stage: .development)
	public static let avatars = Self(name: "avatars", introduced: "2023-03-11", stage: .disabled)

	public static let allFlags: [Self] = [
		.alleys,
		.avatars,
		.developerOptions,
		.gear,
		.lanes,
		.opponents,
		.scoreSheetTab,
		.settingsTab,
		.teams,
	]
}
