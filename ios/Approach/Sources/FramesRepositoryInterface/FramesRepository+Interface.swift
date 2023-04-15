import Dependencies
import ModelsLibrary

public struct FramesRepository: Sendable {
	public var edit: @Sendable (Game.ID) async throws -> [Frame.Edit]?
	public var update: @Sendable (Frame.Edit) async throws -> Void

	public init(
		edit: @escaping @Sendable (Game.ID) async throws -> [Frame.Edit]?,
		update: @escaping @Sendable (Frame.Edit) async throws -> Void
	) {
		self.edit = edit
		self.update = update
	}

	public func frames(forGame: Game.ID) async throws -> [Frame.Edit]? {
		try await self.edit(forGame)
	}
}

extension FramesRepository: TestDependencyKey {
	public static var testValue = Self(
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
