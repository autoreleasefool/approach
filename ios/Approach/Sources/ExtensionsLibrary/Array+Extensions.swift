import Foundation

extension Set {
	public mutating func toggle(_ element: Element) {
		if contains(element) {
			remove(element)
		} else {
			insert(element)
		}
	}
}
