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
