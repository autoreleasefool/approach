import Foundation

extension Analytics.MatchPlay {
	public struct Deleted: TrackableEvent {
		public let name = "MatchPlay.Deleted"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
