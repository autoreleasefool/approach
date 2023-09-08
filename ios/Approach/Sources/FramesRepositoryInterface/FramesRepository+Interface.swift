import Dependencies
import ModelsLibrary

public struct FramesRepository: Sendable {
	public var observe: @Sendable (Game.ID) -> AsyncThrowingStream<[Frame.Edit], Error>
	public var update: @Sendable (Frame.Edit) async throws -> Void

	public init(
		observe: @escaping @Sendable (Game.ID) -> AsyncThrowingStream<[Frame.Edit], Error>,
		update: @escaping @Sendable (Frame.Edit) async throws -> Void
	) {
		self.observe = observe
		self.update = update
	}
}

extension FramesRepository: TestDependencyKey {
	public static var testValue = Self(
		observe: { _ in unimplemented("\(Self.self).observe") },
		update: { _ in unimplemented("\(Self.self).update") }
	)
}

extension DependencyValues {
	public var frames: FramesRepository {
		get { self[FramesRepository.self] }
		set { self[FramesRepository.self] = newValue }
	}
}
