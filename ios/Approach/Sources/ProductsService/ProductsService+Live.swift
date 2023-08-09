import ConstantsLibrary
import Dependencies
import ProductsServiceInterface
import RevenueCat

extension ProductsService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			initialize: {
				let apiKey = AppConstants.ApiKey.revenueCat

				Purchases.logLevel = .debug
				Purchases.configure(
					with: Configuration.Builder(withAPIKey: apiKey)
				)
			},
			isAvailable: { product in
				do {
					let customerInfo = try await Purchases.shared.customerInfo()
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
	}()
}
