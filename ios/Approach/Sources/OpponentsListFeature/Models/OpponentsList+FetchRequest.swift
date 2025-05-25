import BowlersRepositoryInterface
import ModelsLibrary
import Sharing

extension Bowler.Opponent {
	public typealias FetchRequest = Bowler.Ordering
}

extension SharedReaderKey where Self == AppStorageKey<Bowler.Opponent.FetchRequest>.Default {
	static var fetchRequest: Self {
		Self[
			.appStorage("opponentsList_fetchRequest"),
			default: .byRecentlyUsed
		]
	}
}
