import UIKit

public enum AppIcon: String, CaseIterable, Identifiable, Sendable {
	// Primary
	case primary = "AppIcon"
	case dark = "AppIcon-Dark"
	case purple = "AppIcon-Purple"

	// Pride
	case bisexual = "AppIcon-Bisexual"
	case pride = "AppIcon-Pride"
	case trans = "AppIcon-Trans"

	// Christmas
	case christmas = "AppIcon-Christmas"

	// Halloween
	case candyCorn = "AppIcon-CandyCorn"
	case devilHorns = "AppIcon-DevilHorns"
	case witchHat = "AppIcon-WitchHat"

	public var id: String { rawValue }

	public var category: Category {
		switch self {
		case .primary, .purple, .dark: .standard
		case .christmas: .christmas
		case .witchHat, .candyCorn, .devilHorns: .halloween
		case .bisexual, .pride, .trans: .pride
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
		case halloween
		case pride
		case christmas

		public var id: Int { rawValue }

		public var matchingIcons: [AppIcon] {
			AppIcon.allCases.filter { $0.category == self }
		}
	}
}
