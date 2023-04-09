import ModelsLibrary

extension Bowler {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}
