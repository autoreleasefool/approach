import Dependencies
import ModelsLibrary

public struct LocationsRepository: Sendable {
	public var create: @Sendable (Location.Create) async throws -> Void
	public var edit: @Sendable (Location.Edit) async throws -> Void

	public init(
		create: @escaping @Sendable (Location.Create) async throws -> Void,
		edit: @escaping @Sendable (Location.Edit) async throws -> Void
	) {
		self.create = create
		self.edit = edit
	}
}

extension LocationsRepository: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in unimplemented("\(Self.self).create") },
		edit: { _ in unimplemented("\(Self.self).edit") }
	)
}

extension DependencyValues {
	public var locations: LocationsRepository {
		get { self[LocationsRepository.self] }
		set { self[LocationsRepository.self] = newValue }
	}
}
