extension CaseIterable where Self: Equatable {
	private var allCases: AllCases { Self.allCases }
	public var next: Self {
		let index = allCases.index(after: allCases.firstIndex(of: self)!)
		guard index != allCases.endIndex else { return allCases.first! }
		return allCases[index]
	}

	public mutating func toNext() {
		self = next
	}
}
