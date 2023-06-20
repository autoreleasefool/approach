import StatisticsLibrary

extension Statistics {
	public struct ListEntry: Equatable {
		let title: String
		let value: String

		public init(title: String, value: String) {
			self.title = title
			self.value = value
		}
	}
}
