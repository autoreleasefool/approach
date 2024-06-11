import IdentifiedCollections

extension Sequence where Element: Identifiable {
	public func eraseToIdentifiedArray() -> IdentifiedArrayOf<Element> {
		IdentifiedArray(uniqueElements: self)
	}
}
