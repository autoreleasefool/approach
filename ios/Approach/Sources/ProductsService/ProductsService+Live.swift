import BundlePackageServiceInterface
import ConstantsLibrary
import Dependencies
import ProductsLibrary
import ProductsServiceInterface
import RevenueCat

extension ProductsService: DependencyKey {
	public static var liveValue: Self {
		let isAvailableCache = LockIsolated<[Product: Bool]>([:])

		@Sendable func updateCache(forProducts products: [Product], withEntitlements entitlements: EntitlementInfos) {
			isAvailableCache.withValue {
				for product in products {
					let isAvailable = entitlements[product.name]?.isActive == true
					$0[product] = isAvailable
				}
			}
		}

		return Self(
			initialize: {
				@Dependency(\.bundle) var bundle
				let apiKey = bundle.object(forInfoDictionaryKey: "REVENUE_CAT_API_KEY") as? String

				guard let apiKey, !apiKey.isEmpty else {
					print("Products disabled")
					return
				}

				#if DEBUG
				Purchases.logLevel = .debug
				#endif

				Purchases.configure(
					with: Configuration.Builder(withAPIKey: apiKey)
				)

				Task.detached {
					do {
						let customerInfo = try await Purchases.shared.customerInfo()
						updateCache(forProducts: Product.allCases, withEntitlements: customerInfo.entitlements)
						// Ignore error below since we're just trying to optimistically cache values and doesn't matter if it fails
					} catch {}
				}
			},
			peekIsAvailable: { product in
				isAvailableCache.value[product] ?? false
			},
			isAvailable: { product in
				do {
					let customerInfo = try await Purchases.shared.customerInfo()
					updateCache(forProducts: [product], withEntitlements: customerInfo.entitlements)
					return customerInfo.entitlements[product.name]?.isActive == true
				} catch {
					// TODO: handle error checking for product, report to TelemetryDeck
				}

				return false
			},
			observe: { product in
				.init { continuation in
					let task = Task {
						for try await customerInfo in Purchases.shared.customerInfoStream {
							updateCache(forProducts: [product], withEntitlements: customerInfo.entitlements)
							continuation.yield(customerInfo.entitlements[product.name]?.isActive == true)
						}
					}
					continuation.onTermination = { _ in task.cancel() }
				}
			},
			fetchVariants: { product in
				let offerings = try await Purchases.shared.offerings()
				guard let packages = offerings.current?.availablePackages else {
					return []
				}

				return packages.map {
					product.buildVariant(withName: $0.identifier)
				}
			},
			enableVariant: { variant in
				let offerings = try await Purchases.shared.offerings()
				guard let packages = offerings.current?.availablePackages else {
					return false
				}

				guard let packageToPurchase = packages.first(where: { $0.identifier == variant.name }) else {
					return false
				}

				let result = try await Purchases.shared.purchase(package: packageToPurchase)
				return result.customerInfo.entitlements[variant.product.name]?.isActive == true
			},
			restore: {
				_ = try await Purchases.shared.restorePurchases()
			}
		)
	}
}
