import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct GearDataProvider: Sendable {
	public var observeGear: @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>

	public init(
		observeGear: @escaping @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>
	) {
		self.observeGear = observeGear
	}
}

extension GearDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeGear: { _ in unimplemented("\(Self.self).observeGear") }
	)
}

extension DependencyValues {
	public var gearDataProvider: GearDataProvider {
		get { self[GearDataProvider.self] }
		set { self[GearDataProvider.self] = newValue }
	}
}
