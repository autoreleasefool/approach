import Foundation

extension Array where Element: Equatable {
	public mutating func toggle(_ element: Element) {
		guard let index = self.firstIndex(of: element) else {
			self.append(element)
			return
		}

		self.remove(at: index)
	}
}
