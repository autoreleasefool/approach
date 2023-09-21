import Foundation

extension Set {
	public mutating func toggle(_ element: Element) {
		if contains(element) {
			remove(element)
		} else {
			insert(element)
		}
	}

	public mutating func toggle(_ element: Element, toContain: Bool) {
		if toContain {
			insert(element)
		} else {
			remove(element)
		}
	}
}
