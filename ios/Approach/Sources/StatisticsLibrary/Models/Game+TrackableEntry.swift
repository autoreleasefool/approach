import Foundation
import ModelsLibrary

extension Game {
	public struct TrackableEntry: Identifiable, Decodable, Equatable, Sendable {
		public let seriesId: Series.ID
		public let id: Game.ID
		public let index: Int
		public let score: Int
		public let date: Date
		public let matchPlay: MatchPlay.TrackableEntry?
	}
}
