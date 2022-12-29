import Combine
import Dependencies
import Foundation
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface

extension NSNotification.Name {
	enum FeatureFlag {
		static let didChange = NSNotification.Name("FeatureFlag.didChange")
	}
}

extension FeatureFlagsService: DependencyKey {
	public static let liveValue: Self = {
		let flagManager = FeatureFlagOverrides()

		@Sendable func isFlagEnabled(flag: FeatureFlag) -> Bool {
			#if DEBUG
			return flagManager.getOverride(flag: flag) ?? flag.isEnabled
			#else
			return flag.isEnabled
			#endif
		}

		@Sendable func areFlagsEnabled(flags: [FeatureFlag]) -> [Bool] {
			#if DEBUG
			let overrides = flagManager.getOverrides(flags: flags)
			return zip(flags, overrides).map { $1 ?? $0.isEnabled }
			#else
			return flags.map(\.isEnabled)
			#endif
		}

		return Self(
			isEnabled: isFlagEnabled(flag:),
			allEnabled: { flags in areFlagsEnabled(flags: flags).allSatisfy { $0 } },
			observe: { flag in
				.init { continuation in
					continuation.yield(isFlagEnabled(flag: flag))

					let cancellable = NotificationCenter.default
						.publisher(for: .FeatureFlag.didChange)
						.filter {
							guard let objectFlag = $0.object as? FeatureFlag else { return false }
							return flag == objectFlag
						}
						.sink { _ in
							continuation.yield(isFlagEnabled(flag: flag))
						}

					continuation.onTermination = { _ in cancellable.cancel() }
				}
			},
			observeAll: { flags in
					.init { continuation in
						continuation.yield(areFlagsEnabled(flags: flags))

						let cancellable = NotificationCenter.default
							.publisher(for: .FeatureFlag.didChange)
							.filter {
								guard let objectFlag = $0.object as? FeatureFlag else { return false }
								return flags.contains(objectFlag)
							}
							.sink { _ in
								continuation.yield(areFlagsEnabled(flags: flags))
							}

						continuation.onTermination = { _ in cancellable.cancel() }
					}
			},
			setEnabled: { flag, enabled in
				flagManager.setOverride(forFlag: flag, enabled: enabled)
				NotificationCenter.default.post(name: .FeatureFlag.didChange, object: flag)
			}
		)
	}()
}

class FeatureFlagOverrides {
	private let queue = DispatchQueue(label: "FeatureFlagsService.FeatureFlagManager", attributes: .concurrent)
	private var queue_overrides: [FeatureFlag: Bool] = [:]

	func setOverride(forFlag flag: FeatureFlag, enabled: Bool?) {
		queue.async { self.queue_overrides[flag] = enabled }
	}

	func getOverride(flag: FeatureFlag) -> Bool? {
		queue.sync(flags: .barrier) { queue_overrides[flag] }
	}

	func getOverrides(flags: [FeatureFlag]) -> [Bool?] {
		queue.sync(flags: .barrier) { flags.map { queue_overrides[$0] } }
	}
}
