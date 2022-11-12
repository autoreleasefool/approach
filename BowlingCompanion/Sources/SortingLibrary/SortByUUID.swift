import Foundation

public extension Array where Element: Identifiable, Element.ID == UUID {
	func sortBy(ids: [UUID]) -> Array<Element> {
		let idIndices: [UUID: Int] = ids.enumerated().reduce(into: [:], { indices, element in
			indices[element.element] = element.offset
		})

		return sorted { first, second in
			let firstIndex = idIndices[first.id]
			let secondIndex = idIndices[second.id]

			if let firstIndex, let secondIndex {
				return firstIndex < secondIndex
			} else if firstIndex != nil {
				return true
			} else {
				return false
			}
		}
	}
}
