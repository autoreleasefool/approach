import Foundation
import SwiftUI
import SwiftUIExtensionsLibrary

public enum Avatar: Sendable, Hashable, Codable {
	case data(Data)
	case text(String, Background)

	public var image: UIImage? {
		switch self {
		case let .data(data):
			return UIImage(data: data)

			// TODO: read color and text
		case .text:
			return UIImage()
		}
	}

	
}

extension Avatar {
	public enum Background: Sendable, Hashable, Codable {
		case rgb(Double, Double, Double)

		public static func random() -> Self {
			.rgb(0, 0, 0)
		}
	}
}
