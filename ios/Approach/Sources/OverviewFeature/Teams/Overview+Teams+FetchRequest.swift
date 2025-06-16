import ModelsLibrary
import Sharing
import StringsLibrary
import TeamsRepositoryInterface

extension Team.List {
	public typealias FetchRequest = Team.Ordering
}

extension Team.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: Strings.Ordering.mostRecentlyUsed
		case .byName: Strings.Ordering.alphabetical
		}
	}
}

extension SharedReaderKey where Self == AppStorageKey<Team.List.FetchRequest>.Default {
	static var teamsFetchRequest: Self {
		Self[
			.appStorage("overview_fetchRequest"),
			default: .byRecentlyUsed
		]
	}
}
