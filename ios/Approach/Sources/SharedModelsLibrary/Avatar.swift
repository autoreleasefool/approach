import Foundation
import SwiftUI

public enum Avatar: Sendable, Hashable, Codable {
	case url(URL)
	case data(Data)
	case text(String, Background)
}

extension Avatar {
	public enum Background: Sendable, Hashable, Codable, CustomStringConvertible {
		case rgb(Double, Double, Double)

		public static func red() -> Self {
			.rgb(1, 0, 0)
		}

		public var uiColor: UIColor {
			switch self {
			case let .rgb(red, green, blue):
				return UIColor(red: CGFloat(red), green: CGFloat(green), blue: CGFloat(blue), alpha: 1)
			}
		}

		public var color: Color {
			Color(uiColor: uiColor)
		}

		public var description: String {
			switch self {
			case let .rgb(red, green, blue):
				return "rgb(\(red),\(green),\(blue))"
			}
		}
	}
}
