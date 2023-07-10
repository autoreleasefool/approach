import Foundation

extension Analytics.MatchPlay {
	public struct Created: TrackableEvent {
		public let name = "MatchPlay.Created"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
