import Dependencies
import GRDB
import SharedModelsLibrary

public struct FramesPersistenceService: Sendable {
	public var create: @Sendable (Frame, Database) throws -> Void
	public var update: @Sendable (Frame, Database) throws -> Void
	public var delete: @Sendable (Frame, Database) throws -> Void

	public init(
		create: @escaping @Sendable (Frame, Database) throws -> Void,
		update: @escaping @Sendable (Frame, Database) throws -> Void,
		delete: @escaping @Sendable (Frame, Database) throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension FramesPersistenceService: TestDependencyKey {
	public static var testValue = Self(
		create: { _, _ in fatalError("\(Self.self).create") },
		update: { _, _ in fatalError("\(Self.self).update") },
		delete: { _, _ in fatalError("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var framesPersistenceService: FramesPersistenceService {
		get { self[FramesPersistenceService.self] }
		set { self[FramesPersistenceService.self] = newValue }
	}
}
