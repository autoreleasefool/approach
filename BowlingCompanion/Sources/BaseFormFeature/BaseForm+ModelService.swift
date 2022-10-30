import Dependencies

public struct FormModelService: Sendable {
	public var create: @Sendable (any BaseFormModel) async throws -> Void
	public var update: @Sendable (any BaseFormModel) async throws -> Void
	public var delete: @Sendable (any BaseFormModel) async throws -> Void

	public init(
		create: @escaping @Sendable (any BaseFormModel) async throws -> Void,
		update: @escaping @Sendable (any BaseFormModel) async throws -> Void,
		delete: @escaping @Sendable (any BaseFormModel) async throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}

	public init<Model: BaseFormModel>(
		create: @escaping @Sendable (Model) async throws -> Void,
		update: @escaping @Sendable (Model) async throws -> Void,
		delete: @escaping @Sendable (Model) async throws -> Void
	) {
		self.create = { model in
			guard let mapped = model as? Model else { return }
			try await create(mapped)
		}
		self.update = { model in
			guard let mapped = model as? Model else { return }
			try await update(mapped)
		}
		self.delete = { model in
			guard let mapped = model as? Model else { return }
			try await delete(mapped)
		}
	}
}

extension FormModelService: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		update: { _ in fatalError("\(Self.self).update") },
		delete: { _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var formModelService: FormModelService {
		get { self[FormModelService.self] }
		set { self[FormModelService.self] = newValue }
	}
}
