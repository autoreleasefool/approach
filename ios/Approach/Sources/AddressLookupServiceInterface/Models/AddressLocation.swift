import Foundation

public struct AddressLocation: Identifiable {
	public let id: UUID
	public let title: String
	public let subtitle: String
	public let latitude: Double
	public let longitude: Double

	public init(id: UUID, title: String, subtitle: String, latitude: Double, longitude: Double) {
		self.id = id
		self.title = title
		self.subtitle = subtitle
		self.latitude = latitude
		self.longitude = longitude
	}
}
