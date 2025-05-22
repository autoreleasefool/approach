import AssetsLibrary
import DateTimeLibrary
import SwiftUI

extension DaysSince {
	public static let daysSinceWarningCutoff = 14

	public func warningImage(threshold: Int = Self.daysSinceWarningCutoff) -> (systemImage: String, Color) {
		switch self {
		case .never:
			("exclamationmark.triangle.fill", Asset.Colors.Error.default.swiftUIColor)
		case let .days(days):
			if days >= threshold {
				("exclamationmark.triangle", Asset.Colors.Warning.default.swiftUIColor)
			} else {
				("checkmark.icloud", Asset.Colors.Success.default.swiftUIColor)
			}
		}
	}

	public func bannerStyle(threshold: Int = Self.daysSinceWarningCutoff) -> (foreground: Color, background: Color) {
		switch self {
		case .never:
			(Asset.Colors.Text.onError.swiftUIColor, Asset.Colors.Error.default.swiftUIColor)
		case let .days(days):
			if days >= threshold {
				(.black, Asset.Colors.Warning.background.swiftUIColor)
			} else {
				(Asset.Colors.Text.onSuccess.swiftUIColor, Asset.Colors.Success.default.swiftUIColor)
			}
		}
	}
}
