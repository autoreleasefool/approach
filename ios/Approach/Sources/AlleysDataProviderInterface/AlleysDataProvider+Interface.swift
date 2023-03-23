import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct AlleysDataProvider: Sendable {
	public var observeAlley: @Sendable (Alley.SingleFetchRequest) -> AsyncThrowingStream<Alley?, Error>
	public var observeAlleys: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public init(
		observeAlley: @escaping @Sendable (Alley.SingleFetchRequest) -> AsyncThrowingStream<Alley?, Error>,
		observeAlleys: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>
	) {
		self.observeAlley = observeAlley
		self.observeAlleys = observeAlleys
	}
}

extension AlleysDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeAlley: { _ in unimplemented("\(Self.self).observeAlley") },
		observeAlleys: { _ in unimplemented("\(Self.self).observeAlleys") }
	)
}

extension DependencyValues {
	public var alleysDataProvider: AlleysDataProvider {
		get { self[AlleysDataProvider.self] }
		set { self[AlleysDataProvider.self] = newValue }
	}
}
