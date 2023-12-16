public enum AppIcon: String, CaseIterable, Identifiable {
	case primary = "AppIcon"

	case bisexual = "AppIcon-Bisexual"
	case christmas = "AppIcon-Christmas"
	case pride = "AppIcon-Pride"
	case purple = "AppIcon-Purple"
	case trans = "AppIcon-Trans"

	public var id: String { rawValue }

	public var category: Category {
		switch self {
		case .primary, .purple: return .standard
		case .christmas: return .seasonal
		case .bisexual, .pride, .trans: return .pride
		}
	}
}

extension AppIcon {
	public enum Category: Int, CaseIterable, Identifiable {
		case standard
		case seasonal
		case pride

		public var id: Int { rawValue }

		public var matchingIcons: [AppIcon] {
			AppIcon.allCases.filter { $0.category == self }
		}
	}
}
