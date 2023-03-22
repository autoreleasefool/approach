import Dependencies
import XCTestDynamicOverlay

public struct AddressLookupService: Sendable {
	public var beginSearch: @Sendable (Any.Type) async -> AsyncThrowingStream<[AddressLookupResult], Error>
	public var updateSearchQuery: @Sendable (Any.Type, String) async -> Void
	public var finishSearch: @Sendable (Any.Type) async -> Void
	public var lookUpAddress: @Sendable (AddressLookupResult) async throws -> AddressLocation?

	public init(
		beginSearch: @escaping @Sendable (Any.Type) async -> AsyncThrowingStream<[AddressLookupResult], Error>,
		updateSearchQuery: @escaping @Sendable (Any.Type, String) async -> Void,
		finishSearch: @escaping @Sendable (Any.Type) async -> Void,
		lookUpAddress: @escaping @Sendable (AddressLookupResult) async throws -> AddressLocation?
	) {
		self.beginSearch = beginSearch
		self.updateSearchQuery = updateSearchQuery
		self.finishSearch = finishSearch
		self.lookUpAddress = lookUpAddress
	}
}

extension AddressLookupService: TestDependencyKey {
	public static var testValue = Self(
		beginSearch: { _ in unimplemented("\(Self.self).beginSearch") },
		updateSearchQuery: { _, _ in unimplemented("\(Self.self).updateSearchQuery") },
		finishSearch: { _ in unimplemented("\(Self.self).finishSearch") },
		lookUpAddress: { _ in unimplemented("\(Self.self).lookUpAddress") }
	)
}

extension DependencyValues {
	public var addressLookupService: AddressLookupService {
		get { self[AddressLookupService.self] }
		set { self[AddressLookupService.self] = newValue }
	}
}
