public struct Breadcrumb {
	public let message: String
	public let category: Category

	public init(_ message: String, category: Category) {
		self.message = message
		self.category = category
	}

	public static func navigationBreadcrumb(_ screen: Any.Type) -> Breadcrumb {
		.init(String(describing: screen), category: .navigation)
	}
}

extension Breadcrumb {
	public enum Category: String {
		case navigation
	}
}
