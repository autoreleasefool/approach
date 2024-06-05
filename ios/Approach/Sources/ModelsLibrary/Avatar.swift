import Foundation
import SwiftUI

public enum Avatar {}

extension Avatar {
	public typealias ID = UUID
}

extension Avatar {
	public enum Value: Sendable, Hashable, Codable {
		case url(URL)
		case data(Data)
		case text(String, Background)
	}
}

extension Avatar {
	public enum Background: Sendable, Hashable, Codable {
		case rgb(RGB)
		case gradient(RGB, RGB)

		public static var `default`: Self {
			.rgb(.default)
		}
	}
}

extension Avatar.Background {
	public struct RGB: Sendable, Hashable, Codable, CustomStringConvertible {
		public let red: CGFloat
		public let green: CGFloat
		public let blue: CGFloat

		public init(red: CGFloat, green: CGFloat, blue: CGFloat) {
			self.red = red
			self.green = green
			self.blue = blue
		}

		public init(_ red: CGFloat, _ green: CGFloat, _ blue: CGFloat) {
			self.red = red
			self.green = green
			self.blue = blue
		}

		public var uiColor: UIColor {
			.init(red: red, green: green, blue: blue, alpha: 1)
		}

		public var color: Color {
			Color(uiColor: uiColor)
		}

		public static var `default`: Self {
			.init(134 / 255.0, 128 / 255.0, 223 / 255.0)
		}

		public var description: String {
			"rgb(\(red),\(green),\(blue))"
		}
	}
}

extension Avatar {
	public struct Summary: Sendable, Identifiable, Codable, Hashable {
		public let id: Avatar.ID
		public let value: Value

		public init(id: Avatar.ID, value: Value) {
			self.id = id
			self.value = value
		}
	}
}
