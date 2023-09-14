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
	public enum Background: Sendable, Hashable, Codable, CustomStringConvertible {
		case rgb(CGFloat, CGFloat, CGFloat)

		public var uiColor: UIColor {
			switch self {
			case let .rgb(red, green, blue):
				return UIColor(red: red, green: green, blue: blue, alpha: 1)
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

extension Avatar {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Avatar.ID
		public let value: Value

		public init(id: Avatar.ID, value: Value) {
			self.id = id
			self.value = value
		}
	}
}
