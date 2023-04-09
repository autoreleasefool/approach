import Foundation

extension Series {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date

		public init(id: Series.ID, date: Date) {
			self.id = id
			self.date = date
		}
	}
}
