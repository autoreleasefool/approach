import Dependencies
import ModelsLibrary

public struct LanesRepository: Sendable {
	public var list: @Sendable (Alley.ID?) -> AsyncThrowingStream<[Lane.Summary], Error>
	public var edit: @Sendable (Alley.ID) async throws -> [Lane.Edit]
	public var create: @Sendable ([Lane.Create]) async throws -> Void
	public var update: @Sendable ([Lane.Edit]) async throws -> Void
	public var delete: @Sendable ([Lane.ID]) async throws -> Void

	public init(
		list: @escaping @Sendable (Alley.ID?) -> AsyncThrowingStream<[Lane.Summary], Error>,
		edit: @escaping @Sendable (Alley.ID) async throws -> [Lane.Edit],
		create: @escaping @Sendable ([Lane.Create]) async throws -> Void,
		update: @escaping @Sendable ([Lane.Edit]) async throws -> Void,
		delete: @escaping @Sendable ([Lane.ID]) async throws -> Void
	) {
		self.list = list
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension LanesRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			list: { _ in unimplemented("\(Self.self).list", placeholder: .never) },
			edit: { _ in unimplemented("\(Self.self).edit", placeholder: []) },
			create: { _ in unimplemented("\(Self.self).create") },
			update: { _ in unimplemented("\(Self.self).update") },
			delete: { _ in unimplemented("\(Self.self).delete") }
		)
	}
}
