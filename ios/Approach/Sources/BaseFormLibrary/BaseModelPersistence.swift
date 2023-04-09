import Dependencies

public struct BaseModelPersistence: Sendable {
	public var save: @Sendable (Any) async throws -> Void
	public var delete: @Sendable (Any) async throws -> Void

	public init(
		save: @escaping @Sendable (Any) async throws -> Void,
		delete: @escaping @Sendable (Any) async throws-> Void
	) {
		self.save = save
		self.delete = delete
	}

	public init<Model>(
		save: @escaping @Sendable (Model) async throws -> Void,
		delete: @escaping @Sendable (Model) async throws -> Void
	) {
		self.init(
			save: { model in
				// FIXME: assert model casts correctly
				guard let mapped = model as? Model else { return }
				try await save(mapped)
			},
			delete: { model in
				// FIXME: can pass ID instead of entire model
				guard let mapped = model as? Model else { return }
				try await delete(mapped)
			}
		)
	}
}

extension BaseModelPersistence: TestDependencyKey {
	public static var testValue = Self(
		save: { _ in unimplemented("\(Self.self).save") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var modelPersistence: BaseModelPersistence {
		get { self[BaseModelPersistence.self] }
		set { self[BaseModelPersistence.self] = newValue }
	}
}
