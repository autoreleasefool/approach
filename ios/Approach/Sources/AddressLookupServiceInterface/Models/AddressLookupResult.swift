import EquatablePackageLibrary
import Foundation
import MapKit

public struct AddressLookupResult: Identifiable, Equatable, Sendable {
	public let id: UUID
	public let title: String
	public let subtitle: String

	public init(id: UUID, completion: MKLocalSearchCompletion) {
		self.id = id
		self.title = completion.title
		self.subtitle = completion.subtitle
	}
}
