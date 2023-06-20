import IdentifiedCollections
import StatisticsLibrary

extension Statistics {
	public struct ListEntry: Identifiable, Equatable {
		public let title: String
		public let value: String

		public var id: String { title }

		public init(title: String, value: String) {
			self.title = title
			self.value = value
		}
	}
}

extension Statistics {
	public struct ListEntryGroup: Identifiable, Equatable {
		public let category: StatisticCategory
		public let entries: IdentifiedArrayOf<Statistics.ListEntry>

		public var id: StatisticCategory { category }

		public init(category: StatisticCategory, entries: IdentifiedArrayOf<Statistics.ListEntry>) {
			self.category = category
			self.entries = entries
		}
	}
}
