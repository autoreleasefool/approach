import AssetsLibrary
import DateTimeLibrary
import SwiftUI

extension DaysSince {
	public static let daysSinceWarningCutoff = 14

	public func warningSymbol(threshold: Int = Self.daysSinceWarningCutoff) -> (SFSymbol, Color) {
		switch self {
		case .never:
			(.exclamationmarkTriangleFill, Asset.Colors.Error.default.swiftUIColor)
		case let .days(days):
			if days >= threshold {
				(.exclamationmarkTriangle, Asset.Colors.Warning.default.swiftUIColor)
			} else {
				(.checkmarkSealFill, Asset.Colors.Success.default.swiftUIColor)
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
