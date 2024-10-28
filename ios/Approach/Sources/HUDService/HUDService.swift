import Dependencies
import Foundation
import HUDServiceInterface

extension HUDService: DependencyKey {
	public static var liveValue: HUDService {
		let huds = LockIsolated<[AnyHashableSendable: HUDStyle]>([:])
		let observers = LockIsolated<[UUID: AsyncStream<HUDStatus>.Continuation]>([:])

		@MainActor
		@Sendable
		func refreshObservers() async {
			let styles = Set(huds.values)
			let status: HUDStatus = styles.isEmpty ? .hide : .show(styles)
			for observer in observers.values {
				observer.yield(status)
			}
		}

		return HUDService(
			hudStatusNotifications: {
				@Dependency(\.uuid) var uuid
				let (stream, continuation) = AsyncStream<HUDStatus>.makeStream()
				let id = uuid()
				observers.withValue { $0[id] = continuation }
				continuation.onTermination = { _ in
					observers.withValue { $0[id] = nil }
				}
				return stream
			},
			requestHUD: { id, style in
				huds.withValue {
					$0[id] = style
				}

				await refreshObservers()
			},
			dismissHUD: { id in
				huds.withValue {
					$0[id] = nil
				}

				await refreshObservers()
			}
		)
	}
}
