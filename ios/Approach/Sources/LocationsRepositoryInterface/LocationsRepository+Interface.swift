import Dependencies
import ModelsLibrary

public struct LocationsRepository: Sendable {
	public var create: @Sendable (Location.Create) async throws -> Void
	public var update: @Sendable (Location.Edit) async throws -> Void

	public init(
		create: @escaping @Sendable (Location.Create) async throws -> Void,
		update: @escaping @Sendable (Location.Edit) async throws -> Void
	) {
		self.create = create
		self.update = update
	}
}

extension LocationsRepository: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") }
	)
}

extension DependencyValues {
	public var locations: LocationsRepository {
		get { self[LocationsRepository.self] }
		set { self[LocationsRepository.self] = newValue }
	}
}
