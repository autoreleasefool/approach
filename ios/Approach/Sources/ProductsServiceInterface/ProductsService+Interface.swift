import Dependencies
import ProductsLibrary

public struct ProductsService: Sendable {
	public var initialize: @Sendable () -> Void
	public var peekIsAvailable: @Sendable (Product) -> Bool
	public var isAvailable: @Sendable (Product) async -> Bool
	public var observe: @Sendable (Product) -> AsyncStream<Bool>
	public var fetchVariants: @Sendable (Product) async throws -> [ProductVariant]
	public var enableVariant: @Sendable (ProductVariant) async throws -> Bool
	public var restore: @Sendable () async throws -> Void

	public init(
		initialize: @escaping @Sendable () -> Void,
		peekIsAvailable: @escaping @Sendable (Product) -> Bool,
		isAvailable: @escaping @Sendable (Product) async -> Bool,
		observe: @escaping @Sendable (Product) -> AsyncStream<Bool>,
		fetchVariants: @escaping @Sendable (Product) async throws -> [ProductVariant],
		enableVariant: @escaping @Sendable (ProductVariant) async throws -> Bool,
		restore: @escaping @Sendable () async throws -> Void
	) {
		self.initialize = initialize
		self.peekIsAvailable = peekIsAvailable
		self.isAvailable = isAvailable
		self.observe = observe
		self.fetchVariants = fetchVariants
		self.enableVariant = enableVariant
		self.restore = restore
	}
}

extension ProductsService: TestDependencyKey {
	public static var testValue: Self {
		Self(
			initialize: { unimplemented("\(Self.self).initialize") },
			peekIsAvailable: { _ in unimplemented("\(Self.self).peekIsAvailable") },
			isAvailable: { _ in unimplemented("\(Self.self).isAvailable") },
			observe: { _ in unimplemented("\(Self.self).observe") },
			fetchVariants: { _ in unimplemented("\(Self.self).fetchVariants") },
			enableVariant: { _ in unimplemented("\(Self.self).enableVariant") },
			restore: { unimplemented("\(Self.self).restore") }
		)
	}
}
