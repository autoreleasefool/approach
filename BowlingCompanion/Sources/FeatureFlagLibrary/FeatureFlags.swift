// swiftlint:disable line_length

extension FeatureFlag {
	public static let scoreSheetTab = Self(name: "scoreSheetTab", introduced: "2022-11-09", stage: .development)
	public static let alleyTracking = Self(name: "alleyTracking", introduced: "2022-11-09", stage: .development)
	public static let gearTracking = Self(name: "gearTracking", introduced: "2022-11-09", stage: .development)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .development, isOverridable: false)
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)

	public static let allFlags: [Self] = [
		.alleyTracking,
		.gearTracking,
		.scoreSheetTab,
		.settingsTab,
		.developerOptions,
	]
}
