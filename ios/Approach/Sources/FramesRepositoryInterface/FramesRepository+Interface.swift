import Dependencies
import ModelsLibrary

public struct FramesRepository: Sendable {
	public var load: @Sendable (Game.ID) async throws -> [Frame.Summary]?
	public var edit: @Sendable (Game.ID) async throws -> [Frame.Edit]?
	public var update: @Sendable (Frame.Edit) async throws -> Void

	public init(
		load: @escaping @Sendable (Game.ID) async throws -> [Frame.Summary]?,
		edit: @escaping @Sendable (Game.ID) async throws -> [Frame.Edit]?,
		update: @escaping @Sendable (Frame.Edit) async throws -> Void
	) {
		self.load = load
		self.edit = edit
		self.update = update
	}

	public func frames(forGame: Game.ID) async throws -> [Frame.Edit]? {
		try await self.edit(forGame)
	}
}

extension FramesRepository: TestDependencyKey {
	public static var testValue = Self(
		load: { _ in unimplemented("\(Self.self).load") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		update: { _ in unimplemented("\(Self.self).update") }
	)
}

extension DependencyValues {
	public var frames: FramesRepository {
		get { self[FramesRepository.self] }
		set { self[FramesRepository.self] = newValue }
	}
}
