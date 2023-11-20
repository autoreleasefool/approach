public struct Breadcrumb {
	public let message: String
	public let category: Category

	public init(_ message: String, category: Category = .navigation) {
		self.message = message
		self.category = category
	}
}

extension Breadcrumb {
	public enum Category: String {
		case navigation
	}
}
