import Dependencies
import ModelsLibrary
import ScoreKeeperLibrary

public struct FramesRepository: Sendable {
	public var observe: @Sendable (Game.ID) -> AsyncThrowingStream<[Frame.Edit], Error>
	public var observeRolls: @Sendable (Game.ID) -> AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>
	public var update: @Sendable (Frame.Edit) async throws -> Void

	public init(
		observe: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<[Frame.Edit], Error>,
		observeRolls: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<[[ScoreKeeper.Roll]], Error>,
		update: @escaping @Sendable (Frame.Edit) async throws -> Void
	) {
		self.observe = observe
		self.observeRolls = observeRolls
		self.update = update
	}
}

extension FramesRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			observe: { _ in unimplemented("\(Self.self).observe", placeholder: .never) },
			observeRolls: { _ in unimplemented("\(Self.self).observeRolls", placeholder: .never) },
			update: { _ in unimplemented("\(Self.self).update") }
		)
	}
}
