import Dependencies

public protocol BaseDataModel {}

public struct BaseDataProvider: Sendable {
	public var create: @Sendable (any BaseDataModel) async throws -> Void
	public var update: @Sendable (any BaseDataModel) async throws -> Void
	public var delete: @Sendable (any BaseDataModel) async throws -> Void

	public init(
		create: @escaping @Sendable (any BaseDataModel) async throws -> Void,
		update: @escaping @Sendable (any BaseDataModel) async throws -> Void,
		delete: @escaping @Sendable (any BaseDataModel) async throws-> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}

	public init<Model: BaseDataModel>(
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

extension BaseDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		update: { _ in fatalError("\(Self.self).update") },
		delete: { _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var baseDataProvider: BaseDataProvider {
		get { self[BaseDataProvider.self] }
		set { self[BaseDataProvider.self] = newValue }
	}
}
