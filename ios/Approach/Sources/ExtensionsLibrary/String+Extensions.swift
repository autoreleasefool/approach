extension String {
	public var words: [String] {
		components(separatedBy: .alphanumerics.inverted)
			.filter { !$0.isEmpty }
	}

	public var initials: String {
		let words = self.words
		if words.count > 1 {
			return words.prefix(2)
				.map { $0.first?.description ?? "" }
				.joined()
		} else {
			return String(prefix(2))
		}
	}
}
