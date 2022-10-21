import Foundation

public struct Frame: Sendable, Hashable, Codable {
	public let gameId: Game.ID
	public let ordinal: Int
	public let isAccessed: Bool
	public let firstBall: Ball?
	public let secondBall: Ball?
	public let thirdBall: Ball?

	public init(
		gameId: Game.ID,
		ordinal: Int,
		isAccessed: Bool,
		firstBall: Ball?,
		secondBall: Ball?,
		thirdBall: Ball?
	) {
		self.gameId = gameId
		self.ordinal = ordinal
		self.isAccessed = isAccessed
		self.firstBall = firstBall
		self.secondBall = secondBall
		self.thirdBall = thirdBall
	}
}

// MARK: - Ball
extension Frame {
	public struct Ball: Sendable, Hashable, Codable {
		public let deck: Deck
		public let isFoul: Bool

		public init(deck: Deck, isFoul: Bool) {
			self.deck = deck
			self.isFoul = isFoul
		}
	}
}

// MARK: - Deck

extension Frame {
	public struct Deck: Sendable, Hashable, Codable {
		public static let fullDeck: Deck = .init(
			leftTwoPinKnocked: false,
			leftThreePinKnocked: false,
			rightTwoPinKnocked: false,
			rightThreePinKnocked: false,
			headPinKnocked: false
		)

		public let leftTwoPinKnocked: Bool
		public let leftThreePinKnocked: Bool
		public let rightTwoPinKnocked: Bool
		public let rightThreePinKnocked: Bool
		public let headPinKnocked: Bool

		public init(
			leftTwoPinKnocked: Bool,
			leftThreePinKnocked: Bool,
			rightTwoPinKnocked: Bool,
			rightThreePinKnocked: Bool,
			headPinKnocked: Bool
		) {
			self.leftTwoPinKnocked = leftTwoPinKnocked
			self.leftThreePinKnocked = leftThreePinKnocked
			self.rightTwoPinKnocked = rightTwoPinKnocked
			self.rightThreePinKnocked = rightThreePinKnocked
			self.headPinKnocked = headPinKnocked
		}
	}
}
