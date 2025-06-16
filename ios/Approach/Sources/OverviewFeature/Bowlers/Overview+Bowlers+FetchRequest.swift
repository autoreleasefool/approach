import BowlersRepositoryInterface
import ModelsLibrary
import Sharing
import StringsLibrary

extension Bowler.List {
	public typealias FetchRequest = Bowler.Ordering
}

extension Bowler.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: Strings.Ordering.mostRecentlyUsed
		case .byName: Strings.Ordering.alphabetical
		}
	}
}

extension SharedReaderKey where Self == AppStorageKey<Bowler.List.FetchRequest>.Default {
	static var bowlersFetchRequest: Self {
		Self[
			.appStorage("bowlersList_fetchRequest"),
			default: .byRecentlyUsed
		]
	}
}
