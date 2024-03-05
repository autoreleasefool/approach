import Dependencies
import ModelsLibrary

public struct LocationsRepository: Sendable {
	public var insertOrUpdate: @Sendable (Location.Create) async throws -> Void

	public init(
		insertOrUpdate: @escaping @Sendable (Location.Create) async throws -> Void
	) {
		self.insertOrUpdate = insertOrUpdate
	}
}

extension LocationsRepository: TestDependencyKey {
	public static var testValue = Self(
		insertOrUpdate: { _ in unimplemented("\(Self.self).insertOrUpdate") }
	)
}
