import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct GearDataProvider: Sendable {
	public var fetchGear: @Sendable (Gear.FetchRequest) async throws -> [Gear]
	public var observeGear: @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>

	public init(
		fetchGear: @escaping @Sendable (Gear.FetchRequest) async throws -> [Gear],
		observeGear: @escaping @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>
	) {
		self.fetchGear = fetchGear
		self.observeGear = observeGear
	}
}

extension GearDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchGear: { _ in fatalError("\(Self.self).fetchGeas") },
		observeGear: { _ in fatalError("\(Self.self).observeGear") }
	)
}

extension DependencyValues {
	public var gearDataProvider: GearDataProvider {
		get { self[GearDataProvider.self] }
		set { self[GearDataProvider.self] = newValue }
	}
}
