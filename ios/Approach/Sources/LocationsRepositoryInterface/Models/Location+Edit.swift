import Foundation
import ModelsLibrary

extension Location {
	public struct Edit: Identifiable, Codable, Equatable {
		public let id: Location.ID
		public var title: String
		public var coordinate: Location.Coordinate

		public init(id: Location.ID, title: String, coordinate: Location.Coordinate) {
			self.id = id
			self.title = title
			self.coordinate = coordinate
		}
	}
}
