import Dependencies
import ModelsLibrary
import XCTestDynamicOverlay

public struct AddressLookupService: Sendable {
	public var beginSearch: @Sendable (AnyHashable) async -> AsyncThrowingStream<[AddressLookupResult], Error>
	public var updateSearchQuery: @Sendable (AnyHashable, String) async -> Void
	public var lookUpAddress: @Sendable (AddressLookupResult) async throws -> Location.Summary?

	public init(
		beginSearch: @escaping @Sendable (AnyHashable) async -> AsyncThrowingStream<[AddressLookupResult], Error>,
		updateSearchQuery: @escaping @Sendable (AnyHashable, String) async -> Void,
		lookUpAddress: @escaping @Sendable (AddressLookupResult) async throws -> Location.Summary?
	) {
		self.beginSearch = beginSearch
		self.updateSearchQuery = updateSearchQuery
		self.lookUpAddress = lookUpAddress
	}
}

extension AddressLookupService: TestDependencyKey {
	public static var testValue = Self(
		beginSearch: { _ in unimplemented("\(Self.self).beginSearch") },
		updateSearchQuery: { _, _ in unimplemented("\(Self.self).updateSearchQuery") },
		lookUpAddress: { _ in unimplemented("\(Self.self).lookUpAddress") }
	)
}

extension DependencyValues {
	public var addressLookup: AddressLookupService {
		get { self[AddressLookupService.self] }
		set { self[AddressLookupService.self] = newValue }
	}
}
