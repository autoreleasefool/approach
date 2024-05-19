import EquatablePackageLibrary
import Foundation
import MapKit

public struct AddressLookupResult: Identifiable, Equatable {
	public let id: UUID
	public let completion: AlwaysEqual<MKLocalSearchCompletion>

	public init(id: UUID, completion: MKLocalSearchCompletion) {
		self.id = id
		self.completion = .init(completion)
	}
}
