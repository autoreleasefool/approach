import Dependencies
import SharedModelsLibrary

public struct GearDataProvider: Sendable {
	public var fetchGear: @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>

	public init(
		fetchGear: @escaping @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>
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
