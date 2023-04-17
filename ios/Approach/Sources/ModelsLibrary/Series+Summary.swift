import Foundation

extension Series {
	public struct Summary: Identifiable, Codable, Equatable {
		public let id: Series.ID
		public let date: Date
	}
}
