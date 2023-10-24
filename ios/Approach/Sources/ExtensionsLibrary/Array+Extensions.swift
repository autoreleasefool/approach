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

extension Collection where Element: Hashable {
	public func findDuplicates() -> Set<Element> {
		var allElements: Set<Element> = []
		var duplicateElements: Set<Element> = []
		for element in self where allElements.insert(element).inserted {
			duplicateElements.insert(element)
		}

		return duplicateElements
	}
}
