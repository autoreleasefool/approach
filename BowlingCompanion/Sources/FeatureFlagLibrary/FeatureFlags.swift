// swiftlint:disable line_length

extension FeatureFlag {
	public static let scoreSheetTab = Self(name: "scoreSheetTab", introduced: "2022-11-09", stage: .release)
	public static let alleysTab = Self(name: "alleysTab", introduced: "2022-11-09", stage: .development)
	public static let gearTab = Self(name: "gearTab", introduced: "2022-11-09", stage: .development)
	public static let settingsTab = Self(name: "settingsTab", introduced: "2022-11-09", stage: .release, isOverridable: false)

	public static let allFlags: [Self] = [
		.alleysTab,
		.gearTab,
		.scoreSheetTab,
		.settingsTab,
	]
}
