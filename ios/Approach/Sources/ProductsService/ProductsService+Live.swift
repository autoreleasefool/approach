import BundlePackageServiceInterface
import ConstantsLibrary
import Dependencies
import ProductsLibrary
import ProductsServiceInterface

extension ProductsService: DependencyKey {
	public static var liveValue: Self {
		let isAvailableCache = LockIsolated<[Product: Bool]>([:])

		return Self(
			initialize: {},
			peekIsAvailable: { product in
				isAvailableCache.value[product] ?? false
			},
			isAvailable: { _ in false },
			observe: { _ in .finished },
			fetchVariants: { _ in [] },
			enableVariant: { _ in false },
			restore: {}
		)
	}
}
