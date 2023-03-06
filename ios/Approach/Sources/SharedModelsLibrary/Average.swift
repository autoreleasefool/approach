import Foundation

public struct Average: Sendable {
	public let gamesPlayed: Int
	public let totalPinfall: Int

	public var value: Double {
		guard gamesPlayed > 0 else { return 0 }
		return Double(totalPinfall) / Double(gamesPlayed)
	}

	public init(gamesPlayed: Int, totalPinfall: Int) {
		self.gamesPlayed = gamesPlayed
		self.totalPinfall = totalPinfall
	}
}
