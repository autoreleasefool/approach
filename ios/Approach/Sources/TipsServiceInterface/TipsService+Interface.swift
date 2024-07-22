import Dependencies
import TipsLibrary

public struct TipsService: Sendable {
	public var shouldShowTip: @Sendable (Tip) -> Bool
	public var hideTip: @Sendable (Tip) async -> Void

	public init(
		shouldShowTip: @escaping @Sendable (Tip) -> Bool,
		hideTip: @escaping @Sendable (Tip) async -> Void
	) {
		self.shouldShowTip = shouldShowTip
		self.hideTip = hideTip
	}

	public func shouldShow(tipFor: Tip) -> Bool {
		self.shouldShowTip(tipFor)
	}

	public func hide(tipFor: Tip) async {
		await self.hideTip(tipFor)
	}
}

extension TipsService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			shouldShowTip: { _ in unimplemented("\(Self.self).shouldShowTip") },
			hideTip: { _ in unimplemented("\(Self.self).hideTip") }
		)
	}
}
