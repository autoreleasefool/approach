import Foundation
import ModelsLibrary

extension Location {
	public struct Edit: Identifiable, Codable, Equatable, Sendable {
		public let id: Location.ID
		public var title: String
		public var subtitle: String
		public var coordinate: Location.Coordinate

		public init(id: Location.ID, title: String, subtitle: String, coordinate: Location.Coordinate) {
			self.id = id
			self.title = title
			self.subtitle = subtitle
			self.coordinate = coordinate
		}

		public mutating func updateProperties(with location: Location.Edit) {
			self.title = location.title
			self.subtitle = location.subtitle
			self.coordinate = location.coordinate
		}
	}
}
