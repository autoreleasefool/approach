import StringsLibrary
import SwiftUI

public enum Appearance: CaseIterable, Hashable, Identifiable {
	case dark
	case light

	public var id: Self { self }

	var colorScheme: ColorScheme {
		switch self {
		case .dark: .dark
		case .light: .light
		}
	}

	var title: String {
		switch self {
		case .light: Strings.Sharing.Common.ColorScheme.light
		case .dark: Strings.Sharing.Common.ColorScheme.dark
		}
	}
}
