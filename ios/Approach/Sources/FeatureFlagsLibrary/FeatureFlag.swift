public struct FeatureFlag: Identifiable, Hashable {
	public let name: String
	public let introduced: String
	public let stage: RolloutStage
	public let isOverridable: Bool

	public var id: String { name }

	init(name: String, introduced: String, stage: RolloutStage, isOverridable: Bool = true) {
		self.name = name
		self.introduced = introduced
		self.stage = stage
		self.isOverridable = isOverridable
	}
}

extension FeatureFlag {
	public enum RolloutStage: Comparable, CaseIterable {
		case disabled
		case development
		case test
		case release

		public static func < (lhs: Self, rhs: Self) -> Bool {
			switch (lhs, rhs) {
			case (.disabled, .disabled): return false
			case (.disabled, _): return true
			case (.development, .disabled), (.development, .development): return false
			case (.development, _): return true
			case (.test, .disabled), (.test, .development), (.test, .test): return false
			case (.test, _): return true
			case (.release, _): return false
			}
		}
	}
}
