import Foundation

public struct Bowler: Sendable, Identifiable, Hashable, Codable {
	public let id: UUID
	public let name: String
	public let createdAt: Date
	public let lastModifiedAt: Date

	public init(
		id: UUID,
		name: String,
		createdAt: Date,
		lastModifiedAt: Date
	) {
		self.id = id
		self.name = name
		self.createdAt = createdAt
		self.lastModifiedAt = lastModifiedAt
	}
}
