import ModelsLibrary

public protocol EarnableBadge: Sendable {
	static var title: String { get }
	static func consume(from: inout [ConsumableBadgeEvent]) -> [Self]

	init()
}

public protocol ConsumableBadgeEvent: Sendable {
	static var title: String { get }
}

// Earnable

public enum EarnableBadges {}

extension EarnableBadges {
	public static let allCases: [EarnableBadge.Type] = [
		Iconista.self,
	]
}
