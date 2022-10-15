import Foundation

public struct Series: Sendable, Identifiable, Hashable {
	public let id: UUID
	public let date: Date

	public init(id: UUID, date: Date) {
		self.id = id
		self.date = date
	}
}
