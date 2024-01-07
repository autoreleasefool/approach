import IdentifiedCollections
import StatisticsLibrary
import UIKit

extension Statistics {
	public struct ListEntry: Identifiable, Equatable {
		public let title: String
		public let description: String?
		public let value: String
		public let valueDescription: String?
		public let highlightAsNew: Bool

		public var id: String { title }

		public init(title: String, description: String?, value: String, valueDescription: String?, highlightAsNew: Bool) {
			self.title = title
			self.description = description
			self.value = value
			self.valueDescription = valueDescription
			self.highlightAsNew = highlightAsNew
		}
	}
}

extension Statistics {
	public struct ListEntryGroup: Identifiable, Equatable {
		public let title: String
		public let description: String?
		public let images: [Image]?
		public let entries: IdentifiedArrayOf<Statistics.ListEntry>

		public var id: String { title }

		public init(
			title: String,
			description: String?,
			images: [Image]?,
			entries: IdentifiedArrayOf<Statistics.ListEntry>
		) {
			self.title = title
			self.description = description
			self.images = images
			self.entries = entries
		}
	}
}

extension Statistics.ListEntryGroup {
	public struct Image: Identifiable, Equatable {
		public let id: Int
		public let image: UIImage

		public init(id: Int, image: UIImage) {
			self.id = id
			self.image = image
		}
	}
}
