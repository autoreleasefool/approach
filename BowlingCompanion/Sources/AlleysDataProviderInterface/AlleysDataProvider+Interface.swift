import Dependencies
import SharedModelsLibrary

public struct AlleysDataProvider: Sendable {
	public var create: @Sendable (Alley) async throws -> Void
	public var update: @Sendable (Alley) async throws -> Void
	public var delete: @Sendable (Alley) async throws -> Void
	public var fetchAll: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public init(
		create: @escaping @Sendable (Alley) async throws -> Void,
		update: @escaping @Sendable (Alley) async throws -> Void,
		delete: @escaping @Sendable (Alley) async throws -> Void,
		fetchAll: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>
	) {
		self.create = create
		self.update = update
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

extension AlleysDataProvider: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in fatalError("\(Self.self).create") },
		update: { _ in fatalError("\(Self.self).update") },
		delete: { _ in fatalError("\(Self.self).delete") },
		fetchAll: { _ in fatalError("\(Self.self).fetchAll") }
	)
}

extension DependencyValues {
	public var alleysDataProvider: AlleysDataProvider {
		get { self[AlleysDataProvider.self] }
		set { self[AlleysDataProvider.self] = newValue }
	}
}
