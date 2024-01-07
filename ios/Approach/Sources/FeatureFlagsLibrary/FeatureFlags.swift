// swiftlint:disable line_length

extension FeatureFlag {
	public static let developerOptions = Self(name: "developerOptions", introduced: "2022-11-10", stage: .development, isOverridable: false)
	public static let teams = Self(name: "teams", introduced: "2023-01-03", stage: .development)
	public static let opponentDetails = Self(name: "opponentDetails", introduced: "2023-08-18", stage: .development)
	public static let sharingGame = Self(name: "sharingGame", introduced: "2023-08-29", stage: .development)
	public static let sharingSeries = Self(name: "sharingSeries", introduced: "2023-08-29", stage: .development)
	public static let alleyAndGearAverages = Self(name: "alleyAndGearAverages", introduced: "2023-09-12", stage: .development)
	public static let proSubscription = Self(name: "proSubscription", introduced: "2023-08-08", stage: .development)
	public static let purchases = Self(name: "purchase", introduced: "2023-09-15", stage: .disabled)
	public static let dataImport = Self(name: "dataImport", introduced: "2023-09-17", stage: .development)
	public static let seriesQuickCreate = Self(name: "seriesQuickCreate", introduced: "2023-11-19", stage: .release)

	public static let allFlags: [Self] = [
		.alleyAndGearAverages,
		.dataImport,
		.developerOptions,
		.opponentDetails,
		.proSubscription,
		.purchases,
		.seriesQuickCreate,
		.sharingGame,
		.sharingSeries,
		.teams,
	]
}

// swiftlint:enable line_length
