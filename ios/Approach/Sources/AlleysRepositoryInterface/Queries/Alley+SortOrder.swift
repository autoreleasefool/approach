import ModelsLibrary

extension Alley {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}
