import Dependencies
import ModelsLibrary

public struct LanesRepository: Sendable {
	public var edit: @Sendable (Alley.ID) async throws -> [Lane.Edit]
	public var create: @Sendable ([Lane.Create]) async throws -> Void
	public var update: @Sendable ([Lane.Edit]) async throws -> Void
	public var delete: @Sendable ([Lane.ID]) async throws -> Void

	public init(
		edit: @escaping @Sendable (Alley.ID) async throws -> [Lane.Edit],
		create: @escaping @Sendable ([Lane.Create]) async throws -> Void,
		update: @escaping @Sendable ([Lane.Edit]) async throws -> Void,
		delete: @escaping @Sendable ([Lane.ID]) async throws -> Void
	) {
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension LanesRepository: TestDependencyKey {
	public static var testValue = Self(
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var lanes: LanesRepository {
		get { self[LanesRepository.self] }
		set { self[LanesRepository.self] = newValue }
	}
}
