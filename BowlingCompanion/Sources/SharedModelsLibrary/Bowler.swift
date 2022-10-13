import Foundation

public struct Bowler: Equatable, Identifiable {
	public let id: UUID
	public let name: String

	public init(id: UUID, name: String) {
		self.id = id
		self.name = name
	}
}
