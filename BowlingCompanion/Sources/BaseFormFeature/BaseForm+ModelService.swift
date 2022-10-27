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
