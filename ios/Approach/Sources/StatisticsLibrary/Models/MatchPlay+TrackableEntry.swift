import Foundation
import ModelsLibrary

extension MatchPlay {
	public struct TrackableEntry: Identifiable, Codable, Equatable {
		public let id: MatchPlay.ID
		public let result: MatchPlay.Result?
	}
}
