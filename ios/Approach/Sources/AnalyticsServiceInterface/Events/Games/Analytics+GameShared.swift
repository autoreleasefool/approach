import Foundation

extension Analytics.Game {
	public struct Shared: TrackableEvent {
		public let name = "Game.Shared"
		public var payload: [String: String]? { nil }

		public init() {}
	}
}
