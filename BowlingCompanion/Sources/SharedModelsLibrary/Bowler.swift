import Foundation

public struct Bowler: Sendable, Identifiable, Hashable, Codable {
	public let id: UUID
	public let name: String
	public let avatar: Avatar

	public init(
		id: UUID,
		name: String,
		avatar: Avatar
	) {
		self.id = id
		self.name = name
		self.avatar = avatar
	}
}
