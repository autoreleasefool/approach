import FeatureFlagsLibrary
import Foundation

extension FeatureFlag {
	public var isEnabled: Bool {
		#if DEBUG
		stage >= .development
		#else
		let isTestFlight = Bundle.main.appStoreReceiptURL?.lastPathComponent == "sandboxReceipt"
		if isTestFlight {
			return stage >= .test
		} else {
			return stage >= .release
		}
		#endif
	}
}
