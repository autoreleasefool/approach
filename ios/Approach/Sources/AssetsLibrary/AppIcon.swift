import UIKit

public enum AppIcon: String, CaseIterable, Identifiable, Sendable {
	case primary = "AppIcon"
	case dark = "AppIcon-Dark"

	case bisexual = "AppIcon-Bisexual"
	case christmas = "AppIcon-Christmas"
	case pride = "AppIcon-Pride"
	case purple = "AppIcon-Purple"
	case trans = "AppIcon-Trans"

	public var id: String { rawValue }

	public var category: Category {
		switch self {
		case .primary, .purple, .dark: return .standard
		case .christmas: return .seasonal
		case .bisexual, .pride, .trans: return .pride
		}
	}

	public var previewName: String {
		"\(id)-Preview"
	}

	public var image: UIImage? {
		UIImage(named: previewName)
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
