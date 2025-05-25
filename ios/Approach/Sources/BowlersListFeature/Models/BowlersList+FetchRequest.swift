import BowlersRepositoryInterface
import ModelsLibrary
import Sharing

extension Bowler.List {
	public typealias FetchRequest = Bowler.Ordering
}

extension SharedReaderKey where Self == AppStorageKey<Bowler.List.FetchRequest>.Default {
	static var fetchRequest: Self {
		Self[
			.appStorage("bowlersList_fetchRequest"),
			default: .byRecentlyUsed
		]
	}
}
