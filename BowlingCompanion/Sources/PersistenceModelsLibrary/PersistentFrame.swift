import RealmSwift
import SharedModelsLibrary

public class PersistentFrame: EmbeddedObject {
	@Persisted public var ordinal = 0
	@Persisted public var isAccessed = false
	@Persisted public var firstBall: PersistentBall?
	@Persisted public var secondBall: PersistentBall?
	@Persisted public var thirdBall: PersistentBall?
}

public class PersistentBall: EmbeddedObject {
	@Persisted public var deck: PersistentDeck?
	@Persisted public var isFoul: Bool
}

public class PersistentDeck: EmbeddedObject {
	@Persisted public var leftTwoPinKnocked = false
	@Persisted public var leftThreePinKnocked = false
	@Persisted public var rightTwoPinKnocked = false
	@Persisted public var rightThreePinKnocked = false
	@Persisted public var headPinKnocked = false
}

// MARK: - Model

extension PersistentFrame {
	public convenience init(from frame: Frame) {
		self.init()
		self.ordinal = frame.ordinal
		self.isAccessed = frame.isAccessed
		self.firstBall = .init(from: frame.firstBall)
		self.secondBall = .init(from: frame.secondBall)
		self.thirdBall = .init(from: frame.thirdBall)
	}

	public var frame: Frame {
		.init(
			ordinal: ordinal,
			isAccessed: isAccessed,
			firstBall: firstBall?.ball,
			secondBall: secondBall?.ball,
			thirdBall: thirdBall?.ball
		)
	}
}

extension PersistentBall {
	public convenience init?(from ball: Frame.Ball?) {
		guard let ball else { return nil }
		self.init()
		self.deck = .init(from: ball.deck)
		self.isFoul = ball.isFoul
	}

	public var ball: Frame.Ball {
		.init(
			deck: deck?.deck ?? .fullDeck,
			isFoul: isFoul
		)
	}
}

extension PersistentDeck {
	public convenience init(from deck: Frame.Deck) {
		self.init()
		self.headPinKnocked = deck.headPinKnocked
		self.leftTwoPinKnocked = deck.leftTwoPinKnocked
		self.leftThreePinKnocked = deck.leftThreePinKnocked
		self.rightTwoPinKnocked = deck.rightTwoPinKnocked
		self.rightThreePinKnocked = deck.rightThreePinKnocked
	}

	public var deck: Frame.Deck {
		.init(
			leftTwoPinKnocked: leftTwoPinKnocked,
			leftThreePinKnocked: leftThreePinKnocked,
			rightTwoPinKnocked: rightTwoPinKnocked,
			rightThreePinKnocked: rightThreePinKnocked,
			headPinKnocked: headPinKnocked
		)
	}
}
