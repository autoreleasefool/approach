public enum AppIcon: String, CaseIterable, Identifiable {
	case primary = "AppIcon"
	case bisexual = "AppIcon-Bisexual"
	case earth = "AppIcon-Earth"
	case ember = "AppIcon-Ember"
	case glacial = "AppIcon-Glacial"
	case hexed = "AppIcon-Hexed"
	case pink = "AppIcon-Pink"
	case pride = "AppIcon-Pride"
	case sage = "AppIcon-Sage"
	case trans = "AppIcon-Trans"

	public var id: String { rawValue }

	public var category: Category {
		switch self {
		case .primary, .earth, .ember, .glacial, .hexed, .pink, .sage: return .standard
		case .bisexual, .pride, .trans: return .pride
		}
	}
}

extension AppIcon {
	public enum Category: Int, CaseIterable, Identifiable {
		case standard
		case pride

		public var id: Int { rawValue }

		public var matchingIcons: [AppIcon] {
			AppIcon.allCases.filter { $0.category == self }
		}
	}
}
