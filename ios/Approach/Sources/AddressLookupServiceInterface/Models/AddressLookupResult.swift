import Foundation
import MapKit

public struct AddressLookupResult: Identifiable {
	public let id: UUID
	public let completion: MKLocalSearchCompletion

	public init(id: UUID = UUID(), completion: MKLocalSearchCompletion) {
		self.id = id
		self.completion = completion
	}
}
