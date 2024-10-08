import ConcurrencyExtras
import Dependencies
import ModelsLibrary
import XCTestDynamicOverlay

public struct AddressLookupService: Sendable {
	public var beginSearch: @Sendable (AnyHashableSendable) async -> AsyncThrowingStream<[AddressLookupResult], Error>
	public var updateSearchQuery: @Sendable (AnyHashableSendable, String) async -> Void
	public var lookUpAddress: @Sendable (AddressLookupResult) async throws -> Location.Summary?

	public init(
		beginSearch: @escaping @Sendable (AnyHashableSendable) async -> AsyncThrowingStream<[AddressLookupResult], Error>,
		updateSearchQuery: @escaping @Sendable (AnyHashableSendable, String) async -> Void,
		lookUpAddress: @escaping @Sendable (AddressLookupResult) async throws -> Location.Summary?
	) {
		self.beginSearch = beginSearch
		self.updateSearchQuery = updateSearchQuery
		self.lookUpAddress = lookUpAddress
	}
}

extension AddressLookupService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			beginSearch: { _ in unimplemented("\(Self.self).beginSearch", placeholder: .never) },
			updateSearchQuery: { _, _ in unimplemented("\(Self.self).updateSearchQuery") },
			lookUpAddress: { _ in unimplemented("\(Self.self).lookUpAddress", placeholder: nil) }
		)
	}
}
