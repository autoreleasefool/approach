import ModelsLibrary

extension Bowler {
	public struct Editable: Equatable {
		public let id: Bowler.ID
		public var name: String
		public var status: Bowler.Status

		public init(id: Bowler.ID, name: String, status: Bowler.Status) {
			self.id = id
			self.name = name
			self.status = status
		}
	}
}
