import Dependencies

public struct RecordPersistence: Sendable {
	public var create: (@Sendable (Any) async throws -> Void)?
	public var update: (@Sendable (Any) async throws -> Void)?
	public var delete: (@Sendable (Any) async throws -> Void)?

	public init(
		create: (@Sendable (Any) async throws -> Void)?,
		update: (@Sendable (Any) async throws -> Void)?,
		delete: (@Sendable (Any) async throws-> Void)?
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}

	public init<New: CreateableRecord, Existing: EditableRecord>(
		create: @escaping @Sendable (New) async throws -> Void,
		update: @escaping @Sendable (Existing) async throws -> Void,
		delete: @escaping @Sendable (New.ID) async throws -> Void
	) where New.ID == Existing.ID {
		self.init(
			create: { record in
				// FIXME: assert model casts correctly
				guard let mapped = record as? New else { return }
				try await create(mapped)
			},
			update: { record in
				// FIXME: assert model casts correctly
				guard let mapped = record as? Existing else { return }
				try await update(mapped)
			},
			delete: { id in
				// FIXME: assert model casts correctly
				guard let mapped = id as? New.ID else { return }
				try await delete(mapped)
			}
		)
	}
}

extension RecordPersistence: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var records: RecordPersistence {
		get { self[RecordPersistence.self] }
		set { self[RecordPersistence.self] = newValue }
	}
}
