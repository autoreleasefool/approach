import CoreLocation
import Foundation

public enum Location {}

extension Location {
	public typealias ID = UUID
}

extension Location {
	public struct Coordinate: Equatable, Codable {
		public let latitude: Double
		public let longitude: Double

		public init(latitude: Double, longitude: Double) {
			self.latitude = latitude
			self.longitude = longitude
		}

		public var mapCoordinate: CLLocationCoordinate2D {
			.init(latitude: latitude, longitude: longitude)
		}
	}
}

extension Location {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Location.ID
		public let title: String
		public let subtitle: String
		public let coordinate: Coordinate

		public init(id: Location.ID, title: String, subtitle: String, coordinate: Coordinate) {
			self.id = id
			self.title = title
			self.subtitle = subtitle
			self.coordinate = coordinate
		}

		public init(from decoder: Decoder) throws {
			let container = try decoder.container(keyedBy: CodingKeys.self)
			self.id = try container.decode(UUID.self, forKey: CodingKeys.id)
			self.title = try container.decode(String.self, forKey: CodingKeys.title)
			self.subtitle = try container.decode(String.self, forKey: CodingKeys.subtitle)
			let latitude = try container.decode(Double.self, forKey: CodingKeys.latitude)
			let longitude = try container.decode(Double.self, forKey: CodingKeys.longitude)
			self.coordinate = .init(latitude: latitude, longitude: longitude)
		}

		public func encode(to encoder: Encoder) throws {
			var container = encoder.container(keyedBy: CodingKeys.self)
			try container.encode(id, forKey: CodingKeys.id)
			try container.encode(title, forKey: CodingKeys.title)
			try container.encode(subtitle, forKey: CodingKeys.subtitle)
			try container.encode(coordinate.latitude, forKey: CodingKeys.latitude)
			try container.encode(coordinate.longitude, forKey: CodingKeys.longitude)
		}

		enum CodingKeys: CodingKey {
			case id
			case title
			case subtitle
			case latitude
			case longitude
		}
	}
}
