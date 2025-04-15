public enum ShareableGamesImage {
	public enum Style: CaseIterable, Identifiable, Sendable {
		case plain
		case grayscale

		public var id: Self { self }
	}
}
