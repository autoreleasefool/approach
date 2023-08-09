public struct Product: Equatable {
	public let name: String
	public let introduced: String

	public static let proSubscription = Product(name: "approach-pro", introduced: "2023-08-08")

	public func buildVariant(withName: String) -> ProductVariant {
		.init(product: self, name: withName)
	}
}

public struct ProductVariant: Equatable {
	public let product: Product
	public let name: String
}
