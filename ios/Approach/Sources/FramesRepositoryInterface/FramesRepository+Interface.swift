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
	public static var testValue = Self(
		observe: { _ in unimplemented("\(Self.self).observe") },
		observeRolls: { _ in unimplemented("\(Self.self).observeRolls") },
		update: { _ in unimplemented("\(Self.self).update") }
	)
}
