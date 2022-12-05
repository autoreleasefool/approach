import Foundation

public struct Lane: Sendable, Identifiable, Hashable, Codable {
	public let id: UUID
	public let label: String
	public let isAgainstWall: Bool
	public let alley: Alley.ID

	public init(id: UUID, label: String, isAgainstWall: Bool, alley: Alley.ID) {
		self.id = id
		self.label = label
		self.isAgainstWall = isAgainstWall
		self.alley = alley
	}
}
