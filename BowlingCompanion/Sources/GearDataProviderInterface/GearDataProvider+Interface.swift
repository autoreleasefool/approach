import Dependencies
import SharedModelsLibrary

public struct GearDataProvider: Sendable {
	public var fetchGear: @Sendable (Gear.FetchRequest) async throws -> [Gear]

	public init(
		fetchGear: @escaping @Sendable (Gear.FetchRequest) async throws -> [Gear]
	) {
		self.fetchGear = fetchGear
	}
}

extension GearDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchGear: { _ in fatalError("\(Self.self).fetchGears") }
	)
}

extension DependencyValues {
	public var gearDataProvider: GearDataProvider {
		get { self[GearDataProvider.self] }
		set { self[GearDataProvider.self] = newValue }
	}
}
