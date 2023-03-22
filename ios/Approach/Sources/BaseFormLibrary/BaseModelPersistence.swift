import Dependencies

public struct BaseModelPersistence: Sendable {
	public var create: @Sendable (Any) async throws -> Void
	public var update: @Sendable (Any) async throws -> Void
	public var delete: @Sendable (Any) async throws -> Void

	public init(
		create: @escaping @Sendable (Any) async throws -> Void,
		update: @escaping @Sendable (Any) async throws -> Void,
		delete: @escaping @Sendable (Any) async throws-> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}

	public init<Model>(
		create: @escaping @Sendable (Model) async throws -> Void,
		update: @escaping @Sendable (Model) async throws -> Void,
		delete: @escaping @Sendable (Model) async throws -> Void
	) {
		self.init(
			create: { model in
				guard let mapped = model as? Model else { return }
				try await create(mapped)
			},
			update: { model in
				guard let mapped = model as? Model else { return }
				try await update(mapped)
			},
			delete: { model in
				guard let mapped = model as? Model else { return }
				try await delete(mapped)
			}
		)
	}
}

extension BaseModelPersistence: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var modelPersistence: BaseModelPersistence {
		get { self[BaseModelPersistence.self] }
		set { self[BaseModelPersistence.self] = newValue }
	}
}
