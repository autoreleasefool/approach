import Foundation
import ModelsLibrary

extension Gear {
	public struct Edit: Identifiable, Equatable, Codable, Sendable {
		public let id: Gear.ID
		public let kind: Gear.Kind

		public var name: String
		public var owner: Bowler.Summary?
		public var avatar: Avatar.Summary

		public static let placeholder = Edit(
			id: Gear.ID(),
			kind: .bowlingBall,
			name: "",
			owner: nil,
			avatar: Avatar.Summary(id: Avatar.ID(), value: .text("", .default))
		)
	}
}
