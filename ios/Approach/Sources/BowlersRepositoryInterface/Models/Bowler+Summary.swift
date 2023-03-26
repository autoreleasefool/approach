import ModelsLibrary

extension Bowler {
	public struct Summary: Identifiable, Equatable {
		public let id: Bowler.ID
		public let name: String

		public init(id: Bowler.ID, name: String) {
			self.id = id
			self.name = name
		}
	}
}
