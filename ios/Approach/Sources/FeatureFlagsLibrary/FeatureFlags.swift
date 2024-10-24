// swiftlint:disable line_length
@_exported import FeatureFlagsPackageLibrary
@_exported import FeatureFlagsPackageServiceInterface

extension FeatureFlag {
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teams = Self(name: "teams", introduced: "2023-01-03", stage: .development)
	public static let opponentDetails = Self(name: "opponentDetails", introduced: "2023-08-18", stage: .development)
	public static let alleyAndGearAverages = Self(name: "alleyAndGearAverages", introduced: "2023-09-12", stage: .development)
	public static let proSubscription = Self(name: "proSubscription", introduced: "2023-08-08", stage: .development)
	public static let purchases = Self(name: "purchase", introduced: "2023-09-15", stage: .disabled)
	public static let dataImport = Self(name: "dataImport", introduced: "2023-09-17", stage: .release)
	public static let photoAvatars = Self(name: "photoAvatars", introduced: "2024-07-05", stage: .development)
	public static let bowlerDetails = Self(name: "bowlerDetails", introduced: "2024-07-05", stage: .development)
	public static let highestScorePossible = Self(name: "highestScorePossible", introduced: "2024-08-14", stage: .development)
	public static let badges = Self(name: "badges", introduced: "2024-10-03", stage: .development)
	public static let automaticBackups = Self(name: "automaticBackups", introduced: "2024-10-05", stage: .development)
	public static let crossPlatformImports = Self(name: "crossPlatformImports", introduced: "2024-10-17", stage: .release)

	public static let allFlags: [Self] = [
		.alleyAndGearAverages,
		.automaticBackups,
		.badges,
		.bowlerDetails,
		.crossPlatformImports,
		.dataImport,
		.developerOptions,
		.highestScorePossible,
		.opponentDetails,
		.photoAvatars,
		.proSubscription,
		.purchases,
		.teams,
	]

	public static func find(byId: FeatureFlag.ID) -> FeatureFlag? {
		allFlags.first { $0.id == byId }
	}
}

// swiftlint:enable line_length
