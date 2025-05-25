import GearRepositoryInterface
import ModelsLibrary
import Sharing

extension Gear.Summary {
	public struct FetchRequest: Equatable, Sendable {
		public var ordering: Gear.Ordering
		public var kind: Gear.Kind?

		public init(kind: Gear.Kind?, ordering: Gear.Ordering) {
			self.ordering = ordering
			self.kind = kind
		}
	}
}

extension SharedReaderKey where Self == AppStorageKey<Gear.Ordering>.Default {
	static var ordering: Self {
		Self[
			.appStorage("gearList_ordering"),
			default: .byRecentlyUsed
		]
	}
}
