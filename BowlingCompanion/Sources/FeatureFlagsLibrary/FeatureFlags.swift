// swiftlint:disable line_length

extension FeatureFlag {
	public static let scoreSheetTab = Self(name: "scoreSheetTab", introduced: "2022-11-09", stage: .development)
	public static let alleyTracking = Self(name: "alleyTracking", introduced: "2022-11-09", stage: .development)
	public static let gearTracking = Self(name: "gearTracking", introduced: "2022-11-09", stage: .development)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .development, isOverridable: false)
	public static let lanesTracking = Self(name: "lanesTracking", introduced: "2022-12-09", stage: .development)
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teamsTracking = Self(name: "teamsTracking", introduced: "2023-01-03", stage: .development)

	public static let allFlags: [Self] = [
		.alleyTracking,
		.gearTracking,
		.lanesTracking,
		.scoreSheetTab,
		.teamsTracking,
		.settingsTab,
		.developerOptions,
	]
}
