import Foundation
import ModelsLibrary

extension Game {
	public struct TrackableEntry: Identifiable, Decodable {
		public let id: Game.ID
		public let score: Int
		public let date: Date
	}
}
