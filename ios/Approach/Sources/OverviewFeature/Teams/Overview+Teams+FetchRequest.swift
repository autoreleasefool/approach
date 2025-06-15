import ModelsLibrary
import Sharing
import TeamsRepositoryInterface

extension Team.List {
	public typealias FetchRequest = Team.Ordering
}

extension SharedReaderKey where Self == AppStorageKey<Team.List.FetchRequest>.Default {
	static var teamsFetchRequest: Self {
		Self[
			.appStorage("overview_fetchRequest"),
			default: .byRecentlyUsed
		]
	}
}
